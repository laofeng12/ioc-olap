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
@ApiModel("OLAP轴")
@Entity
@Table(name = "OLAP_ANALYZE_AXIS")
public class OlapAnalyzeAxis implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键id")
	private Long id;
	@ApiModelProperty("分析id")
	private Long analyzeId;
	@ApiModelProperty("列id")
	private Long columnId;
	@ApiModelProperty("1 行轴 2 列轴 3 指标轴 4 筛选轴")
	private Long type;
	@ApiModelProperty("表ID")
	private Long tableId;
	
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
	public Long getAnalyzeAxisId() {
		return id;
	}
	public void setAnalyzeAxisId(Long id) {
		this.id = id;
	}
	

	@Column(name = "ANALYZE_ID")
	public Long getAnalyzeId() {
		return analyzeId;
	}
	public void setAnalyzeId(Long analyzeId) {
		this.analyzeId = analyzeId;
	}
	

	@Column(name = "COLUMN_ID")
	public Long getColumnId() {
		return columnId;
	}
	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}
	

	@Column(name = "TYPE")
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}

	@Column(name = "TABLE_ID")
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
}