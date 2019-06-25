package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @JSONField(name="ID")
    public String ID;
    @JSONField(name="NAME")
    public String NAME;
    @JSONField(name="Type")
    public UserTypeDto Type;
}
