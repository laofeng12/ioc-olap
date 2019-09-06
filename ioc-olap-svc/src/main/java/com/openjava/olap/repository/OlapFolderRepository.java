package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapFolder;
import org.ljdp.core.spring.data.DynamicJpaRepository;

import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapFolderRepository extends DynamicJpaRepository<OlapFolder, Long>, OlapFolderRepositoryCustom{
    public List<OlapFolder> findByCreateIdOrderBySortNumDesc(Long userId);

    List<OlapFolder> findByCreateIdAndTypeOrderBySortNumDesc(Long userId, String type);
}
