package com.openjava.olap.service;

import java.util.List;

import com.openjava.olap.domain.OlapShare;
import com.openjava.olap.dto.ShareUserDto;
import com.openjava.olap.query.OlapShareDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 *
 * @author xiepc
 */
public interface OlapShareService {
    Page<OlapShare> query(OlapShareDBParam params, Pageable pageable);

    List<OlapShare> queryDataOnly(OlapShareDBParam params, Pageable pageable);

    OlapShare get(Long id);

    OlapShare doSave(OlapShare m);

    void doDelete(Long id);

    void doRemove(String ids);

    void save(Long[] shareUserIds, String sourceType, Long sourceId, Long userId, String userName);

    void save(Long[] shareUserIds, String sourceType, Long sourceId, Long userId, String userName, String cubeName);

    List<ShareUserDto> getList(String sourceType, String sourceId, Long userId);

    List<ShareUserDto> getList(String sourceType, String sourceId, Long userId, String cubeName);
}
