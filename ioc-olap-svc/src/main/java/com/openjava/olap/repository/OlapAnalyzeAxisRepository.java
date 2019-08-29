package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapAnalyzeAxis;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapAnalyzeAxisRepository extends DynamicJpaRepository<OlapAnalyzeAxis, Long>, OlapAnalyzeAxisRepositoryCustom{

    List<OlapAnalyzeAxis> findByAnalyzeId(Long id);
    @Modifying
    @Query("delete from OlapAnalyzeAxis where analyzeId=?1")
    void deleteByAnalyzeId(Long analyzeId);
}
