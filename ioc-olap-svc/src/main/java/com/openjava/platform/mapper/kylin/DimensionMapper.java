package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DimensionMapper {
    public String name;
    public String realName;
    public ArrayList<String> derived;
    public String table;
    public String column;
    public String columnType;
    public String column_type;
}
