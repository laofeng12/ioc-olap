package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapDatalaketable;
import com.openjava.platform.domain.OlapShare;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建选择表数据库访问层
 * @author zy
 *
 */
public interface OlapDatalaketableRepository extends DynamicJpaRepository<OlapDatalaketable, Long>, OlapDatalaketableRepositoryCustom{
    @Query(value = "select * from OLAP_DATALAKETABLE t where t.CUBE_NAME=:cubeName)", nativeQuery = true)
    ArrayList<OlapDatalaketable> getListByCubeName(@Param("cubeName") String cubeName);
}
