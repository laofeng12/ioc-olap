package com.openjava.platform.repository;

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
public interface OlapRealQueryRepository extends DynamicJpaRepository<OlapRealQuery, Long>, OlapRealQueryRepositoryCustom{

    List<OlapRealQuery> findByFolderId(Long folderId);

    @Query(value = "select a.*,c.FULLNAME as shareUserName from OLAP_REAL_QUERY a inner join OLAP_SHARE b on a.ID=b.FK_ID and b.FK_TYPE='RealQuery' inner join SYS_USER c on b.SHARE_USER_ID=c.USERID where b.SHARE_USER_ID=:userId",nativeQuery = true)
    List<OlapRealQuery> getAllShares(@Param("userId") Long userId);
}
