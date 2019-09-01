package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TableStructureMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @JSONField(name = "name")
    public String name;

    @JSONField(name = "columns")
    public ArrayList<TableStructureColumnsMapper> columns;
    @JSONField(name = "source_type")
    public Long source_type;
    @JSONField(name = "table_type")
    public String table_type;
    @JSONField(name = "database")
    public String database;
}
