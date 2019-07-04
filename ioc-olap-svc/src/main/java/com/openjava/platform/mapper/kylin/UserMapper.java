package com.openjava.platform.mapper.kylin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserMapper {
    public String password;
    public String username;
    public List<UserRoleMapper> authorities;
    public boolean accountNonExpired;
    public boolean accountNonLocked;
    public boolean enabled;
}
