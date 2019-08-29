package com.openjava.olap.dto;

import com.openjava.olap.domain.OlapShare;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ShareUserDto extends OlapShare {
    @ApiModelProperty("分享用户名称")
    private String shareUserName;

    public ShareUserDto(){

    }

    public ShareUserDto(Long id, Long fkId, String fkType, Long shareUserId, Date createTime, Long createId, String createName, String shareUserName){
        this.setShareId(id);
        this.setFkId(fkId);
        this.setFkType(fkType);
        this.setShareUserId(shareUserId);
        this.setCreateTime(createTime);
        this.setCreateId(createId);
        this.setCreateName(createName);
        this.setShareUserName(shareUserName);
    }
}
