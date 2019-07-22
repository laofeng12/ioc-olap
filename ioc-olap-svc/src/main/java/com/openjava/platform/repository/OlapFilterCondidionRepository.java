package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapFilterCondidion;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;


/**
 * 过滤条件数据库访问层
 * @author zy
 *
 */
public interface OlapFilterCondidionRepository extends DynamicJpaRepository<OlapFilterCondidion, Long>, OlapFilterCondidionRepositoryCustom{
	
}
