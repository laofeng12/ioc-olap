package com.openjava.olap.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体
 * @author xiepc
 *
 */
@ApiModel("即时查询")
@Entity
@Table(name = "OLAP_REAL_QUERY")
public class OlapRealQuery implements Persistable<Long>,Serializable {
	@ApiModelProperty("主键")
	private Long realQueryId;
	@ApiModelProperty("模型名称")
	@Column(name = "CUBE_NAME")
	private String cubeName;
	@ApiModelProperty("文件夹ID")
	private Long folderId;
	@ApiModelProperty("查询结果名称")
	private String name;
	@ApiModelProperty("sql语句")
	private String sql;
	@ApiModelProperty("创建人id")
	private Long createId;
	@ApiModelProperty("创建人账号")
	private String createName;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("更新人id")
	private Long updateId;
	@ApiModelProperty("更新人账号")
	private String updateName;
	@ApiModelProperty("更新时间")
	private Date updateTime;
	@ApiModelProperty("标志 0：正常 1：共享")
	private Integer flags;
	@ApiModelProperty("查询行数")
	private Integer limit;

	@ApiModelProperty("是否新增")
    private Boolean isNew;

	@Transient
    @JsonIgnore
    @Override
    public Long getId() {
        return this.realQueryId;
	}

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
    	if(isNew != null) {
    		return isNew;
    	}
    	if(this.realQueryId != null) {
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
	public Long getRealQueryId() {
		return realQueryId;
	}
	public void setRealQueryId(Long id) {
		this.realQueryId = id;
	}


	@Column(name = "FOLDER_ID")
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}


	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "SQL")
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
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


	@Column(name = "UPDATE_ID")
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}


	@Column(name = "UPDATE_NAME")
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
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


	@Column(name = "FLAGS")
	public Integer getFlags() {
		return flags;
	}
	public void setFlags(Integer flags) {
		this.flags = flags;
	}

	@Column(name = "LIMIT")
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}
}
