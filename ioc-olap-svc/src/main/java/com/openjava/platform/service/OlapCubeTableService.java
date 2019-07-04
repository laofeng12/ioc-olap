package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.query.OlapCubeTableDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapCubeTableService {
	Page<OlapCubeTable> query(OlapCubeTableDBParam params, Pageable pageable);
	
	List<OlapCubeTable> queryDataOnly(OlapCubeTableDBParam params, Pageable pageable);
	
	OlapCubeTable get(Long id);
	
	OlapCubeTable doSave(OlapCubeTable m);
	
	void doDelete(Long id);
	void doRemove(String ids);
}
