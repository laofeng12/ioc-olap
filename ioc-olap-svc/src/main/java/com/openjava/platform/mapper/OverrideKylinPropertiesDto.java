package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverrideKylinPropertiesDto {
    @JSONField(name = "author")
    public String author;
}
