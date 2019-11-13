package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapRealQuery;
import com.openjava.olap.query.OlapRealQueryDBParam;
import org.ljdp.component.exception.APIException;
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

	OlapRealQuery get(Long id) throws APIException;

	OlapRealQuery doSave(OlapRealQuery m);

	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapRealQuery> getListWithFolderId(Long folderId);

	List<OlapRealQuery> getAllShares(Long userId);
}
