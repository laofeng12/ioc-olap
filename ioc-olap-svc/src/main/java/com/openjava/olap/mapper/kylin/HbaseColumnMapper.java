package com.openjava.olap.mapper.kylin;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class HbaseColumnMapper {
    public String qualifier;
    public ArrayList<String> measure_refs;
}
