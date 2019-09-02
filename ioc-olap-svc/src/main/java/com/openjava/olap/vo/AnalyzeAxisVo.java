package com.openjava.olap.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyzeAxisVo {
    @ApiModelProperty("列id")
    private Long columnId;
    @ApiModelProperty("1 行轴 2 列轴 3 指标轴 4 筛选轴")
    private Integer type;
    @ApiModelProperty("表ID")
    private Long tableId;
    @ApiModelProperty("表中文名称")
    private String name;
    @ApiModelProperty("立方体id")
    private Long cubeId;
    @ApiModelProperty("表名称")
    private String tableName;
    @ApiModelProperty("表别名")
    private String tableAlias;
    @ApiModelProperty("是否是事实表")
    private Integer isDict;


    @ApiModelProperty("列中文名称")
    private String columnChName;
    @ApiModelProperty("列名称")
    private String columnName;
    @ApiModelProperty("列别名")
    private String columnAlias;
    @ApiModelProperty("表达式类型 max min sum...")
    private String expressionType;
    @ApiModelProperty("完整表达式")
    private String expressionFull;

    @ApiModelProperty("是否包含 1 包含 0 不包含")
    private Integer isInclude;
    @ApiModelProperty("选中的值列表 逗号分隔")
    private String selectValues;
}
