package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AggregationGroupRuleMapper {
    public ArrayList<ArrayList<String>> hierarchy_dims;
    public ArrayList<String> mandatory_dims;
    public ArrayList<ArrayList<String>> joint_dims;
}
