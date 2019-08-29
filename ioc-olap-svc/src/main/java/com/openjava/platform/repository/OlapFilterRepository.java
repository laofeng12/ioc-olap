package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapFilter;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


/**
 * 构建过滤数据库访问层
 * @author zy
 *
 */
public interface OlapFilterRepository extends DynamicJpaRepository<OlapFilter, Long>, OlapFilterRepositoryCustom{

    @Query(value = "select t.* from OLAP_FILTER t where t.CUBE_NAME=:cubeName and t.CREATE_ID=:createId", nativeQuery = true)
    Optional<OlapFilter> findTableInfo(@Param("cubeName") String cubeName, @Param("createId") Long createId);
}
