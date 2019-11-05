package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeDictionaryMapper {
    public static final String GlobalDictionaryBuilder = "org.apache.kylin.dict.GlobalDictionaryBuilder";

    @JSONField(name = "column")
    public String column;
    @JSONField(name = "builder")
    public String builder;

    public CubeDictionaryMapper() {

    }

    public CubeDictionaryMapper(String column, String builder) {
        this.column = column;
        this.builder = builder;
    }
}
