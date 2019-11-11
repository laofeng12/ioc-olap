package com.openjava.olap.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体
 * @author zy
 *
 */
@ApiModel("构建定时任务")
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@Entity
@Table(name = "OLAP_TIMINGREFRESH")
public class OlapTimingrefresh implements Persistable<Long>,Serializable {

	@ApiModelProperty("主键ID")
	@Id
	@Column(name = "ID")
	private Long timingrefreshId;

	@ApiModelProperty("立方体名称")
	@Length(min=0, max=50)
	@Column(name = "CUBE_NAME")
	private String cubeName;

	@ApiModelProperty("频率类型")
	@Column(name = "FREQUENCYTYPE")
	private Integer frequencytype;

	@ApiModelProperty("间隔")
	@Column(name = "INTERVAL")
	private Integer interval;

	@ApiModelProperty("创建时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date createTime;

	@ApiModelProperty("创建人id")
	@Column(name = "CREATE_ID")
	private Long createId;

	@ApiModelProperty("创建人名称")
	@Length(min=0, max=50)
	@Column(name = "CREATE_NAME")
	private String createName;

	@ApiModelProperty("更新时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	@ApiModelProperty("更新人id")
	@Column(name = "UPDATE_ID")
	private Long updateId;

	@ApiModelProperty("更新人名称")
	@Length(min=0, max=50)
	@Column(name = "UPDATE_NAME")
	private String updateName;

	@ApiModelProperty("最后执行时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FINAL_EXECUTION_TIME")
	private Date finalExecutionTime;

	@ApiModelProperty("下一次执行执行时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NEXT_EXECUTION_TIME")
	private Date nextExecutionTime;


	@ApiModelProperty("是否自动刷新模型")
	@Column(name = "AUTORELOAD")
	private Integer autoReload;

	@ApiModelProperty("开始时间，增量时，需要传参")
	//@Column(name = "BEGIN")
	private transient Long begin;

	@ApiModelProperty("结束时间，增量时，需要传参")
	//@Column(name = "END")
	private transient Long end;

	/**0:定时构建，1:手动构建**/
	@ApiModelProperty("是否手动构建,0:定时构建，1:手动构建")
	@Column(name = "MANUAL")
	private transient Integer manual;

	@ApiModelProperty("是否新增")
	@Transient
    private Boolean isNew;

	@Transient
    @JsonIgnore
	public Long getId() {
        return this.timingrefreshId;
	}

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
    	if(isNew != null) {
    		return isNew;
    	}
    	if(this.timingrefreshId != null) {
    		return false;
    	}
    	return true;
    }

}
