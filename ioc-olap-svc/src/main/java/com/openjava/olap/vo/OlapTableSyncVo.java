package com.openjava.olap.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjava.olap.mapper.kylin.TableStructureMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author linchuangang
 * @create 2019-11-06 13:58
 **/
@ApiModel("同步表记录返回的消息体")
@Getter
@Setter
public class OlapTableSyncVo {

    private String businessId;
    @ApiModelProperty("数据库id")
    private String databaseId;
    private String jobId;
    private String message;
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("资源名称")
    private String resourceName;
    @ApiModelProperty("创建同步任务的结果；true为成功")
    private Boolean success;
    @ApiModelProperty("虚拟表名")
    private String virtualTableName;
    @ApiModelProperty("真实表名")
    private String writerTableName;
    @JsonIgnore
    private Boolean isNew;
    @JsonIgnore
    private Long syncId;
    @ApiModelProperty("对应hive表结构，字段信息")
    private TableStructureMapper meta;
}
