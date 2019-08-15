package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class HiveMapper {
    @JSONField(name = "tableNameArr")
    public ArrayList[] tableNameArr;
    @JSONField(name = "libraryName")
    public String libraryName;
    @JSONField(name = "calculate")
    public boolean calculate = true;
}
