package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class CubeDescDataMapper {
    public String uuid;
    public String last_modified;
    public String version;
    public boolean is_draft;
    public String name;
    public String model_name;
    public String description;
    public ArrayList<DimensionMapper> dimensions;
    public ArrayList<MeasureMapper> measures;
    public ArrayList<CubeDictionaryMapper> dictionaries;
    public RowkeyMapper rowkey;
    public ArrayList<AggregationGroupMapper> aggregation_groups;
    public ArrayList mandatory_dimension_set_list;
    public Long partition_date_start;
    public ArrayList<String> notify_list;
    public HbaseMappingMapper hbase_mapping;
    public String volatile_range;
    public String retention_range;
    public ArrayList<String> status_need_notify;
    public ArrayList<Long> auto_merge_time_ranges;
    public Integer engine_type;
    public String storage_type;
    public String columns_Type;
    public HashMap override_kylin_properties;

    public CubeDescDataMapper(){
        override_kylin_properties=new HashMap();
        override_kylin_properties.put("kylin.cube.aggrgroup.is-mandatory-only-valid","true");
        override_kylin_properties.put("kylin.cube.aggrgroup.max-combination",Integer.MAX_VALUE);
    }
}
