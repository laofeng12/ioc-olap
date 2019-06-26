package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectManageDto {
    @JSONField(name = "formerProjectName")
    public String formerProjectName;
    @JSONField(name = "projectDescData")
    public ProjectDescDataDto projectDescData;
}
