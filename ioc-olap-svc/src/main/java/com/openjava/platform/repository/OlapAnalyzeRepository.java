package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapAnalyze;
import com.openjava.platform.domain.OlapRealQuery;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapAnalyzeRepository extends DynamicJpaRepository<OlapAnalyze, Long>, OlapAnalyzeRepositoryCustom{
    List<OlapAnalyze> findByFolderId(Long folderId);

    @Query(value = "select a.* from OLAP_ANALYZE a inner join OLAP_SHARE b on a.ID=b.FK_ID and b.FK_TYPE='Analyze' where b.SHARE_USER_ID=:userId",nativeQuery = true)
    List<OlapAnalyze> getAllShares(@Param("userId") Long userId);
}
