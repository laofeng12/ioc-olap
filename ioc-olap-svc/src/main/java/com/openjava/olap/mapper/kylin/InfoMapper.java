package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoMapper {
    @JSONField(name = "yarn_application_id")
    public String yarn_application_id;
    @JSONField(name = "yarn_application_tracking_url")
    public String yarn_application_tracking_url;
    @JSONField(name = "mr_job_id")
    public String mr_job_id;
    @JSONField(name = "hdfs_bytes_written")
    public String hdfs_bytes_written;
    @JSONField(name = "startTime")
    public String startTime;
    @JSONField(name = "endTime")
    public String endTime;
    @JSONField(name = "sourceSizeBytes")
    public String sourceSizeBytes;
    @JSONField(name = "mapReduceWaitTime")
    public String mapReduceWaitTime;
    @JSONField(name = "source_records_count")
    public String source_records_count;
    @JSONField(name = "source_records_size")
    public String source_records_size;
    @JSONField(name = "sourceRecordCount")
    public String sourceRecordCount;
}
