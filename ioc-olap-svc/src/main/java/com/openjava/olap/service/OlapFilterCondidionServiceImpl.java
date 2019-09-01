package com.openjava.olap.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.olap.domain.OlapFilterCondidion;
import com.openjava.olap.query.OlapFilterCondidionDBParam;
import com.openjava.olap.repository.OlapFilterCondidionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 过滤条件业务层
 * @author zy
 *
 */
@Service
@Transactional
public class OlapFilterCondidionServiceImpl implements OlapFilterCondidionService {

	@Resource
	private OlapFilterCondidionRepository olapFilterCondidionRepository;

	public Page<OlapFilterCondidion> query(OlapFilterCondidionDBParam params, Pageable pageable){
		Page<OlapFilterCondidion> pageresult = olapFilterCondidionRepository.query(params, pageable);
		return pageresult;
	}

	public List<OlapFilterCondidion> queryDataOnly(OlapFilterCondidionDBParam params, Pageable pageable){
		return olapFilterCondidionRepository.queryDataOnly(params, pageable);
	}

	public OlapFilterCondidion get(Long id) {
		Optional<OlapFilterCondidion> o = olapFilterCondidionRepository.findById(id);
		if(o.isPresent()) {
			OlapFilterCondidion m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapFilterCondidion："+id);
		return null;
	}

	public OlapFilterCondidion doSave(OlapFilterCondidion m) {
		return olapFilterCondidionRepository.save(m);
	}

	public void doDelete(Long id) {
		olapFilterCondidionRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapFilterCondidionRepository.deleteById(new Long(items[i]));
		}
	}
	public void deleteCubeId(Long filterId) {
		olapFilterCondidionRepository.deleteCubeId(filterId);
	}
}
