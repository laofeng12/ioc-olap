package com.openjava.platform.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectDescDataDto {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "tables")
    public List<String> tables;
    @JSONField(name = "owner")
    public String owner;
    @JSONField(name = "status")
    public String status;
    @JSONField(name = "create_time_utc")
    public Long create_time_utc;
    @JSONField(name = "last_update_time")
    public Long last_update_time;
    @JSONField(name = "description")
    public String description;
    @JSONField(name = "realizations")
    public UserTypeDto realizations;
    @JSONField(name = "models")
    public List<String> models;
    @JSONField(name = "ext_filters")
    public List<String> ext_filters;
    @JSONField(name = "override_kylin_properties")
    public OverrideKylinPropertiesDto override_kylin_properties;
}
