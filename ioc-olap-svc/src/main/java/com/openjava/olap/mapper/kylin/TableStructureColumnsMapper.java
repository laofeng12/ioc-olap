package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("麒麟hive表列信息")
@Getter
@Setter
public class TableStructureColumnsMapper {
    @JSONField(name = "id")
    public String id;
    @JSONField(name = "name")
    @ApiModelProperty("列名")
    public String name;
    @ApiModelProperty("数据类型")
    @JSONField(name = "datatype")
    public String datatype;
    @ApiModelProperty("字段描述")
    @JSONField(name = "comment")
    public String comment;
}
