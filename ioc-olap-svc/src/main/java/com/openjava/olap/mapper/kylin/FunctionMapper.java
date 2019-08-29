package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionMapper {
    public String expression;
    public String returntype;
    public ParameterMapper parameter;
}
