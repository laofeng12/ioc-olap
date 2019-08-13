package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.query.OlapCubeDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapCubeService {
	Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable);

	OlapCube findTableInfo(String cubeName, Long createId);

	OlapCube findByCubeName(String cubeName);

	List<OlapCube> queryDataOnly(OlapCubeDBParam params, Pageable pageable);
	
	OlapCube get(Long id);
	
	OlapCube doSave(OlapCube m);
	
	void doDelete(Long id);
	void doRemove(String ids);

	void updateFlags(String name,Integer flags);

    ArrayList<OlapCube> getListByUserId(Long userId);
}
