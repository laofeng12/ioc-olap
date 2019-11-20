package com.openjava.olap.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 批量同步任务创建接口的参数体
 * @author linchuangang
 * @create 2019-11-05 17:16
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 **/
@ApiModel("创建同步任务时所需的消息体")
@Getter
@Setter
public class OlapTableSyncParam {

    @ApiModelProperty("数据库id")
    private String databaseId;
    @ApiModelProperty("资源id")
    private String resourceId;
    @ApiModelProperty("资源名称，实则为虚拟表名")
    private String resourceName;
    private String hiveDbName = "olap";
    @ApiModelProperty("同步类型，olap固定值为3")
    private Integer syncSource;
    private Integer type;
    private String writerTableComment;
    private String writerTableSource;
    private Integer success;
}
