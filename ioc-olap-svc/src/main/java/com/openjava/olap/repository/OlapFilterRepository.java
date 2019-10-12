package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapFilter;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;


/**
 * 构建过滤数据库访问层
 * @author zy
 *
 */
public interface OlapFilterRepository extends DynamicJpaRepository<OlapFilter, Long>, OlapFilterRepositoryCustom{
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_FILTER t where t.CUBE_NAME=:cubeName", nativeQuery = true)
    void deleteCubeName(@Param("cubeName") String cubeName);

    @Query(value = "select t.* from OLAP_FILTER t where t.CUBE_NAME=:cubeName", nativeQuery = true)
    Optional<OlapFilter> findTableInfo(@Param("cubeName") String cubeName);
}
