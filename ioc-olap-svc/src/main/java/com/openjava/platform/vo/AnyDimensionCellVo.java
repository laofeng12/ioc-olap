package com.openjava.platform.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnyDimensionCellVo {
    private Integer colspan;
    private Integer rowspan;
    private String value;
    private Integer type;
    private Object attrs;

    public AnyDimensionCellVo(){

    }

    public AnyDimensionCellVo(Integer colspan,Integer rowspan,String value,Integer type){
        this(colspan,rowspan,value,type,null);
    }

    public AnyDimensionCellVo(Integer colspan,Integer rowspan,String value,Integer type,Object attrs){
        this.colspan=colspan;
        this.rowspan=rowspan;
        this.value=value;
        this.type=type;
        this.attrs=attrs;
    }
}
