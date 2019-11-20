package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@ApiModel("麒麟hive表结构")
public class TableStructureMapper {
    @JSONField(name = "uuid")
    @ApiModelProperty("表唯一id")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @ApiModelProperty("表名")
    @JSONField(name = "name")
    public String name;

    @JSONField(name = "columns")
    @ApiModelProperty("表包含的列集合")
    public ArrayList<TableStructureColumnsMapper> columns;
    @JSONField(name = "source_type")
    public Long source_type;
    @JSONField(name = "table_type")
    public String table_type;
    @ApiModelProperty("表所在的库名")
    @JSONField(name = "database")
    public String database;
}
