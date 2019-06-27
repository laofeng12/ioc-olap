package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class HbaseColumnFamilyMapper {
    public String name;
    public ArrayList<HbaseColumnMapper> columns;
}

