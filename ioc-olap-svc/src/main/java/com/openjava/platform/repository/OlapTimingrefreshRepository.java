package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapTimingrefresh;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;


/**
 * 构建定时任务数据库访问层
 * @author zy
 *
 */
public interface OlapTimingrefreshRepository extends DynamicJpaRepository<OlapTimingrefresh, Long>, OlapTimingrefreshRepositoryCustom{
	
}
