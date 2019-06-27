package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AggregationGroupMapper {
    public ArrayList<String> includes;
    public AggregationGroupRuleMapper select_rule;
}
