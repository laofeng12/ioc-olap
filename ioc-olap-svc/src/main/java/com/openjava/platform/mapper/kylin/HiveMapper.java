package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HiveMapper {
    @JSONField(name = "tableNameArr")
    public String[] tableNameArr;
    @JSONField(name = "libraryName")
    public String libraryName;
    @JSONField(name = "calculate")
    public boolean calculate = true;
}
