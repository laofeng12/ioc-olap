package com.openjava.olap.mapper.kylin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author linchuangang
 * @create 2019-10-31 15:31
 **/
@Data
@ApiModel(description = "模型表的虚拟表名与真实表名的对应关系实体")
public class TableNameRelationMapper {

    @ApiModelProperty(value = "同步数据任务jobId(暂未使用)")
    private String jobId;

    @ApiModelProperty(value = "资源id，唯一标识，用于虚拟与真实表名之间的交叉替换")
    private String resourceId;

    @ApiModelProperty(value = "真实表名，调用数据集批量同步数据任务返回的表名，目前由前端自定义")
    private String tableName;

    @ApiModelProperty(value = "虚拟表名，资源信息接口返回的表名")
    private String virtualTableName;

    public TableNameRelationMapper(){

    }

}
