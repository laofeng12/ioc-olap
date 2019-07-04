package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinMapper {
    @JSONField(name = "type")
    public String type;
    @JSONField(name = "primary_key")
    public String[] primary_key;
    @JSONField(name = "foreign_key")
    public String[] foreign_key;
    @JSONField(name = "isCompatible")
    public String[] isCompatible;
    @JSONField(name = "pk_type")
    public String[] pk_type;
    @JSONField(name = "fk_type")
    public String[] fk_type;
}
