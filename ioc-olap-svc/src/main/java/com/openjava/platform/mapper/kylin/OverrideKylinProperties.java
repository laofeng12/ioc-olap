package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverrideKylinProperties {
    @JSONField(name = "author")
    public String author;
}
