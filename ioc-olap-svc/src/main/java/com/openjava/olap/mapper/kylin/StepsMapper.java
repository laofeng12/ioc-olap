package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepsMapper {
    @JSONField(name = "interruptCmd")
    public String interruptCmd;
    @JSONField(name = "id")
    public String id;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "sequence_id")
    public Long sequence_id;
    @JSONField(name = "exec_cmd")
    public String exec_cmd;
    @JSONField(name = "interrupt_cmd")
    public String interrupt_cmd;
    @JSONField(name = "exec_start_time")
    public Long exec_start_time;
    @JSONField(name = "exec_end_time")
    public Long exec_end_time;
    @JSONField(name = "exec_wait_time")
    public Long exec_wait_time;
    @JSONField(name = "step_status")
    public String step_status;
    @JSONField(name = "cmd_type")
    public String cmd_type;
    @JSONField(name = "info")
    public InfoMapper info;
    @JSONField(name = "run_async")
    public String run_async;
}
