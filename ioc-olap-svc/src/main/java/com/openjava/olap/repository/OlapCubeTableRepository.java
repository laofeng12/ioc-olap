package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapCubeTable;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;

/**
 * 文件夹表数据库访问层
 *
 * @author xiepc
 */
public interface OlapCubeTableRepository extends DynamicJpaRepository<OlapCubeTable, Long>, OlapCubeTableRepositoryCustom {
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_CUBE_TABLE t where t.CUBE_ID=:cubeId", nativeQuery = true)
    void deleteCubeId(@Param("cubeId") Long cubeId);

    ArrayList<OlapCubeTable> findByCubeId(Long cubeId);

    @Query(value = "select * from OLAP_CUBE_TABLE t  where t.CUBE_ID  in  (select b.ID from OLAP_CUBE b where b.NAME=:cubeName)", nativeQuery = true)
    ArrayList<OlapCubeTable> findByTable(@Param("cubeName") String cubeName);
}
