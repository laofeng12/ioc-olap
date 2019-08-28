package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectMapper {
    @JSONField(name = "formerProjectName")
    public String formerProjectName;
    @JSONField(name = "projectDescData")
    public ProjectDescDataMapper projectDescData;
}
