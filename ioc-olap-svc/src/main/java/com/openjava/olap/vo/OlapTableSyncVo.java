package com.openjava.olap.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linchuangang
 * @create 2019-11-06 13:58
 **/
@Getter
@Setter
public class OlapTableSyncVo {

    private String businessId;
    private String databaseId;
    private String jobId;
    private String message;
    private String resourceId;
    private String resourceName;
    private Boolean success;
    private String writerTableName;
    @JsonIgnore
    private Boolean isNew;
    @JsonIgnore
    private Long syncId;
}
