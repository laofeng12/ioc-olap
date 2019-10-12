package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapFolder;
import com.openjava.olap.query.OlapFolderDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 *
 * @author xiepc
 */
public interface OlapFolderService {
    Page<OlapFolder> query(OlapFolderDBParam params, Pageable pageable);

    List<OlapFolder> queryDataOnly(OlapFolderDBParam params, Pageable pageable);

    OlapFolder get(Long id);

    OlapFolder doSave(OlapFolder m);

    void doDelete(Long id);

    void doRemove(String ids);

    List<OlapFolder> getListByCreateId(Long userId);

    List<OlapFolder> getListByTypeAndCreateId(Long userId, String type);

    boolean checkExsitName(String name, Long userId);

    boolean checkExsitName(String name, Long folderId, Long userId);
}
