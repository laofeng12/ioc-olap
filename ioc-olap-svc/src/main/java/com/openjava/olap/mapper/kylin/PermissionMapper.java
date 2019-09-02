package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionMapper {

    @JSONField(name = "mask")
    public Long mask;
    @JSONField(name = "pattern")
    public String pattern;
}
