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
@ApiModel("分享")
@Entity
@Table(name = "OLAP_SHARE")
public class OlapShare implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键ID")
	private Long id;
	@ApiModelProperty("关联表ID")
	private Long fkId;
	@ApiModelProperty("关联表类型 ")
	private String fkType;
	@ApiModelProperty("分享用户ID")
	private Long shareUserId;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("创建人ID")
	private Long createId;
	@ApiModelProperty("创建人名称")
	private String createName;
	
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
	public Long getShareId() {
		return id;
	}
	public void setShareId(Long id) {
		this.id = id;
	}
	

	@Column(name = "FK_ID")
	public Long getFkId() {
		return fkId;
	}
	public void setFkId(Long fkId) {
		this.fkId = fkId;
	}
	

	@Column(name = "FK_TYPE")
	public String getFkType() {
		return fkType;
	}
	public void setFkType(String fkType) {
		this.fkType = fkType;
	}
	

	@Column(name = "SHARE_USER_ID")
	public Long getShareUserId() {
		return shareUserId;
	}
	public void setShareUserId(Long shareUserId) {
		this.shareUserId = shareUserId;
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
	
}