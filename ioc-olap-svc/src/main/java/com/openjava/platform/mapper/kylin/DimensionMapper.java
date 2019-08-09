package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensionMapper {
    public String name;
    public String realName;
    public String[] derived;
    public String table;
    public String column;
    public String columnType;
    public String column_type;
}
