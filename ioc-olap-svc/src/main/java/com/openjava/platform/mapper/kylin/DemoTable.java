package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DemoTable {
    public String resourceId;
    public Long last_modified;
    public String version;
    public String resourceTableName;
    public Long source_type;
    public String table_type;
    public String database;
    public String orgId;
}
