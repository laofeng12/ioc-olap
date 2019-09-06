package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableStructureColumnsMapper {
    @JSONField(name = "id")
    public String id;
    @JSONField(name = "name")
    public String name;
    @JSONField(name = "datatype")
    public String datatype;
    @JSONField(name = "comment")
    public String comment;
}
