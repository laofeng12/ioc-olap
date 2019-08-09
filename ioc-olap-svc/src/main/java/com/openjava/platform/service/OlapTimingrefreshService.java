package com.openjava.platform.service;

import java.util.Date;
import java.util.List;

import com.openjava.platform.domain.OlapAnalyze;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.domain.OlapTimingrefresh;
import com.openjava.platform.query.OlapTimingrefreshDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 构建定时任务业务层接口
 * @author zy
 *
 */
public interface OlapTimingrefreshService {
	Page<OlapTimingrefresh> query(OlapTimingrefreshDBParam params, Pageable pageable);
	
	List<OlapTimingrefresh> queryDataOnly(OlapTimingrefreshDBParam params, Pageable pageable);

	List<OlapTimingrefresh> findTiming(int frequencyType);

	OlapTimingrefresh findTableInfo(String cubeName, Long createId);

	OlapTimingrefresh get(Long id);
	
	OlapTimingrefresh doSave(OlapTimingrefresh m);
	
	void doDelete(Long id);
	void doRemove(String ids);
}
