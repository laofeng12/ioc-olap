package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasureMapper
{
    public String name;
    public FunctionMapper function;
    public boolean showDim;
}
