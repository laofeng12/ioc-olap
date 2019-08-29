package com.openjava.platform.domain;

import java.util.Date;
import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Max;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Persistable;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体
 * @author zy
 *
 */
@ApiModel("构建选择表")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "OLAP_DATALAKETABLE")
public class OlapDatalaketable implements Persistable<Long>,Serializable {
	
	@ApiModelProperty("主键ID")
	@Id
	@Column(name = "ID")
	private Long id;

	@ApiModelProperty("cubeName")
	@Length(min=0, max=200)
	@Column(name = "CUBE_NAME")
	private String cubeName;
	
	@ApiModelProperty("库名")
	@Length(min=0, max=200)
	@Column(name = "ORG_NAME")
	private String orgName;
	
	@ApiModelProperty("库名ID")
	@Column(name = "ORG_ID")
	private Long orgId;
	
	@ApiModelProperty("表名称ID")
	@Length(min=0, max=200)
	@Column(name = "TABLE_ID")
	private String table_id;
	
	@ApiModelProperty("表名称")
	@Length(min=0, max=200)
	@Column(name = "TABLE_NAME")
	private String table_name;

	@ApiModelProperty("RESOURCE_ID")
	@Length(min=0, max=200)
	@Column(name = "RESOURCE_ID")
	private String resourceId;


	@ApiModelProperty("库类型")
	@Length(min=0, max=200)
	@Column(name = "DATABASE")
	private String database;

	@ApiModelProperty("类型")
	@Length(min=0, max=200)
	@Column(name = "TYPE")
	private String type;

	@ApiModelProperty("FILED")
	@Column(name = "FILED")
	private Long filed;
	
	@ApiModelProperty("是否新增")
	@Transient
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
    
}