package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EncodingDataMapper {
    public String code;
    public Map<String, String[]> data;
    public String msg;
}
