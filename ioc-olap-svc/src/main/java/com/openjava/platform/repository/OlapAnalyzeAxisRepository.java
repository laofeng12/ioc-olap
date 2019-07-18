package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapAnalyzeAxis;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapAnalyzeAxisRepository extends DynamicJpaRepository<OlapAnalyzeAxis, Long>, OlapAnalyzeAxisRepositoryCustom{

    List<OlapAnalyzeAxis> findByAnalyzeId(Long id);
}
