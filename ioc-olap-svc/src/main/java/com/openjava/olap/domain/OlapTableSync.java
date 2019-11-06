package com.openjava.olap.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author linchuangang
 * @create 2019-11-05 15:29
 **/
@Entity
@Table(name = "OLAP_TABLE_SYNC")
@Getter
@Setter
@ApiModel("表同步记录")
public class OlapTableSync implements Persistable<Long>,Serializable{

    @Id
    @Column(name = "SYNC_ID")
    @ApiModelProperty("主键ID")
    private Long syncId;

    @Column(name = "DATABASE_ID")
    @ApiModelProperty("数据库id")
    private String databaseId;

    @Column(name = "RESOURCE_ID")
    @ApiModelProperty("资源id")
    private String resourceId;

    @Column(name = "VIRTUAL_TABLE_NAME")
    @ApiModelProperty("虚拟表名")
    private String virtualTableName;

    @Column(name = "TABLE_NAME")
    @ApiModelProperty("真实表名，后端统一格式为:OLAP_{resourceId}")
    private String tableName;

    @Column(name = "SUCCESS")
    private Integer success;

    @Column(name = "JOB_ID")
    private String jobId;

    @Column(name = "BUSINESS_ID")
    private String businessId;

    @Nullable
    @Override
    public Long getId() {
        return this.syncId;
    }

    @ApiModelProperty("是否新增")
    @Transient
    private Boolean isNew;

    @Transient
    @Override
    public boolean isNew() {
        if (this.isNew !=null){
            return this.isNew;
        }
        if (this.syncId != null){
            return false;
        }
        return false;
    }
}
