package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeMapper {
    public String uuid;
    public Integer last_modified;
    public String version;
    public String name;
    public String owner;
    public String descriptor;
    public String display_name;
    public Integer cost;
    public String status;
    public Integer create_time_utc;
    public String cuboid_bytes;
    public String cuboid_bytes_recommend;
    public Integer cuboid_last_optimized;
    public String project;
    public String model;
    public boolean is_streaming;
    public String partitionDateColumn;
    public Integer partitionDateStart;
    public boolean isStandardPartitioned;
    public Integer size_kb;
    public Integer input_records_count;
    public Integer input_records_size;
}
