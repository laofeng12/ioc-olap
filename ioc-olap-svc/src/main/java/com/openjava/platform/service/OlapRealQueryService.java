package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapRealQuery;
import com.openjava.platform.query.OlapRealQueryDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapRealQueryService {
	Page<OlapRealQuery> query(OlapRealQueryDBParam params, Pageable pageable);
	
	List<OlapRealQuery> queryDataOnly(OlapRealQueryDBParam params, Pageable pageable);
	
	OlapRealQuery get(Long id);
	
	OlapRealQuery doSave(OlapRealQuery m);
	
	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapRealQuery> getListWithFolderId(Long folderId);

	List<OlapRealQuery> getAllShares(Long userId);
}
