package com.openjava.platform.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TreeNodeVo {
    private String id;
    private String name;
    private ArrayList<TreeNodeVo> row;
}
