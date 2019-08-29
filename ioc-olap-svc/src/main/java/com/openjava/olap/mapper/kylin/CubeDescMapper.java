package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeDescMapper {
    public CubeDescDataMapper cubeDescData;
    public String project;
    public String uuid;
    public String cubeName;
}
