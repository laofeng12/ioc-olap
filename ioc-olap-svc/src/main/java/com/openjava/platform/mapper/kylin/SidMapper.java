package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SidMapper {
    @JSONField(name = "principal")
    public String principal;
}
