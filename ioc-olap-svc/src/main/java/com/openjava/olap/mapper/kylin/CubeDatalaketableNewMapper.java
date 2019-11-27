package com.openjava.olap.mapper.kylin;

import com.openjava.olap.domain.OlapDatalaketable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CubeDatalaketableNewMapper {
    public String orgId;
    public String orgName;
    public ArrayList<OlapDatalaketable> tableList;
}
