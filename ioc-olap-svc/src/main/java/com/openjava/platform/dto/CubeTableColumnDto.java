package com.openjava.platform.dto;

import io.swagger.annotations.ApiModelProperty;

public class CubeTableColumnDto {
    @ApiModelProperty("主键")
    private Long tableId;
    @ApiModelProperty("表中文名称")
    private String tableChName;
    @ApiModelProperty("立方体id")
    private Long cubeId;
    @ApiModelProperty("表名称")
    private String tableName;
    @ApiModelProperty("表别名")
    private String tableAlias;
    @ApiModelProperty("是否是事实表")
    private Long isDict;
    @ApiModelProperty("关联表名")
    private String joinTable;
    @ApiModelProperty("关联类型 left join 或者 inner join")
    private String joinType;
    @ApiModelProperty("主键列名称")
    private String pkKey;
    @ApiModelProperty("外键列名称")
    private String fkKey;
    @ApiModelProperty("主键数据类型")
    private String pkDataType;
    @ApiModelProperty("外键数据类型")
    private String fkDataType;
    @ApiModelProperty("备注")
    private String tableRemark;
    @ApiModelProperty("主键ID")
    private Long columnId;
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
    @ApiModelProperty("备注")
    private String columnRemark;
}

