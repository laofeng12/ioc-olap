package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupsMapper {
    @JSONField(name = "table")
    public String table;
    @JSONField(name = "virtualTableName")
    public String virtualTableName;
    @JSONField(name = "kind")
    public String kind;
    @JSONField(name = "alias")
    public String alias;
    @JSONField(name = "joinTable")
    public String joinTable;
    @JSONField(name = "joinAlias")
    public String joinAlias;
    @JSONField(name = "joinId")
    public String joinId;
    @JSONField(name = "join")
    public JoinMapper join;

    @JSONField(name = "SAxis")
    public Long SAxis;
    @JSONField(name = "YAxis")
    public Long YAxis;
    @JSONField(name = "joinSAxis")
    public Long joinSAxis;
    @JSONField(name = "joinYAxis")
    public Long joinYAxis;

    @JSONField(name = "id")
    public String id;
}
