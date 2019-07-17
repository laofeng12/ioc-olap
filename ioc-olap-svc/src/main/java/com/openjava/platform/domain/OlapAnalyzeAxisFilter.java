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
@ApiModel("OLAP轴条件筛选")
@Entity
@Table(name = "OLAP_ANALYZE_AXIS_FILTER")
public class OlapAnalyzeAxisFilter implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键id")
	private Long id;
	@ApiModelProperty("轴id")
	private Long axisId;
	@ApiModelProperty("是否包含 1 包含 0 不包含")
	private Long isInclude;
	@ApiModelProperty("选中的值列表 逗号分隔")
	private String selectValues;
	
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
	public Long getAnalyzeAxisFilterId() {
		return id;
	}
	public void setAnalyzeAxisFilterId(Long id) {
		this.id = id;
	}
	

	@Column(name = "AXIS_ID")
	public Long getAxisId() {
		return axisId;
	}
	public void setAxisId(Long axisId) {
		this.axisId = axisId;
	}
	

	@Column(name = "IS_INCLUDE")
	public Long getIsInclude() {
		return isInclude;
	}
	public void setIsInclude(Long isInclude) {
		this.isInclude = isInclude;
	}
	

	@Column(name = "SELECT_VALUES")
	public String getSelectValues() {
		return selectValues;
	}
	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}
	
}