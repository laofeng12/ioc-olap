package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapCubeTableColumn;
import com.openjava.olap.domain.OlapFilterCondidion;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;


/**
 * 过滤条件数据库访问层
 * @author zy
 *
 */
public interface OlapFilterCondidionRepository extends DynamicJpaRepository<OlapFilterCondidion, Long>, OlapFilterCondidionRepositoryCustom{

    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_FILTER_CONDIDION t where t.FILTER_ID=:filterId", nativeQuery = true)
    void deleteFilterId(@Param("filterId") Long filterId);

    @Query(value = "select * from OLAP_FILTER_CONDIDION t  where t.FILTER_ID=:filterId", nativeQuery = true)
    ArrayList<OlapFilterCondidion> findByFilterId(@Param("filterId") Long filterId);
}
