package com.openjava.olap.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;
import org.hibernate.validator.constraints.Length;

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
@ApiModel("过滤条件")
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@Entity
@Table(name = "OLAP_FILTER_CONDIDION")
public class OlapFilterCondidion implements Persistable<Long>,Serializable {

	@ApiModelProperty("主键ID")
	@Id
	@Column(name = "ID")
	private Long id;

	@ApiModelProperty("过滤表ID")
	@Column(name = "FILTER_ID")
	private Long filterId;

	@ApiModelProperty("表名称")
	@Length(min=0, max=50)
	@Column(name = "TABLENAME")
	private String tableName;

	@ApiModelProperty("表字段")
	@Length(min=0, max=500)
	@Column(name = "FIELD")
	private String field;

	@ApiModelProperty("过滤方式")
	@Length(min=0, max=500)
	@Column(name = "PATTERN")
	private String pattern;

	@ApiModelProperty("过滤值")
	@Length(min=0, max=500)
	@Column(name = "PARAMETER")
	private String parameter;

	@ApiModelProperty("过滤值")
	@Length(min=0, max=500)
	@Column(name = "PARAMETERBE")
	private String parameterbe;

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
