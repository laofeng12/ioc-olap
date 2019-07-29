package com.openjava.platform.repository;

import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.dto.ShareUserDto;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapShareRepository extends DynamicJpaRepository<OlapShare, Long>, OlapShareRepositoryCustom{
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_SHARE WHERE FK_ID = :sourceId and FK_TYPE = :sourceType",nativeQuery = true)
    void deleteByFkIdAndSourceId(@Param("sourceId") Long sourceId,@Param("sourceType") String sourceType);

    @Query(value = "select new com.openjava.platform.dto.ShareUserDto(p.ID,p.FK_ID,p.FK_TYPE,p.SHARE_USER_ID,p.CREATE_TIME,p.CREATE_ID,p.CREATE_NAME,q.FULLNAME )  from OLAP_SHARE p inner join SYS_USER q on p.shareUserId=q.USERID where p.FK_TYPE = :sourceType and p.FK_ID = :sourceId and p.CREATE_ID = :userId")
    List<ShareUserDto> getList(@Param("sourceType") String sourceType, @Param("sourceId") String sourceId, @Param("userId") Long userId);
}
