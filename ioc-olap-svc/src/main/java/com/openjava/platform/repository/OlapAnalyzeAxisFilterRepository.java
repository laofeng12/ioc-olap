package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapAnalyzeAxisFilter;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapAnalyzeAxisFilterRepository extends DynamicJpaRepository<OlapAnalyzeAxisFilter, Long>, OlapAnalyzeAxisFilterRepositoryCustom{

    OlapAnalyzeAxisFilter findByAxisId(Long analyzeAxisId);
    @Modifying
    @Query("delete from OlapAnalyzeAxisFilter where analyzeId=?1")
    void deleteByAnalyzeId(Long analyzeId);
}
