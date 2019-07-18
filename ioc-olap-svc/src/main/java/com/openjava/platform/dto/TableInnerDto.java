package com.openjava.platform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableInnerDto {
    private String sourceTableAlias;
    private String joinTable;
    private Long joinTableId;
    private String joinTableAlias;
    private String joinType;
    private String[] joinPkKeys;
    private String[] joinFkKeys;
    private String databaseName;
}
