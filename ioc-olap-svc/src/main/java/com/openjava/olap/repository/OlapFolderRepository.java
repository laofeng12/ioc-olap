package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapFolder;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 文件夹表数据库访问层
 *
 * @author xiepc
 */
public interface OlapFolderRepository extends DynamicJpaRepository<OlapFolder, Long>, OlapFolderRepositoryCustom {
    public List<OlapFolder> findByCreateIdOrderBySortNumDesc(Long userId);

    List<OlapFolder> findByCreateIdAndTypeOrderBySortNumDesc(Long userId, String type);

    @Query(value = "select t.* from olap_folder t where t.name=:name and t.id!=:folderId and t.create_id=:userId", nativeQuery = true)
     List<OlapFolder> findByName(@Param("name") String name, @Param("folderId") Long folderId, @Param("userId") Long userId);
}
