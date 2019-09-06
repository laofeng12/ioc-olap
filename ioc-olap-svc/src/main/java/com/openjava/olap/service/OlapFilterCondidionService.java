package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapFilterCondidion;
import com.openjava.olap.query.OlapFilterCondidionDBParam;
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

	List<OlapFilterCondidion> findByFilterId(Long filterId);

	OlapFilterCondidion get(Long id);

	OlapFilterCondidion doSave(OlapFilterCondidion m);

	void doDelete(Long id);
	void doRemove(String ids);
	void deleteFilterId(Long filterId);
}
