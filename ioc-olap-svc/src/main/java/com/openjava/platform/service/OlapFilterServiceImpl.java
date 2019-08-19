package com.openjava.platform.service;

import java.util.*;
import javax.annotation.Resource;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.domain.OlapFilterCondidion;
import com.openjava.platform.mapper.kylin.CubeDescDataMapper;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.query.OlapFilterDBParam;
import com.openjava.platform.repository.OlapFilterRepository;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 构建过滤业务层
 * @author zy
 *
 */
@Service
@Transactional
public class OlapFilterServiceImpl implements OlapFilterService {

	@Resource
	private OlapFilterCondidionService olapFilterCondidionService;

	@Resource
	private OlapFilterRepository olapFilterRepository;

	public Page<OlapFilter> query(OlapFilterDBParam params, Pageable pageable){
		Page<OlapFilter> pageresult = olapFilterRepository.query(params, pageable);
		return pageresult;
	}

	public List<OlapFilter> queryDataOnly(OlapFilterDBParam params, Pageable pageable){
		return olapFilterRepository.queryDataOnly(params, pageable);
	}

	public OlapFilter findTableInfo(String cubeName, Long createId) {
		Optional<OlapFilter> o = olapFilterRepository.findTableInfo(cubeName, createId);
		if (o.isPresent()) {
			OlapFilter m = o.get();
			return m;
		}
		return null;
	}

	public OlapFilter get(Long id) {
		Optional<OlapFilter> o = olapFilterRepository.findById(id);
		if(o.isPresent()) {
			OlapFilter m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapFilter："+id);
		return null;
	}

	public OlapFilter doSave(OlapFilter m) {
		return olapFilterRepository.save(m);
	}

	public void doDelete(Long id) {
		olapFilterRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapFilterRepository.deleteById(new Long(items[i]));
		}
	}


	//保存过滤
	public List<OlapFilter> filter(CubeDescMapper cube, List<OlapFilterCondidion> filterCondidionList, Date
			date, OaUserVO userVO) {
		CubeDescDataMapper cubeDescData = cube.getCubeDescData();
		SequenceService ss = ConcurrentSequence.getInstance();
		Long filterId = ss.getSequence();
		String filterSql = "";//过滤最终形成的sql
		List<OlapFilter> filterList = new ArrayList<>();


		//保存过滤主表
		OlapFilter filter = new OlapFilter();

		//根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据并修改olap_filter表数据,同时删除olap_filter_condidion的数据
		if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
			OlapFilter olapFilter = findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
			filterId = olapFilter.getId();
			filter.setUpdateId(Long.parseLong(userVO.getUserId()));
			filter.setUpdateName(userVO.getUserAccount());
			filter.setUpdateTime(date);
			filter.setIsNew(false);
			olapFilterCondidionService.deleteCubeId(filterId);
		} else {
			filter.setCreateTime(date);//创建时间
			filter.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
			filter.setCreateName(userVO.getUserAccount());//创建人名称
			filter.setIsNew(true);
		}
		filter.setId(filterId);
		filter.setCubeName(cubeDescData.getName());//立方体名称

		//保存过滤条件
		for (OlapFilterCondidion fc : filterCondidionList) {
			OlapFilterCondidion filterCondion = new OlapFilterCondidion();
			filterCondion.setId(ss.getSequence());
			filterCondion.setFilterId(filterId);            //过滤表ID
			filterCondion.setTablename(fc.getTablename());  //表名称
			filterCondion.setField(fc.getField());          //表字段
			filterCondion.setPattern(fc.getPattern());      //过滤方式
			filterCondion.setParameter(fc.getParameter());  //过滤值

			if (fc.getPattern().equals("BETWEEN")) {
				filterCondion.setParameterbe(fc.getParameterbe());  //ETWEEN过滤值

				filterSql += "select * from " + fc.getTablename() + "" +
						" where " + fc.getField() + " " +
						"   " + fc.getPattern() + "" +
						"   '" + fc.getParameter() + "' " +
						"   and  " +
						"   '" + fc.getParameterbe() + "' " +
						",";

			} else {
				filterSql += "select * from " + fc.getTablename() + "" +
						" where " + fc.getField() + " " +
						"   " + fc.getPattern() + " " +
						"   '" + fc.getParameter() + "' " +
						",";
			}
			filterCondion.setIsNew(true);
			olapFilterCondidionService.doSave(filterCondion);
		}
		filterSql = filterSql.substring(0, filterSql.length() - 1);
		filter.setFilterSql(filterSql);    //过滤最终形成的sql
		filterList.add(filter);

		return filterList;
	}
}
