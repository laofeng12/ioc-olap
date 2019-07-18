package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapAnalyzeAxis;
import com.openjava.platform.query.OlapAnalyzeAxisDBParam;
import com.openjava.platform.repository.OlapAnalyzeAxisRepository;
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
public class OlapAnalyzeAxisServiceImpl implements OlapAnalyzeAxisService {
	
	@Resource
	private OlapAnalyzeAxisRepository olapAnalyzeAxisRepository;
	
	public Page<OlapAnalyzeAxis> query(OlapAnalyzeAxisDBParam params, Pageable pageable){
		Page<OlapAnalyzeAxis> pageresult = olapAnalyzeAxisRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapAnalyzeAxis> queryDataOnly(OlapAnalyzeAxisDBParam params, Pageable pageable){
		return olapAnalyzeAxisRepository.queryDataOnly(params, pageable);
	}
	
	public OlapAnalyzeAxis get(Long id) {
		Optional<OlapAnalyzeAxis> o = olapAnalyzeAxisRepository.findById(id);
		if(o.isPresent()) {
			OlapAnalyzeAxis m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapAnalyzeAxis："+id);
		return null;
	}
	
	public OlapAnalyzeAxis doSave(OlapAnalyzeAxis m) {
		return olapAnalyzeAxisRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapAnalyzeAxisRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapAnalyzeAxisRepository.deleteById(new Long(items[i]));
		}
	}
}
