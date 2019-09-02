package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnMetaMapper {
    public Integer isNullable;
    public String label;
    public String name;
    public Integer columnType;
    public String columnTypeName;
}
