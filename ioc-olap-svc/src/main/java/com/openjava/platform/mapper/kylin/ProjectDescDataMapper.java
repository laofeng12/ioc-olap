package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectDescDataMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "tables")
    public ArrayList<String> tables;
    @JSONField(name = "owner")
    public String owner;
    @JSONField(name = "status")
    public String status;
    @JSONField(name = "create_time_utc")
    public Long create_time_utc;
    @JSONField(name = "last_update_time")
    public String last_update_time;
    @JSONField(name = "description")
    public String description;
    @JSONField(name = "realizations")
    public ArrayList<Realizations> realizations;
    @JSONField(name = "models")
    public ArrayList<String> models;
    @JSONField(name = "ext_filters")
    public ArrayList<String> ext_filters;
    @JSONField(name = "override_kylin_properties")
    public OverrideKylinPropertiesMapper override_kylin_properties;
    @JSONField(name = "accessLoading")
    public boolean accessLoading;
    @JSONField(name = "visiblePage")
    public String visiblePage;
    @JSONField(name = "accessEntities")
    public ArrayList<AccessEntitiesMapper> accessEntities;
}
