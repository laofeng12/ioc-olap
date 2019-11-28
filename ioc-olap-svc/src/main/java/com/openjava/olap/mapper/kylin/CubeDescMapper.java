package com.openjava.olap.mapper.kylin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("立方体消息体")
public class CubeDescMapper {

    @ApiModelProperty("立方体详细消息体")
    public CubeDescDataMapper cubeDescData;
    @ApiModelProperty("项目id，默认值使用当前用户的id")
    public String project;
    @ApiModelProperty("唯一标识")
    public String uuid;
    @ApiModelProperty("立方体名称")
    public String cubeName;
}
