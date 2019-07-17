package com.openjava.platform.domain;

import java.util.Date;
import java.io.Serializable;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体
 * @author xiepc
 *
 */
@ApiModel("立方体表中的列")
@Entity
@Table(name = "OLAP_CUBE_TABLE_COLUMN")
public class OlapCubeTableColumn implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键ID")
	private Long id;
	@ApiModelProperty("列中文名称")
	private String name;
	@ApiModelProperty("立方体ID")
	private Long cubeId;
	@ApiModelProperty("表ID")
	private Long tableId;
	@ApiModelProperty("列名称")
	private String columnName;
	@ApiModelProperty("列别名")
	private String columnAlias;
	@ApiModelProperty("表达式类型 max min sum...")
	private String expressionType;
	@ApiModelProperty("完整表达式")
	private String expressionFull;
	
	@ApiModelProperty("是否新增")
    private Boolean isNew;
	
	@Transient
    @JsonIgnore
    @Override
    public Long getId() {
        return this.id;
	}
    
    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
    	if(isNew != null) {
    		return isNew;
    	}
    	if(this.id != null) {
    		return false;
    	}
    	return true;
    }
    
    @Transient
    public Boolean getIsNew() {
		return isNew;
	}
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
	
	@Id
	@Column(name = "ID")
	public Long getCubeTableColumnId() {
		return id;
	}
	public void setCubeTableColumnId(Long id) {
		this.id = id;
	}
	

	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	@Column(name = "CUBE_ID")
	public Long getCubeId() {
		return cubeId;
	}
	public void setCubeId(Long cubeId) {
		this.cubeId = cubeId;
	}
	

	@Column(name = "TABLE_ID")
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	

	@Column(name = "COLUMN_NAME")
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	

	@Column(name = "COLUMN_ALIAS")
	public String getColumnAlias() {
		return columnAlias;
	}
	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}
	

	@Column(name = "EXPRESSION_TYPE")
	public String getExpressionType() {
		return expressionType;
	}
	public void setExpressionType(String expressionType) {
		this.expressionType = expressionType;
	}
	

	@Column(name = "EXPRESSION_FULL")
	public String getExpressionFull() {
		return expressionFull;
	}
	public void setExpressionFull(String expressionFull) {
		this.expressionFull = expressionFull;
	}
}