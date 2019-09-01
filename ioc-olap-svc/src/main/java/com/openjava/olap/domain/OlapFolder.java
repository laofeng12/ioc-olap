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
@ApiModel("文件夹表")
@Entity
@Table(name = "OLAP_FOLDER")
public class OlapFolder implements Persistable<Long>,Serializable {

	@ApiModelProperty("主键ID")
	private Long folderId;
	@ApiModelProperty("文件夹名称")
	private String name;
	@ApiModelProperty("父文件夹ID")
	private Long parentId;
	@ApiModelProperty("文件夹类型")
	private String type;
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
	@ApiModelProperty("标志 0：正常")
	private Integer flags;
	@ApiModelProperty("排序编号")
	private Integer sortNum;

	@ApiModelProperty("是否新增")
    private Boolean isNew;

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
    	if(isNew != null) {
    		return isNew;
    	}
    	if(this.folderId != null) {
    		return false;
    	}
    	return true;
    }

	@Transient
	@JsonIgnore
	@Override
	public Long getId() {
		return this.folderId;
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
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long id) {
		this.folderId = id;
	}


	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	@Column(name = "PARENT_ID")
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}


	@Column(name = "TYPE")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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

	@Column(name = "SORT_NUM")
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

}
