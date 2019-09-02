package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DimensionsMapper {
    @JSONField(name = "table")
    public String table;
    @JSONField(name = "columns")
    public ArrayList<String> columns;
}
