package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapCubeTable;
import com.openjava.olap.domain.OlapCubeTableRelation;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.mapper.kylin.ModelsMapper;
import com.openjava.olap.query.OlapCubeTableRelationDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * tt业务层接口
 * @author x_pc
 *
 */
public interface OlapCubeTableRelationService {
	Page<OlapCubeTableRelation> query(OlapCubeTableRelationDBParam params, Pageable pageable);

	List<OlapCubeTableRelation> queryDataOnly(OlapCubeTableRelationDBParam params, Pageable pageable);

	OlapCubeTableRelation get(Long id);

	OlapCubeTableRelation doSave(OlapCubeTableRelation m);

	void doDelete(Long id);
	void doRemove(String ids);
	void deleteCubeId(Long cubeId);

	 List<OlapCubeTableRelation> saveCubeTableRelation(CubeDescMapper cube, ModelsMapper models, Long
			cubeId, List<OlapCubeTable> dmEntity);
}
