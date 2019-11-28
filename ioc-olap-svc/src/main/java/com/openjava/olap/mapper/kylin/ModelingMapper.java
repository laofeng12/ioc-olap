package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import com.openjava.olap.domain.OlapFilterCondidion;
import com.openjava.olap.domain.OlapTimingrefresh;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("模型参数体")
public class ModelingMapper {

    @JSONField(name = "models")
    @ApiModelProperty("模型基础信息")
    public ModelsMapper models;

    @JSONField(name = "cube")
    @ApiModelProperty("立方体消息体")
    public CubeDescMapper cube;

    @JSONField(name = "filterCondidion")
    @ApiModelProperty("过滤条件集合")
    public List<OlapFilterCondidion> filterCondidion;

    @JSONField(name = "timingreFresh")
    @ApiModelProperty("定时构建配置消息体")
    public OlapTimingrefresh timingreFresh;

    @JSONField(name = "olapDatalaketable")
    @ApiModelProperty("用户勾选的表信息消息体")
    public List<CubeDatalaketableNewMapper> cubeDatalaketableNew;

    @JSONField(name = "dimensionlength")
    public Long dimensionLength;

    @JSONField(name = "dimensionFiledLength")
    public Long dimensionFiledLength;

    @JSONField(name = "measureFiledLength")
    public Long measureFiledLength;

    @JSONField(name = "graphData")
    public String graphData;

    @JSONField(name = "relations")
    @ApiModelProperty("虚拟表名与真实表名关联映射关系消息体")
    public List<TableNameRelationMapper> relations;
}
