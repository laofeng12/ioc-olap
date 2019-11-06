package com.openjava.olap.repository;

import com.openjava.olap.domain.OlapTableSync;
import org.ljdp.core.spring.data.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OlapTableSyncRepository extends DynamicJpaRepository<OlapTableSync,Long> {

    OlapTableSync getByDatabaseIdAndResourceId(String databaseId,String resourceId);

    @Query(value = "select SYNC_ID from OLAP_TABLE_SYNC where DATABASE_ID=:databaseId and RESOURCE_ID=:resourceId",nativeQuery = true)
    Long exist(@Param("databaseId") String databaseId,@Param("resourceId") String resourceId);
}
