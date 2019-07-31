package com.openjava.platform.vo;

import com.openjava.platform.domain.OlapCube;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalyzeCubeVo extends OlapCube {
    @ApiModelProperty("指标树列表")
    private List<TreeVo> measures;
    @ApiModelProperty("维度树列表")
    private List<TreeVo> dimensures;
}
