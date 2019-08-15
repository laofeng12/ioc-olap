package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SegmentsMapper {
    public String binary_signature;
    public ArrayList[] blackout_cuboids;
    public Long create_time_utc;
//    public String cuboid_shard_nums;
    public Long date_range_end;
    public Long date_range_start;
    public Long input_records;
    public Long input_records_size;
    public String last_build_job_id;
    public Long last_build_time;
    public String name;
    public ArrayList[] rowkey_stats;
    public Integer size_kb;
//    public Snapshots snapshots;
    public Long source_offset_end;
    public Long source_offset_start;
    public String  status;
    public String  storage_location_identifier;
    public Integer  total_shards;
    public String  uuid;

}
