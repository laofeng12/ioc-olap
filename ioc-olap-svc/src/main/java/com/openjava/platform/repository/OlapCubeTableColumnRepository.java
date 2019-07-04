package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapCubeTableColumn;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapCubeTableColumnRepository extends DynamicJpaRepository<OlapCubeTableColumn, Long>, OlapCubeTableColumnRepositoryCustom{
	
}
