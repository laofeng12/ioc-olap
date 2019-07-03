package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapFolder;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapFolderRepository extends DynamicJpaRepository<OlapFolder, Long>, OlapFolderRepositoryCustom{
    public List<OlapFolder> findByCreateIdOrderBySortNumDesc(Long userId);
}
