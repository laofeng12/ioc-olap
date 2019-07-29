package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapAnalyzeAxis;
import com.openjava.platform.query.OlapAnalyzeAxisDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapAnalyzeAxisService {
	Page<OlapAnalyzeAxis> query(OlapAnalyzeAxisDBParam params, Pageable pageable);
	
	List<OlapAnalyzeAxis> queryDataOnly(OlapAnalyzeAxisDBParam params, Pageable pageable);
	
	OlapAnalyzeAxis get(Long id);
	
	OlapAnalyzeAxis doSave(OlapAnalyzeAxis m);
	
	void doDelete(Long id);
	void doRemove(String ids);
}
