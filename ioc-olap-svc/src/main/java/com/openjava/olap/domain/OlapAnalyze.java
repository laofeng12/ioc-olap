package com.openjava.olap.domain;

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
@ApiModel("OLAP分析")
@Entity
@Table(name = "OLAP_ANALYZE")
public class OlapAnalyze implements Persistable<Long>,Serializable {

	@ApiModelProperty("ID")
	private Long analyzeId;
	@ApiModelProperty("NAME")
	private String name;
	@ApiModelProperty("FOLDER_ID")
	private Long folderId;
	@ApiModelProperty("CREATE_TIME")
	private Date createTime;
	@ApiModelProperty("CREATE_ID")
	private Long createId;
	@ApiModelProperty("CREATE_NAME")
	private String createName;
	@ApiModelProperty("UPDATE_ID")
	private Long updateId;
	@ApiModelProperty("UPDATE_TIME")
	private Date updateTime;
	@ApiModelProperty("UPDATE_NAME")
	private String updateName;
	@ApiModelProperty("FLAGS")
	private Integer flags;
	@ApiModelProperty("sql")
	private String sql;
	@ApiModelProperty("立方体id")
	private Long cubeId;

	@ApiModelProperty("是否新增")
    private Boolean isNew;

	@Transient
    @JsonIgnore
    @Override
    public Long getId() {
        return this.analyzeId;
	}

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
    	if(isNew != null) {
    		return isNew;
    	}
    	if(this.analyzeId != null) {
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
	public Long getAnalyzeId() {
		return analyzeId;
	}
	public void setAnalyzeId(Long id) {
		this.analyzeId = id;
	}


	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "FOLDER_ID")
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}


	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	@Column(name = "CREATE_ID")
	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}


	@Column(name = "CREATE_NAME")
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}


	@Column(name = "UPDATE_ID")
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}


	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_TIME")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


	@Column(name = "UPDATE_NAME")
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}


	@Column(name = "FLAGS")
	public Integer getFlags() {
		return flags;
	}
	public void setFlags(Integer flags) {
		this.flags = flags;
	}

	@Column(name = "SQL")
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

	@Column(name = "CUBE_ID")
	public Long getCubeId() {
		return cubeId;
	}
	public void setCubeId(Long cubeId) {
		this.cubeId = cubeId;
	}
}
