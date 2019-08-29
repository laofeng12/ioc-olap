package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverrideKylinPropertiesMapper {
    @JSONField(name = "author")
    public String author;
}
