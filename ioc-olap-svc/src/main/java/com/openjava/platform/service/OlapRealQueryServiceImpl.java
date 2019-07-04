package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapRealQuery;
import com.openjava.platform.query.OlapRealQueryDBParam;
import com.openjava.platform.repository.OlapRealQueryRepository;
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
public class OlapRealQueryServiceImpl implements OlapRealQueryService {
	
	@Resource
	private OlapRealQueryRepository olapRealQueryRepository;
	
	public Page<OlapRealQuery> query(OlapRealQueryDBParam params, Pageable pageable){
		Page<OlapRealQuery> pageresult = olapRealQueryRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapRealQuery> queryDataOnly(OlapRealQueryDBParam params, Pageable pageable){
		return olapRealQueryRepository.queryDataOnly(params, pageable);
	}
	
	public OlapRealQuery get(Long id) {
		Optional<OlapRealQuery> o = olapRealQueryRepository.findById(id);
		if(o.isPresent()) {
			OlapRealQuery m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapRealQuery："+id);
		return null;
	}
	
	public OlapRealQuery doSave(OlapRealQuery m) {
		return olapRealQueryRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapRealQueryRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapRealQueryRepository.deleteById(new Long(items[i]));
		}
	}
}
