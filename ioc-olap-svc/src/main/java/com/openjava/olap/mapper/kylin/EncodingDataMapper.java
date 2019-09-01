package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EncodingDataMapper {
//    public String code;
    public Map<String, String[]> data;
    public String msg;
}
