package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RealizationsDto {
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "realization")
    public Long realization;
}
