package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.query.OlapFilterDBParam;
import com.openjava.platform.repository.OlapFilterRepository;
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
}
