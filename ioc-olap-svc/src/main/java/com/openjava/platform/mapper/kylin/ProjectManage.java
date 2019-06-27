package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectManage {
    @JSONField(name = "formerProjectName")
    public String formerProjectName;
    @JSONField(name = "projectDescData")
    public ProjectDescData projectDescData;
}
