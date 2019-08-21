package com.openjava.platform.vo;

import com.openjava.platform.mapper.kylin.ColumnMetaMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnyDimensionVo {
    private List<ArrayList<AnyDimensionCellVo>> results;
    private String cube;
    private Integer affectedRowCount;
    private boolean isException;
    private String exceptionMessage;
    private Integer duration;
    private Integer totalScanCount;
    private Integer totalScanBytes;
    private Integer totalRows;
}
