package com.openjava.olap.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CubeHbaseMapper {
    private Long dateRangeEnd;
    private Long dateRangeStart;
    private Integer regionCount;
    private String segmentName;
    private String segmentStatus;
    private Integer sourceCount;
    private Integer sourceOffsetEnd;
    private Integer sourceOffsetStart;
    private String tableName;
    private Long tableSize;
}
