package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCubeTableRelation;
import com.openjava.platform.query.OlapCubeTableRelationDBParam;
import com.openjava.platform.repository.OlapCubeTableRelationRepository;
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
}
