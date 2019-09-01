package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Realizations {
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "realization")
    public String realization;
}
