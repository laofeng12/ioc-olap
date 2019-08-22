package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapCubeTableColumn;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 文件夹表数据库访问层
 *
 * @author xiepc
 */
public interface OlapCubeTableRepository extends DynamicJpaRepository<OlapCubeTable, Long>, OlapCubeTableRepositoryCustom {

    ArrayList<OlapCubeTable> findByCubeId(Long cubeId);


    @Query(value = "delete from OLAP_CUBE_TABLE t where t.CUBE_ID=:cubeId)", nativeQuery = true)
    void deleteCubeId(@Param("cubeId") Long cubeId);

    @Query(value = "select * from OLAP_CUBE_TABLE t  where t.CUBE_ID  in  (select b.ID from OLAP_CUBE b where b.NAME=:cubeName))", nativeQuery = true)
    ArrayList<OlapCubeTable> findByTable(@Param("cubeName") String cubeName);
}
