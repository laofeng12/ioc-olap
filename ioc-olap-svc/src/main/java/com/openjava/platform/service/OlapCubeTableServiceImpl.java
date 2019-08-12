package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.query.OlapCubeTableDBParam;
import com.openjava.platform.repository.OlapCubeTableRepository;
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
public class OlapCubeTableServiceImpl implements OlapCubeTableService {
	
	@Resource
	private OlapCubeTableRepository olapCubeTableRepository;
	
	public Page<OlapCubeTable> query(OlapCubeTableDBParam params, Pageable pageable){
		Page<OlapCubeTable> pageresult = olapCubeTableRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapCubeTable> queryDataOnly(OlapCubeTableDBParam params, Pageable pageable){
		return olapCubeTableRepository.queryDataOnly(params, pageable);
	}
	
	public OlapCubeTable get(Long id) {
		Optional<OlapCubeTable> o = olapCubeTableRepository.findById(id);
		if(o.isPresent()) {
			OlapCubeTable m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapCubeTable："+id);
		return null;
	}
	
	public OlapCubeTable doSave(OlapCubeTable m) {
		return olapCubeTableRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapCubeTableRepository.deleteById(id);
	}

	public void deleteCubeId(Long cubeId) {
		olapCubeTableRepository.deleteCubeId(cubeId);
	}


	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapCubeTableRepository.deleteById(new Long(items[i]));
		}
	}

	@Override
	public ArrayList<OlapCubeTable> getListByCubeId(Long cubeId) {
		return olapCubeTableRepository.findByCubeId(cubeId);
	}
}
