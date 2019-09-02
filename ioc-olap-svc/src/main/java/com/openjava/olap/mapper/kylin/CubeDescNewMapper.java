package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeDescNewMapper {
    public String cubeDescData;
    public String project;
    public String uuid;
    public String cubeName;

    public String kafkaData;
    public String message;
    public String streamingCube;
    public String streamingData;
    public String successful;
}
