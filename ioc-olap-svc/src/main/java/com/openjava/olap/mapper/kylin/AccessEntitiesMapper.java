package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessEntitiesMapper {

    @JSONField(name = "permission")
    public PermissionMapper permission;
    @JSONField(name = "id")
    public String id;
    @JSONField(name = "sid")
    public SidMapper sid;
    @JSONField(name = "granting")
    public boolean granting;
}
