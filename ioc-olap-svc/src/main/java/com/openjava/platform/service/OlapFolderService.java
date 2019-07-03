package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapFolder;
import com.openjava.platform.query.OlapFolderDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapFolderService {
	Page<OlapFolder> query(OlapFolderDBParam params, Pageable pageable);
	
	List<OlapFolder> queryDataOnly(OlapFolderDBParam params, Pageable pageable);
	
	OlapFolder get(Long id);
	
	OlapFolder doSave(OlapFolder m);
	
	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapFolder> getListByCreateId(Long userId);
}
