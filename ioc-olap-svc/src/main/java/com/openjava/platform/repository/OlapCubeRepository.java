package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapCubeRepository extends DynamicJpaRepository<OlapCube, Long>, OlapCubeRepositoryCustom{

    ArrayList<OlapCube> findByCreateId(Long userId);

    ArrayList<OlapCube> findAll();

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME=:cubeName and t.CREATE_ID=:createId", nativeQuery = true)
    Optional<OlapCube> findTableInfo(@Param("cubeName") String cubeName, @Param("createId") Long createId);

}
