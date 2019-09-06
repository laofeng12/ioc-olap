package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class RowkeyMapper {
    public ArrayList<RowkeyColumnMapper> rowkey_columns;
}
