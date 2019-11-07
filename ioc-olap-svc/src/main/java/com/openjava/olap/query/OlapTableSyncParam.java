package com.openjava.olap.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量同步任务创建接口的参数体
 * @author linchuangang
 * @create 2019-11-05 17:16
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 **/
@Getter
@Setter
public class OlapTableSyncParam {

    private String databaseId;
    private String resourceId;
    private String resourceName;
    private String hiveDbName = "olap";
    private Integer syncSource;
    private Integer type;
    private String writerTableComment;
    private String writerTableSource;
    private Integer success;
}
