package com.openjava.olap.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author linchuangang
 * @create 2019-11-05 17:16
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
