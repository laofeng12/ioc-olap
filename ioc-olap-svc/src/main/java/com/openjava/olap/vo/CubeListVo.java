package com.openjava.olap.vo;

import com.openjava.olap.mapper.kylin.CubeMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CubeListVo {
    private List<CubeMapper> cubeMappers;
    private boolean isNext;

    public CubeListVo(List<CubeMapper> cubeMappers, boolean isNext) {
        this.cubeMappers = cubeMappers;
        this.isNext = isNext;
    }
}
