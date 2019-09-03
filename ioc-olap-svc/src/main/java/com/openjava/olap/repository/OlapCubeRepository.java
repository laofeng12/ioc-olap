package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapCube;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文件夹表数据库访问层
 * @author xiepc
 *
 */
public interface OlapCubeRepository extends DynamicJpaRepository<OlapCube, Long>, OlapCubeRepositoryCustom{
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_CUBE t where t.NAME=:cubeName", nativeQuery = true)
    void deleteCubeName(@Param("cubeName") String cubeName);

    @Query(value = "select t.* from OLAP_CUBE t where  t.CREATE_ID=:createId", nativeQuery = true)
    List<OlapCube> findByUserId(@Param("createId") Long createId);

    ArrayList<OlapCube> findAll();

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME=:cubeName and t.CREATE_ID=:createId", nativeQuery = true)
    Optional<OlapCube> findTableInfo(@Param("cubeName") String cubeName, @Param("createId") Long createId);

    ArrayList<OlapCube> findByCreateIdAndFlags(Long userId, int flags);

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME=:cubeName", nativeQuery = true)
    Optional<OlapCube> findTableInfo(@Param("cubeName") String cubeName);


    @Query(value = "select c.* from OLAP_CUBE c left join olap_share s  on c.ID=s.FK_ID where s.SHARE_USER_ID=:shareUserId and c.ID is not null", nativeQuery = true)
    List<OlapCube> getOlapShareByShareUserId(@Param("shareUserId") String shareUserId);
}
