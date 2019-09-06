package com.openjava.olap.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnyDimensionCellVo {
    private Integer colspan;
    private Integer rowspan;
    private String value;
    private Integer type;//1.X  2.Y  3.指标  4.数据
    private Object attrs;
    private String id;

    public AnyDimensionCellVo() {

    }

    public AnyDimensionCellVo(String id, Integer colspan, Integer rowspan, String value, Integer type) {
        this(id,colspan, rowspan, value, type, null);
    }

    public AnyDimensionCellVo(String id, Integer colspan, Integer rowspan, String value, Integer type, Object attrs) {
        this.id = id;
        this.colspan = colspan;
        this.rowspan = rowspan;
        this.value = value;
        this.type = type;
        this.attrs = attrs;
    }
}
