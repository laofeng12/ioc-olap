package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ModelsDescDataMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "owner")
    public String owner;
//    @JSONField(name = "is_draft")
//    public boolean is_draft;
    @JSONField(name = "description")
    public String description;
    @JSONField(name = "fact_table")
    public String fact_table;
    @JSONField(name = "lookups")
    public ArrayList<LookupsMapper> lookups;
    @JSONField(name = "dimensions")
    public ArrayList<DimensionsMapper>  dimensions;
    @JSONField(name = "metrics")
    public String[] metrics;
    @JSONField(name = "filter_condition")
    public String filter_condition;
    @JSONField(name = "partition_desc")
    public PartitionDescMapper partition_desc;
    @JSONField(name = "capacity")
    public String capacity;

    @JSONField(name = "SAxis")
    public String SAxis;
    @JSONField(name = "YAxis")
    public String YAxis;
}
