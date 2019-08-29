package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CubeMapper {
    public String uuid;
    public Long last_modified;
    public String version;
    public String name;
    public String owner;
    public String descriptor;
    public String display_name;
    public Integer cost;
    public String status;
    public Long create_time_utc;
    public String cuboid_bytes;
    public String cuboid_bytes_recommend;
    public Integer cuboid_last_optimized;
    public String project;
    public ArrayList<SegmentsMapper> segments;
    public String model;
    public boolean is_streaming;
    public String partitionDateColumn;
    public Long partitionDateStart;
    public boolean isStandardPartitioned;
    public Integer size_kb;
    public Integer input_records_count;
    public Integer input_records_size;
    public String modelSource;
    public Long cubeId;
}
