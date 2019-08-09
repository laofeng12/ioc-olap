package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapFilterCondidion;
import com.openjava.platform.query.OlapFilterCondidionDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 过滤条件业务层接口
 * @author zy
 *
 */
public interface OlapFilterCondidionService {
	Page<OlapFilterCondidion> query(OlapFilterCondidionDBParam params, Pageable pageable);
	
	List<OlapFilterCondidion> queryDataOnly(OlapFilterCondidionDBParam params, Pageable pageable);
	
	OlapFilterCondidion get(Long id);
	
	OlapFilterCondidion doSave(OlapFilterCondidion m);
	
	void doDelete(Long id);
	void doRemove(String ids);
	void deleteCubeId(Long filterId);
}
