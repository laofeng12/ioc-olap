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
@ApiModel("立方体")
@Entity
@Table(name = "OLAP_CUBE")
public class OlapCube implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键ID")
	private Long id;
	@ApiModelProperty("立方体名称")
	private String name;
	@ApiModelProperty("描述")
	private String remark;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("创建人id")
	private Long createId;
	@ApiModelProperty("创建人名称")
	private String createName;
	@ApiModelProperty("更新时间")
	private Date updateTime;
	@ApiModelProperty("更新人id")
	private Long updateId;
	@ApiModelProperty("更新人名称")
	private String updateName;

	@ApiModelProperty("0 不可用 1可用")
	private Long flags;

	
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
	public Long getCubeId() {
		return id;
	}
	public void setCubeId(Long id) {
		this.id = id;
	}
	

	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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


	@Column(name = "FLAGS")
	public Long getFlags() {
		return flags;
	}
	public void setFlags(Long flags) {
		this.flags = flags;
	}
}