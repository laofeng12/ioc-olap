package com.openjava.platform.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.openjava.platform.common.StringUtils;
import com.openjava.platform.domain.*;
import com.openjava.platform.dto.TableInnerDto;
import com.openjava.platform.query.OlapAnalyzeDBParam;
import com.openjava.platform.repository.*;
import com.openjava.platform.vo.AnalyzeAxisVo;
import com.openjava.platform.vo.AnalyzeVo;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件夹表业务层
 * @author xiepc
 *
 */
@Service
@Transactional
public class OlapAnalyzeServiceImpl implements OlapAnalyzeService {
	
	@Resource
	private OlapAnalyzeRepository olapAnalyzeRepository;

	@Resource
	private OlapCubeTableRelationRepository olapCubeTableRelationRepository;

	@Resource
	private OlapAnalyzeAxisRepository olapAnalyzeAxisRepository;

	@Resource
	private OlapAnalyzeAxisFilterRepository olapAnalyzeAxisFilterRepository;

	@Resource
	private OlapCubeTableRepository olapCubeTableRepository;

	@Resource
	private OlapCubeTableColumnRepository olapCubeTableColumnRepository;

	public Page<OlapAnalyze> query(OlapAnalyzeDBParam params, Pageable pageable){
		Page<OlapAnalyze> pageresult = olapAnalyzeRepository.query(params, pageable);
		return pageresult;
	}

	public List<OlapAnalyze> queryDataOnly(OlapAnalyzeDBParam params, Pageable pageable){
		return olapAnalyzeRepository.queryDataOnly(params, pageable);
	}

	public OlapAnalyze get(Long id) {
		Optional<OlapAnalyze> o = olapAnalyzeRepository.findById(id);
		if(o.isPresent()) {
			OlapAnalyze m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapAnalyze："+id);
		return null;
	}

	public OlapAnalyze doSave(OlapAnalyze m) {
		return olapAnalyzeRepository.save(m);
	}

	public void doDelete(Long id) {
		olapAnalyzeRepository.deleteById(id);
	}

	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapAnalyzeRepository.deleteById(new Long(items[i]));
		}
	}

	@Override
	public List<OlapAnalyze> getListWithFolderId(Long folderId) {
		return olapAnalyzeRepository.findByFolderId(folderId);
	}

	@Override
	public List<OlapAnalyze> getAllShares(Long userId) {
		return olapAnalyzeRepository.getAllShares(userId);
	}

	private void writeJoinTable(OlapCubeTable dict, List<OlapCubeTableRelation> relations, ArrayList<TableInnerDto> joinTables, List<OlapCubeTable> cubeTables, Long tableId) throws APIException {
		OlapCubeTableRelation relation=relations.stream().filter((p)-> p.getJoinTableId().equals(tableId)).findFirst().get();;
		if(relation==null){
			throw new APIException("结构错误！");
		}
		if(relation.getTableId()!=dict.getCubeTableId()){
			writeJoinTable(dict,relations,joinTables,cubeTables,relation.getTableId());
		}
		if(joinTables.stream().filter((p)->p.getJoinTableId().equals(tableId)).count()==0)
		{
			OlapCubeTable joinTable=cubeTables.stream().filter((p)->p.getCubeTableId().equals(tableId)).findFirst().get();
			OlapCubeTable sourceTable=cubeTables.stream().filter((p)->p.getCubeTableId().equals(relation.getTableId())).findFirst().get();
			TableInnerDto innerDto=new TableInnerDto();
			innerDto.setJoinTable(joinTable.getTableName());
			innerDto.setJoinTableAlias(joinTable.getTableAlias());
			innerDto.setJoinTableId(tableId);
			innerDto.setJoinType(relation.getJoinType());
			innerDto.setSourceTableAlias(sourceTable.getTableAlias());
			innerDto.setJoinFkKeys(relation.getFkKey().split(","));
			innerDto.setJoinPkKeys(relation.getPkKey().split(","));
			innerDto.setDatabaseName(joinTable.getDatabaseName());
		}
	}

	private String builderSql(ArrayList<String> selectColumns,ArrayList<TableInnerDto> joinTables,ArrayList<String> filters,ArrayList<String> groups,OlapCubeTable dict)
	{
		List<String> joinTableSqls=joinTables.stream().map((p)->{
			String joinOn="",fk,pk;
			for (Integer i=0;i<p.getJoinPkKeys().length;i++){
				pk=p.getJoinPkKeys()[i];
				fk=p.getJoinFkKeys()[i];
				if(!StringUtils.isNullOrEmpty(pk)){
					joinOn+=p.getSourceTableAlias()+"."+fk+"="+p.getJoinTableAlias()+"."+pk;
					if(i<p.getJoinPkKeys().length-1){
						joinOn+=" and ";
					}
				}
			}
			return p.getJoinType()+" "+p.getJoinTable()+" as "+p.getJoinTableAlias()+" on "+joinOn;
		}).collect(Collectors.toList());
		String sql=MessageFormat.format("select {0} from {1}.{2} as {3} {4} where {5} group by {6}",String.join(",",selectColumns),
				dict.getDatabaseName(),dict.getTableName(),dict.getTableAlias(),String.join(" ",joinTableSqls),String.join(" and ",filters),
				String.join(",",groups));
		return sql;
	}

	@Override
	public AnalyzeVo save(AnalyzeVo analyzeVo) throws APIException {
		ArrayList<String> selectColumns=new ArrayList<String>();
		ArrayList<TableInnerDto> joinTables=new ArrayList<TableInnerDto>();
		ArrayList<String> filters=new ArrayList<String>();
		ArrayList<String> groups=new ArrayList<String>();
		String group,column,filter;
		List<OlapCubeTableRelation> relations=olapCubeTableRelationRepository.findByCubeId(analyzeVo.getCubeId());
		List<OlapCubeTable> cubeTables=olapCubeTableRepository.findByCubeId(analyzeVo.getCubeId());
		OlapCubeTable dict=cubeTables.stream().filter(p->p.getIsDict()==1).findFirst().get();
		for (AnalyzeAxisVo axis : analyzeVo.getOlapAnalyzeAxes()){
			// 连接查询构建
			writeJoinTable(dict,relations,joinTables,cubeTables,axis.getTableId());
			// 列名构建
			if(axis.getType()!=4){
				column=MessageFormat.format(axis.getExpressionFull(),axis.getTableAlias(),axis.getColumnName(),axis.getColumnChName());
				if(!selectColumns.contains(column)){
					selectColumns.add(column);
				}

				// 分组语句中构建
				if(axis.getType()==1 || axis.getType()==2){
					group=axis.getTableAlias()+"."+axis.getColumnName();
					if(!groups.contains(group)){
						groups.add(group);
					}
				}
			}
			else{
				// 过滤条件构建
				List<String> values=  Arrays.stream(axis.getSelectValues().split(",")).map((p)->{
					return "'"+p+"'";
				}).collect(Collectors.toList());
				filter="{0}.{1} {2} ({3})";
				if(axis.getIsInclude()==1){
					filter= MessageFormat.format(filter,axis.getTableAlias(),axis.getColumnName(),"in",String.join(",",values));
				}
				else{
					filter= MessageFormat.format(filter,axis.getTableAlias(),axis.getColumnName(),"not in",String.join(",",values));
				}
				filters.add(filter);
			}
		}

		String sql=builderSql(selectColumns,joinTables,filters,groups,dict);

		OlapAnalyze analyze=new OlapAnalyze();
		MyBeanUtils.copyPropertiesNotBlank(analyze, analyzeVo);
		analyze.setSql(sql);
		olapAnalyzeRepository.save(analyze);
		for (AnalyzeAxisVo axis : analyzeVo.getOlapAnalyzeAxes()){
			OlapAnalyzeAxis analyzeAxis=new OlapAnalyzeAxis();
			MyBeanUtils.copyPropertiesNotBlank(analyzeAxis, axis);
			analyzeAxis.setAnalyzeAxisId(ConcurrentSequence.getInstance().getSequence());
			analyzeAxis.setAnalyzeId(analyze.getAnalyzeId());
			olapAnalyzeAxisRepository.save(analyzeAxis);
			if(axis.getType()==4){
				OlapAnalyzeAxisFilter analyzeAxisFilter=new OlapAnalyzeAxisFilter();
				analyzeAxisFilter.setAnalyzeAxisFilterId(ConcurrentSequence.getInstance().getSequence());
				analyzeAxisFilter.setAxisId(analyzeAxis.getAnalyzeAxisId());
				analyzeAxisFilter.setIsInclude(axis.getIsInclude());
				analyzeAxisFilter.setSelectValues(axis.getSelectValues());
				olapAnalyzeAxisFilterRepository.save(analyzeAxisFilter);
			}
		}

		return analyzeVo;
	}

	@Override
	public AnalyzeVo getVo(Long id) {
		AnalyzeVo analyzeVo=new AnalyzeVo();
		//获取analyze
		OlapAnalyze olapAnalyze=olapAnalyzeRepository.getOne(id);
		//复制属性到VO中
		MyBeanUtils.copyPropertiesNotBlank(analyzeVo, olapAnalyze);
		//查询子表数据
		List<OlapAnalyzeAxis> analyzeAxes=olapAnalyzeAxisRepository.findByAnalyzeId(id);
		List<OlapCubeTableColumn> cubeTableColumns=olapCubeTableColumnRepository.findByCubeId(id);
		List<OlapCubeTable> cubeTables=olapCubeTableRepository.findByCubeId(analyzeVo.getCubeId());
		analyzeVo.setOlapAnalyzeAxes(new ArrayList<AnalyzeAxisVo>());
		for(OlapAnalyzeAxis axis : analyzeAxes){
			OlapCubeTable cubeTable=cubeTables.stream().filter(p->p.getCubeTableId()==axis.getTableId()).findFirst().get();
			OlapCubeTableColumn cubeTableColumn=cubeTableColumns.stream().filter(p->p.getCubeTableColumnId()==axis.getColumnId()).findFirst().get();
			AnalyzeAxisVo axisVo=new AnalyzeAxisVo();
			MyBeanUtils.copyPropertiesNotBlank(axisVo, axis);
			axisVo.setName(cubeTable.getName());
			axisVo.setCubeId(cubeTable.getCubeId());
			axisVo.setTableName(cubeTable.getTableName());
			axisVo.setTableAlias(cubeTable.getTableAlias());
			axisVo.setIsDict(cubeTable.getIsDict());
			axisVo.setColumnName(cubeTableColumn.getColumnName());
			axisVo.setColumnChName(cubeTableColumn.getName());
			axisVo.setColumnAlias(cubeTableColumn.getColumnAlias());
			axisVo.setExpressionType(cubeTableColumn.getExpressionType());
			axisVo.setExpressionFull(cubeTableColumn.getExpressionFull());
			if(axis.getType()==4){
				OlapAnalyzeAxisFilter axisFilter=olapAnalyzeAxisFilterRepository.findByAxisId(axis.getAnalyzeAxisId());
				axisVo.setIsInclude(axisFilter.getIsInclude());
				axisVo.setSelectValues(axisFilter.getSelectValues());
			}
			analyzeVo.getOlapAnalyzeAxes().add(axisVo);
		}

		return analyzeVo;
	}
}
