package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapCubeTableRelation;
import com.openjava.platform.mapper.kylin.CubeDescDataMapper;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.mapper.kylin.LookupsMapper;
import com.openjava.platform.mapper.kylin.ModelsMapper;
import com.openjava.platform.query.OlapCubeTableRelationDBParam;
import com.openjava.platform.repository.OlapCubeTableRelationRepository;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * tt业务层
 * @author x_pc
 *
 */
@Service
@Transactional
public class OlapCubeTableRelationServiceImpl implements OlapCubeTableRelationService {
	
	@Resource
	private OlapCubeTableRelationRepository olapCubeTableRelationRepository;
	
	public Page<OlapCubeTableRelation> query(OlapCubeTableRelationDBParam params, Pageable pageable){
		Page<OlapCubeTableRelation> pageresult = olapCubeTableRelationRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapCubeTableRelation> queryDataOnly(OlapCubeTableRelationDBParam params, Pageable pageable){
		return olapCubeTableRelationRepository.queryDataOnly(params, pageable);
	}
	
	public OlapCubeTableRelation get(Long id) {
		Optional<OlapCubeTableRelation> o = olapCubeTableRelationRepository.findById(id);
		if(o.isPresent()) {
			OlapCubeTableRelation m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapCubeTableRelation："+id);
		return null;
	}
	
	public OlapCubeTableRelation doSave(OlapCubeTableRelation m) {
		return olapCubeTableRelationRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapCubeTableRelationRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapCubeTableRelationRepository.deleteById(new Long(items[i]));
		}
	}
	public void deleteCubeId(Long cubeId) {
		olapCubeTableRelationRepository.deleteCubeId(cubeId);
	}

	//保存OLAP_CUBE_TABLE_RELATION表
	public List<OlapCubeTableRelation> saveCubeTableRelation(CubeDescMapper cube, ModelsMapper models, Long
			cubeId, List<OlapCubeTable> dmEntity) {
		ArrayList<LookupsMapper> modelDescData = models.modelDescData.getLookups();
		CubeDescDataMapper cubeDescData = cube.getCubeDescData();
		SequenceService ss = ConcurrentSequence.getInstance();
		List<OlapCubeTableRelation> olapcubeList = new ArrayList<>();

		//根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除column里的数据
		if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
			deleteCubeId(cubeId);
		}

		for (LookupsMapper lm : modelDescData) {
			OlapCubeTableRelation Relation = new OlapCubeTableRelation();

			String Primary_key = "";
			String Foreign_key = "";
			String Pk_type = StringUtils.join(lm.join.getPk_type(), ",");
			String Fk_type = StringUtils.join(lm.join.getFk_type(), ",");

			String tableName = lm.getTable().substring(lm.getTable().indexOf(".") + 1);
			//去除关联表里每一个列名前面的表别名
			for (String pk : lm.join.getPrimary_key()) {
				Primary_key += pk.substring(pk.indexOf(".") + 1) + ",";
			}
			Primary_key = Primary_key.substring(0, Primary_key.length() - 1);

			for (String fk : lm.join.getForeign_key()) {
				Foreign_key += fk.substring(fk.indexOf(".") + 1) + ",";
			}
			Foreign_key = Foreign_key.substring(0, Foreign_key.length() - 1);


			Optional<OlapCubeTable> tableId = dmEntity.stream()
					.filter(p -> p.getTableAlias().equals(lm.getAlias())).findFirst();

			Optional<OlapCubeTable> joinTableId = dmEntity.stream()
					.filter(p -> p.getTableAlias().equals(lm.getJoinTable())).findFirst();

			Relation.setId(ss.getSequence());
			Relation.setTableId(joinTableId.get().getId()); //源表id
			Relation.setJoinTableId(tableId.get().getId()); //关联表ID
			Relation.setJoinType(lm.join.getType());            //关联类型
			Relation.setPkKey(Primary_key);                     //主键列名称
			Relation.setFkKey(Foreign_key);                     //外键列名称
			Relation.setPkDataType(Pk_type);                    //主键数据类型
			Relation.setFkDataType(Fk_type);                    //外键数据类型
			Relation.setCubeId(cubeId);                         //立方体ID
			Relation.setIsNew(true);
			olapcubeList.add(Relation);
		}
		return olapcubeList;
	};

}
