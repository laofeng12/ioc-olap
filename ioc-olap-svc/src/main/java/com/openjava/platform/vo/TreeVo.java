package com.openjava.platform.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TreeVo {
    private String title;
    private String id;
    private ArrayList<TreeNodeVo> row;
}
