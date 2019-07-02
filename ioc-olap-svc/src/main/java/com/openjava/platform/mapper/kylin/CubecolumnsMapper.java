package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubecolumnsMapper {

    @JSONField(name = "table_SCHEM")
    public String table_SCHEM;
    @JSONField(name = "table_NAME")
    public String table_NAME;
    @JSONField(name = "column_NAME")
    public String column_NAME;
    @JSONField(name = "table_CAT")
    public String table_CAT;
    @JSONField(name = "remarks")
    public String remarks;
    @JSONField(name = "type_NAME")
    public String type_NAME;
    @JSONField(name = "data_TYPE")
    public Long data_TYPE;
    @JSONField(name = "column_SIZE")
    public Long column_SIZE;
    @JSONField(name = "buffer_LENGTH")
    public Long buffer_LENGTH;
    @JSONField(name = "decimal_DIGITS")
    public Long decimal_DIGITS;
    @JSONField(name = "num_PREC_RADIX")
    public Long num_PREC_RADIX;
    @JSONField(name = "nullable")
    public Long nullable;
    @JSONField(name = "column_DEF")
    public String column_DEF;
    @JSONField(name = "sql_DATA_TYPE")
    public Long sql_DATA_TYPE;
    @JSONField(name = "sql_DATETIME_SUB")
    public Long sql_DATETIME_SUB;
    @JSONField(name = "char_OCTET_LENGTH")
    public Long char_OCTET_LENGTH;
    @JSONField(name = "ordinal_POSITION")
    public Long ordinal_POSITION;
    @JSONField(name = "is_NULLABLE")
    public String is_NULLABLE;
    @JSONField(name = "scope_CATLOG")
    public String scope_CATLOG;
    @JSONField(name = "scope_SCHEMA")
    public String scope_SCHEMA;
    @JSONField(name = "scope_TABLE")
    public String scope_TABLE;
    @JSONField(name = "source_DATA_TYPE")
    public Long source_DATA_TYPE;
    @JSONField(name = "is_AUTOINCREMENT")
    public String is_AUTOINCREMENT;
}
