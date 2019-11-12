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
 *
 * @author xiepc
 */
public interface OlapCubeRepository extends DynamicJpaRepository<OlapCube, Long>, OlapCubeRepositoryCustom {
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_CUBE t where t.NAME=:cubeName", nativeQuery = true)
    void deleteCubeName(@Param("cubeName") String cubeName);

    @Query(value = "select t.* from OLAP_CUBE t where  t.CREATE_ID=:createId", nativeQuery = true)
    List<OlapCube> findByUserId(@Param("createId") Long createId);

    ArrayList<OlapCube> findAll();

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME=:cubeName and t.CREATE_ID=:createId", nativeQuery = true)
    Optional<OlapCube> findTableInfo(@Param("cubeName") String cubeName, @Param("createId") Long createId);

    @Query(value = "select * from OLAP_CUBE where FLAGS=:flags and create_id=:userId union select c.* from OLAP_CUBE c inner join olap_share s  on c.ID=s.FK_ID and c.FLAGS=:flags where s.SHARE_USER_ID=:userId", nativeQuery = true)
    ArrayList<OlapCube> findByCreateIdAndFlags(@Param("userId") Long userId, @Param("flags") int flags);

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME=:cubeName", nativeQuery = true)
    Optional<OlapCube> findTableInfo(@Param("cubeName") String cubeName);


    @Query(value = "select c.* from OLAP_CUBE c inner join olap_share s  on c.ID=s.FK_ID where s.SHARE_USER_ID=:shareUserId", nativeQuery = true)
    List<OlapCube> getOlapShareByShareUserId(@Param("shareUserId") String shareUserId);

    @Query(value = "select c.NAME from OLAP_CUBE c where c.FLAGS=:flags",nativeQuery = true)
    List<String> getCubeNameByFlags(@Param("flags") Integer flags);

    @Query(value = "select c.NAME from OLAP_CUBE c where c.FLAGS=:flags",nativeQuery = true)
    List<OlapCube> getListByFlags(@Param("flags") Integer flags);

    @Query(value = "select t.* from OLAP_CUBE t where t.NAME in (:nameList)", nativeQuery = true)
    List<OlapCube> findByCubeNameList(@Param("nameList")List<String> cubeNameList);
}
