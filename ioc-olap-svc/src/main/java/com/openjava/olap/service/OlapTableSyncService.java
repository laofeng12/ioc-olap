package com.openjava.olap.service;

import com.openjava.olap.domain.OlapTableSync;
import com.openjava.olap.query.OlapTableSyncParam;
import com.openjava.olap.vo.OlapTableSyncVo;

import java.util.List;

public interface OlapTableSyncService {

    OlapTableSync save(OlapTableSync sync);

    OlapTableSync get(String databaseId,String resourceId);

    List<OlapTableSyncVo> available(List<OlapTableSyncParam> params)throws Exception;
}
