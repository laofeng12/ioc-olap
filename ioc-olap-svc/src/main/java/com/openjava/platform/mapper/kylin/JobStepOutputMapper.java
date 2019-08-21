package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobStepOutputMapper {
    private String jobId;
    private String cmd_output;
    private String stepId;
}
