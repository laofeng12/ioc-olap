package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCubeTableRelation;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * tt数据库访问层
 * @author x_pc
 *
 */
public interface OlapCubeTableRelationRepository extends DynamicJpaRepository<OlapCubeTableRelation, Long>, OlapCubeTableRelationRepositoryCustom{

    List<OlapCubeTableRelation> findByCubeId(Long cubeId);
}
