package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapCubeTableRelation;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * tt数据库访问层
 * @author x_pc
 *
 */
public interface OlapCubeTableRelationRepository extends DynamicJpaRepository<OlapCubeTableRelation, Long>, OlapCubeTableRelationRepositoryCustom{

    List<OlapCubeTableRelation> findByCubeId(Long cubeId);

    @Query(value = "delete from OLAP_CUBE_TABLE_RELATION t where t.CUBE_ID=:cubeId", nativeQuery = true)
    void deleteCubeId(@Param("cubeId") Long cubeId);
}
