package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.query.OlapCubeDBParam;
import com.openjava.platform.repository.OlapCubeRepository;
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
public class OlapCubeServiceImpl implements OlapCubeService {
	
	@Resource
	private OlapCubeRepository olapCubeRepository;
	
	public Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable){
		Page<OlapCube> pageresult = olapCubeRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapCube> queryDataOnly(OlapCubeDBParam params, Pageable pageable){
		return olapCubeRepository.queryDataOnly(params, pageable);
	}

	public OlapCube findTableInfo(String cubeName, Long createId) {
		Optional<OlapCube> o = olapCubeRepository.findTableInfo(cubeName, createId);
		if (o.isPresent()) {
			OlapCube m = o.get();
			return m;
		}
		return null;
	}

	public OlapCube get(Long id) {
		Optional<OlapCube> o = olapCubeRepository.findById(id);
		if(o.isPresent()) {
			OlapCube m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapCube："+id);
		return null;
	}
	
	public OlapCube doSave(OlapCube m) {
		return olapCubeRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapCubeRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapCubeRepository.deleteById(new Long(items[i]));
		}
	}

	@Override
	public ArrayList<OlapCube> getListByUserId(Long userId) {
		return olapCubeRepository.findByCreateId(userId);
	}

	@Override
	public List<OlapCube> findAll() {
		return olapCubeRepository.findAll();
	}
}
