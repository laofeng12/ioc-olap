package com.openjava.olap.vo;

import com.openjava.olap.domain.OlapAnalyze;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnalyzeVo extends OlapAnalyze {
    private List<AnalyzeAxisVo> olapAnalyzeAxes;
}
