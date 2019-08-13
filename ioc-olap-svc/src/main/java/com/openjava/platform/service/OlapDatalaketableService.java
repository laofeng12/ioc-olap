package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapDatalaketable;
import com.openjava.platform.query.OlapDatalaketableDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 构建选择表业务层接口
 * @author zy
 *
 */
public interface OlapDatalaketableService {
	Page<OlapDatalaketable> query(OlapDatalaketableDBParam params, Pageable pageable);
	
	List<OlapDatalaketable> queryDataOnly(OlapDatalaketableDBParam params, Pageable pageable);
	
	OlapDatalaketable get(Long id);
	
	OlapDatalaketable doSave(OlapDatalaketable m);
	
	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapDatalaketable> getListByCubeName(String cubeName);
}
