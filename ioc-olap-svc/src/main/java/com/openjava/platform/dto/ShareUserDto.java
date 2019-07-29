package com.openjava.platform.dto;

import com.openjava.platform.domain.OlapShare;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class ShareUserDto extends OlapShare {
    @ApiModelProperty("分享用户名称")
    private String shareUserName;
}