package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupsMapper {
    @JSONField(name = "table")
    public String table;
    @JSONField(name = "kind")
    public String kind;
    @JSONField(name = "alias")
    public String alias;
    @JSONField(name = "join")
    public JoinMapper join;
}
