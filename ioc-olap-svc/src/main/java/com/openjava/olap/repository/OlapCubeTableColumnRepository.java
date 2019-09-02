package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapCubeTableColumn;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹表数据库访问层
 *
 * @author xiepc
 */
public interface OlapCubeTableColumnRepository extends DynamicJpaRepository<OlapCubeTableColumn, Long>, OlapCubeTableColumnRepositoryCustom {

    ArrayList<OlapCubeTableColumn> findByTableId(Long cubeTableId);

    List<OlapCubeTableColumn> findByCubeId(Long id);

    @Query(value = "delete from OLAP_CUBE_TABLE_COLUMN t where t.CUBE_ID=:cubeId", nativeQuery = true)
    void deleteCubeId(@Param("cubeId") Long cubeId);

    @Query(value = "select * from OLAP_CUBE_TABLE_COLUMN t  where t.CUBE_ID  in  (select b.ID from OLAP_CUBE b where b.NAME=:cubeName)", nativeQuery = true)
    ArrayList<OlapCubeTableColumn> findByColumn(@Param("cubeName") String cubeName);

    @Query(value = "select * from OLAP_CUBE_TABLE_COLUMN t  where t.TABLE_ID=:tableId", nativeQuery = true)
    ArrayList<OlapCubeTableColumn> findByCubeTableId(@Param("tableId") Long tableId);
}
