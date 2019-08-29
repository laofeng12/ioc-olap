package com.openjava.platform.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjava.platform.domain.OlapDatalaketable;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.domain.OlapFilterCondidion;
import com.openjava.platform.domain.OlapTimingrefresh;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ModelingMapper {

    @JSONField(name = "models")
    public ModelsMapper models;

    @JSONField(name = "cube")
    public CubeDescMapper cube;

    @JSONField(name = "filterCondidion")
    public List<OlapFilterCondidion> filterCondidion;

    @JSONField(name = "timingreFresh")
    public OlapTimingrefresh timingreFresh;

    @JSONField(name = "olapDatalaketable")
    public List<CubeDatalaketableNewMapper> cubeDatalaketableNew;

    @JSONField(name = "dimensionlength")
    public Long dimensionLength;

    @JSONField(name = "dimensionFiledLength")
    public Long dimensionFiledLength;

    @JSONField(name = "measureFiledLength")
    public Long measureFiledLength;
}
