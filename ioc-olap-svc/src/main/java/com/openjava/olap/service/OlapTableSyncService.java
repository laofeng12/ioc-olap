package com.openjava.olap.service;

import com.openjava.olap.domain.OlapTableSync;
import com.openjava.olap.query.OlapTableSyncParam;

import java.util.List;
import java.util.Map;

public interface OlapTableSyncService {

    OlapTableSync save(OlapTableSync sync);

    OlapTableSync get(String databaseId,String resourceId,String createBy);

    Map<String,Object> available(List<OlapTableSyncParam> params)throws Exception;
}
