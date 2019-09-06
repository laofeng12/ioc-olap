package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapDatalaketable;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 构建选择表数据库访问层
 *
 * @author zy
 */
public interface OlapDatalaketableRepository extends DynamicJpaRepository<OlapDatalaketable, Long>, OlapDatalaketableRepositoryCustom {
    @Transactional
    @Modifying
    @Query(value = "delete from OLAP_DATALAKETABLE t where t.CUBE_NAME=:cubeName", nativeQuery = true)
    void deleteCubeName(@Param("cubeName") String cubeName);

    @Query(value = "select * from OLAP_DATALAKETABLE t where t.CUBE_NAME=:cubeName", nativeQuery = true)
    List<OlapDatalaketable> getListByCubeName(@Param("cubeName") String cubeName);
}
