package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Realizations {
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "realization")
    public String realization;
}
