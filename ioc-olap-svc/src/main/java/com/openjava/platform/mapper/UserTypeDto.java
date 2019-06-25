package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTypeDto {
    @JSONField(name="ID")
    public String ID;
    @JSONField(name="NAME")
    public String NAME;
}
