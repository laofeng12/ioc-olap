package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class QueryResultMapper {
    public ArrayList<ColumnMetaMapper> columnMetas;
    public ArrayList<String> results;
    public String cube;
    public Integer affectedRowCount;
    public boolean isException;
    public String exceptionMessage;
    public Integer duration;
    public Integer totalScanCount;
    public Integer totalScanBytes;
}
