package com.openjava.platform.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class TreeNodeVo {
    private String id;
    private String name;
    private ArrayList<TreeNodeVo> children;
    private Object attrs;

    public TreeNodeVo(){

    }

    public TreeNodeVo(String name,String id){
        this(name,id,null,null);
    }

    public TreeNodeVo(String name,String id,ArrayList<TreeNodeVo> children){
        this(name,id,children,null);
    }

    public TreeNodeVo(String name,String id,ArrayList<TreeNodeVo> children,Object attrs){
        this.name=name;
        this.id=id;
        this.children=children;
        this.attrs=attrs;
    }
}
