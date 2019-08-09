package com.openjava.platform.mapper.kylin;

import com.openjava.platform.domain.OlapDatalaketable;
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
