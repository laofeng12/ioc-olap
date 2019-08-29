package com.openjava.olap.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体
 *
 * @author xiepc
 */
@ApiModel("立方体表")
@Entity
@Table(name = "OLAP_CUBE_TABLE")
public class OlapCubeTable implements Persistable<Long>, Serializable {

    @ApiModelProperty("主键")
    private Long cubeTableId;
    @ApiModelProperty("表中文名称")
    private String name;
    @ApiModelProperty("立方体id")
    private Long cubeId;
    @ApiModelProperty("表名称")
    private String tableName;
    @ApiModelProperty("表别名")
    private String tableAlias;
    @ApiModelProperty("是否是事实表")
    private Integer isDict;
    @ApiModelProperty("数据库名称")
    private String databaseName;
    @ApiModelProperty("是否新增")
    private Boolean isNew;
    @ApiModelProperty("S轴")
    private Long SAxis;
    @ApiModelProperty("Y轴")
    private Long YAxis;
    @ApiModelProperty("joinS轴")
    private Long joinSAxis;
    @ApiModelProperty("joinY轴")
    private Long joinYAxis;

    @ApiModelProperty("别名")
    private String joinAlias;
    @ApiModelProperty("id")
    private String joinId;
    @ApiModelProperty("表名称")
    private String joinTable;

    @ApiModelProperty("前端要存的id")
    private String tableId;

    @Transient
    @JsonIgnore
    @Override
    public Long getId() {
        return this.cubeTableId;
    }

    @JsonIgnore
    @Transient
    @Override
    public boolean isNew() {
        if (isNew != null) {
            return isNew;
        }
        if (this.cubeTableId != null) {
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
    public Long getCubeTableId() {
        return cubeTableId;
    }
    public void setCubeTableId(Long id) {
        this.cubeTableId = id;
    }


    @Column(name = "NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    @Column(name = "CUBE_ID")
    public Long getCubeId() {
        return cubeId;
    }
    public void setCubeId(Long cubeId) {
        this.cubeId = cubeId;
    }


    @Column(name = "TABLE_NAME")
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    @Column(name = "TABLE_ALIAS")
    public String getTableAlias() {
        return tableAlias;
    }
    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }


    @Column(name = "IS_DICT")
    public Integer getIsDict() {
        return isDict;
    }

    public void setIsDict(Integer isDict) {
        this.isDict = isDict;
    }


    @Column(name = "DATABASE_NAME")
    public String getDatabaseName() {
        return databaseName;
    }
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Column(name = "S_AXIS")
    public Long getSAxis() {
        return SAxis;
    }
    public void setSAxis(Long SAxis) {
        this.SAxis = SAxis;
    }

    @Column(name = "Y_AXIS")
    public Long getYAxis() {
        return YAxis;
    }
    public void setYAxis(Long YAxis) {
        this.YAxis = YAxis;
    }


    @Column(name = "JOIN_SAXIS")
    public Long getJoinSAxis() { return joinSAxis; }
    public void setJoinSAxis(Long joinSAxis) { this.joinSAxis = joinSAxis; }


    @Column(name = "JOIN_YAXIS")
    public Long getJoinYAxis() { return joinYAxis; }
    public void setJoinYAxis(Long joinYAxis) { this.joinYAxis = joinYAxis; }


    @Column(name = "JOINALIAS")
    public String getJoinAlias() {
        return joinAlias;
    }
    public void setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
    }

    @Column(name = "JOINID")
    public String getJoinId() {
        return joinId;
    }
    public void setJoinId(String joinId) {
        this.joinId = joinId;
    }

    @Column(name = "JOINTABLE")
    public String getJoinTable() {
        return joinTable;
    }
    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    @Column(name = "TABLEID")
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
