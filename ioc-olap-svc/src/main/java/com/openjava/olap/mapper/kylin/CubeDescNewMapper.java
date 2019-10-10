package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeDescNewMapper {
    @JSONField(name = "cubeDescData")
    public String cubeDescData;
    @JSONField(name = "project")
    public String project;
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "cubeName")
    public String cubeName;
    @JSONField(name = "kafkaData")
    public String kafkaData;
    @JSONField(name = "message")
    public String message;
    @JSONField(name = "streamingCube")
    public String streamingCube;
    @JSONField(name = "streamingData")
    public String streamingData;
    @JSONField(name = "successful")
    public String successful;
}
