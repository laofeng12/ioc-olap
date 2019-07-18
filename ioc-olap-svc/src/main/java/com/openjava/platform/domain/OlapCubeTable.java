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
@ApiModel("立方体表")
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
	private Integer isDict;
	@ApiModelProperty("数据库名称")
	private String databaseName;
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
	public Integer getIsDict() {
		return isDict;
	}
	public void setIsDict(Integer isDict) {
		this.isDict = isDict;
	}
	

	@Column(name = "DATABASE_NAME")
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}