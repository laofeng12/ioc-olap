package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapAnalyzeAxisFilter;
import com.openjava.olap.query.OlapAnalyzeAxisFilterDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapAnalyzeAxisFilterService {
	Page<OlapAnalyzeAxisFilter> query(OlapAnalyzeAxisFilterDBParam params, Pageable pageable);

	List<OlapAnalyzeAxisFilter> queryDataOnly(OlapAnalyzeAxisFilterDBParam params, Pageable pageable);

	OlapAnalyzeAxisFilter get(Long id);

	OlapAnalyzeAxisFilter doSave(OlapAnalyzeAxisFilter m);

	void doDelete(Long id);
	void doRemove(String ids);
}
