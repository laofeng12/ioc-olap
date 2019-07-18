package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapAnalyzeAxisFilter;
import com.openjava.platform.query.OlapAnalyzeAxisFilterDBParam;
import com.openjava.platform.repository.OlapAnalyzeAxisFilterRepository;
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
public class OlapAnalyzeAxisFilterServiceImpl implements OlapAnalyzeAxisFilterService {
	
	@Resource
	private OlapAnalyzeAxisFilterRepository olapAnalyzeAxisFilterRepository;
	
	public Page<OlapAnalyzeAxisFilter> query(OlapAnalyzeAxisFilterDBParam params, Pageable pageable){
		Page<OlapAnalyzeAxisFilter> pageresult = olapAnalyzeAxisFilterRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapAnalyzeAxisFilter> queryDataOnly(OlapAnalyzeAxisFilterDBParam params, Pageable pageable){
		return olapAnalyzeAxisFilterRepository.queryDataOnly(params, pageable);
	}
	
	public OlapAnalyzeAxisFilter get(Long id) {
		Optional<OlapAnalyzeAxisFilter> o = olapAnalyzeAxisFilterRepository.findById(id);
		if(o.isPresent()) {
			OlapAnalyzeAxisFilter m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapAnalyzeAxisFilter："+id);
		return null;
	}
	
	public OlapAnalyzeAxisFilter doSave(OlapAnalyzeAxisFilter m) {
		return olapAnalyzeAxisFilterRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapAnalyzeAxisFilterRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapAnalyzeAxisFilterRepository.deleteById(new Long(items[i]));
		}
	}
}
