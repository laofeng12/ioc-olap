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
@ApiModel("文件夹表")
@Entity
@Table(name = "OLAP_CUBE_TABLE")
public class OlapCubeTable implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键")
	private Long id;
	@ApiModelProperty("表中文名称")
	private String name;
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
	private String remark;
	
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
	public Long getCubeTableId() {
		return id;
	}
	public void setCubeTableId(Long id) {
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
	

	@Column(name = "TABLE_NAME")
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	

	@Column(name = "TABLE_ALIAS")
	public String getTableAlias() {
		return tableAlias;
	}
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
	

	@Column(name = "IS_DICT")
	public Long getIsDict() {
		return isDict;
	}
	public void setIsDict(Long isDict) {
		this.isDict = isDict;
	}
	

	@Column(name = "JOIN_TABLE")
	public String getJoinTable() {
		return joinTable;
	}
	public void setJoinTable(String joinTable) {
		this.joinTable = joinTable;
	}
	

	@Column(name = "JOIN_TYPE")
	public String getJoinType() {
		return joinType;
	}
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	

	@Column(name = "PK_KEY")
	public String getPkKey() {
		return pkKey;
	}
	public void setPkKey(String pkKey) {
		this.pkKey = pkKey;
	}
	

	@Column(name = "FK_KEY")
	public String getFkKey() {
		return fkKey;
	}
	public void setFkKey(String fkKey) {
		this.fkKey = fkKey;
	}
	

	@Column(name = "PK_DATA_TYPE")
	public String getPkDataType() {
		return pkDataType;
	}
	public void setPkDataType(String pkDataType) {
		this.pkDataType = pkDataType;
	}
	

	@Column(name = "FK_DATA_TYPE")
	public String getFkDataType() {
		return fkDataType;
	}
	public void setFkDataType(String fkDataType) {
		this.fkDataType = fkDataType;
	}
	

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}