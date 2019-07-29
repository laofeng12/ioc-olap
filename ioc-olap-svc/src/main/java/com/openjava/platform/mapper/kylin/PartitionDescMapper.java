package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartitionDescMapper {
    @JSONField(name = "partition_date_column")
    public String partition_date_column;
    @JSONField(name = "partition_date_start")
    public Long partition_date_start;
    @JSONField(name = "partition_date_format")
    public String partition_date_format;
    @JSONField(name = "partition_time_format")
    public String partition_time_format;
    @JSONField(name = "partition_type")
    public String partition_type;
    @JSONField(name = "partition_condition_builder")
    public String partition_condition_builder;

    @JSONField(name = "interval")
    public Long interval;
    @JSONField(name = "frequencytype")
    public Long frequencytype;
}
