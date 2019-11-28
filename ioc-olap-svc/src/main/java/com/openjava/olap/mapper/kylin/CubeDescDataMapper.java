package com.openjava.olap.mapper.kylin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@ApiModel("立方体详细消息体")
public class CubeDescDataMapper {

    public String uuid;
    public String last_modified;
    public String version;
    public boolean is_draft;
    @ApiModelProperty("立方体名称")
    public String name;
    @ApiModelProperty("模型名称")
    public String model_name;
    @ApiModelProperty("模型描述")
    public String description;
    @ApiModelProperty("维度列表")
    public ArrayList<DimensionMapper> dimensions;
    @ApiModelProperty("度量列表")
    public ArrayList<MeasureMapper> measures;
    @ApiModelProperty("立方体字典列表")
    public ArrayList<CubeDictionaryMapper> dictionaries;
    @ApiModelProperty("列索引")
    public RowkeyMapper rowkey;
    @ApiModelProperty("聚合消息体")
    public ArrayList<AggregationGroupMapper> aggregation_groups;
    public ArrayList mandatory_dimension_set_list;
    @ApiModelProperty("增量构建的开始时间")
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
