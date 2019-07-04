package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class QueryTableColumnMapper {
    @JSONField(name = "columns")
    public ArrayList<CubecolumnsMapper> columns;
    @JSONField(name = "table_SCHEM")
    public String table_SCHEM;
    @JSONField(name = "table_NAME")
    public String table_NAME;
    @JSONField(name = "table_CAT")
    public String table_CAT;
    @JSONField(name = "table_TYPE")
    public String table_TYPE;
    @JSONField(name = "remarks")
    public String remarks;
    @JSONField(name = "type_CAT")
    public String type_CAT;
    @JSONField(name = "type_SCHEM")
    public String type_SCHEM;
    @JSONField(name = "type_NAME")
    public String type_NAME;
    @JSONField(name = "self_REFERENCING_COL_NAME")
    public String self_REFERENCING_COL_NAME;
    @JSONField(name = "ref_GENERATION")
    public String ref_GENERATION;
}
