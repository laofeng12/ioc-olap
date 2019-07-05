package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCubeTable;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapCubeTableRepository extends DynamicJpaRepository<OlapCubeTable, Long>, OlapCubeTableRepositoryCustom{

    ArrayList<OlapCubeTable> findByCubeId(Long cubeId);
}
