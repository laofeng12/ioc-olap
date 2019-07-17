package com.openjava.platform.vo;

import com.openjava.platform.domain.OlapAnalyze;
import com.openjava.platform.domain.OlapAnalyzeAxis;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalyzeVo extends OlapAnalyze {
    private List<AnalyzeAxisVo> olapAnalyzeAxes;
}
