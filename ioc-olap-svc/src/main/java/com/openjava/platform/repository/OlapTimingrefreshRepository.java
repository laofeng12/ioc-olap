package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapAnalyze;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.domain.OlapTimingrefresh;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * 构建定时任务数据库访问层
 *
 * @author zy
 */
public interface OlapTimingrefreshRepository extends DynamicJpaRepository<OlapTimingrefresh, Long>, OlapTimingrefreshRepositoryCustom {

    @Query(value = "select * from OLAP_TIMINGREFRESH where FREQUENCYTYPE=:frequencyType AND NEXT_EXECUTION_TIME < trunc(sysdate, 'DD')", nativeQuery = true)
    List<OlapTimingrefresh> findTiming(@Param("frequencyType") int frequencyType);

    @Query(value = "select t.* from OLAP_TIMINGREFRESH t where t.CUBE_NAME=:cubeName and t.CREATE_ID=:createId)", nativeQuery = true)
    Optional<OlapTimingrefresh> findTableInfo(@Param("cubeName") String cubeName, @Param("createId") Long createId);
}