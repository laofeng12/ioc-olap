package com.openjava.olap.mapper.kylin;

import com.openjava.olap.common.CubeFlags;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@ApiModel("立方体")
public class CubeMapper {
    public String uuid;
    public Long last_modified;
    public String version;
    public String name;
    public String owner;
    public String descriptor;
    public String display_name;
    public Integer cost;
    @ApiModelProperty("麒麟接口返回的模型状态，基本上在接口上不会使用了")
    public String status;
    public Long create_time_utc;
    public String cuboid_bytes;
    public String cuboid_bytes_recommend;
    public Integer cuboid_last_optimized;
    public String project;
    public ArrayList<SegmentsMapper> segments;
    @ApiModelProperty("模型id")
    public String model;
    public boolean is_streaming;
    @ApiModelProperty("分区构建日期字段")
    public String partitionDateColumn;
    @ApiModelProperty("分区构建开始时间")
    public Long partitionDateStart;
    public boolean isStandardPartitioned;
    @ApiModelProperty("模型大小")
    public Integer size_kb;
    public Integer input_records_count;
    public Integer input_records_size;
    public String modelSource;
    @ApiModelProperty("如果返回的是共享列表，则这个属性为共享id")
    private String shareId;
    @ApiModelProperty("模型业务主键id")
    public Long cubeId;
    /**状态名称**/
    @ApiModelProperty("状态名称")
    private String flagsName;
    /**状态值**/
    @ApiModelProperty("状态值")
    private Integer flags;

    public String getflagsName() {
        return CubeFlags.getByFlags(this.flags);
    }
}
