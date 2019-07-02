package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class JobsMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "last_modified")
    public Long last_modified;
    @JSONField(name = "version")
    public String version;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "duration")
    public Long duration;
    @JSONField(name = "related_cube")
    public String related_cube;
    @JSONField(name = "related_segment")
    public String related_segment;
    @JSONField(name = "exec_start_time")
    public Long exec_start_time;
    @JSONField(name = "exec_end_time")
    public Long exec_end_time;
    @JSONField(name = "exec_interrupt_time")
    public Long exec_interrupt_time;
    @JSONField(name = "mr_waiting")
    public Long mr_waiting;
    @JSONField(name = "steps")
    public ArrayList<StepsMapper> steps;
    @JSONField(name = "submitter")
    public String submitter;
    @JSONField(name = "job_status")
    public String job_status;
    @JSONField(name = "progress")
    public Long progress;
}
