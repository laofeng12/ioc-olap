package com.openjava.olap.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体
 * @author x_pc
 *
 */
@ApiModel("立方体关系")
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@Entity
@Table(name = "OLAP_CUBE_TABLE_RELATION")
public class OlapCubeTableRelation implements Persistable<Long>,Serializable {

	@ApiModelProperty("主键id")
	@Id
	@Column(name = "ID")
	private Long id;

	@ApiModelProperty("源表id")
	@Column(name = "TABLE_ID")
	private Long tableId;

	@ApiModelProperty("关联表ID")
	@Column(name = "JOIN_TABLE_ID")
	private Long joinTableId;

	@ApiModelProperty("关联类型")
	@Column(name = "JOIN_TYPE")
	private String joinType;

	@ApiModelProperty("主键列名称")
	@Column(name = "PK_KEY")
	private String pkKey;

	@ApiModelProperty("外键列名称")
	@Column(name = "FK_KEY")
	private String fkKey;

	@ApiModelProperty("主键数据类型")
	@Column(name = "PK_DATA_TYPE")
	private String pkDataType;

	@ApiModelProperty("外键数据类型")
	@Column(name = "FK_DATA_TYPE")
	private String fkDataType;

	@ApiModelProperty("立方体ID")
	@Column(name = "CUBE_ID")
	private Long cubeId;


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
