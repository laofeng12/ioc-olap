package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.query.OlapShareDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapShareService {
	Page<OlapShare> query(OlapShareDBParam params, Pageable pageable);
	
	List<OlapShare> queryDataOnly(OlapShareDBParam params, Pageable pageable);
	
	OlapShare get(Long id);
	
	OlapShare doSave(OlapShare m);
	
	void doDelete(Long id);
	void doRemove(String ids);
}
