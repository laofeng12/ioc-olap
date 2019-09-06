package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DemoColumn {
    public String resourceId;
    public String resourceId1;
    public Long last_modified;
    public String version;
    public String name;
    public List<DemoColumnData> data;
}
