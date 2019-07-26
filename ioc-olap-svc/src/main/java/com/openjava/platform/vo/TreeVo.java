package com.openjava.platform.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TreeVo {
    private String name;
    private String id;
    private ArrayList<TreeNodeVo> children;
    private Object attrs;

    public TreeVo(){

    }

    public TreeVo(String name,String id){
        this(name,id,null,null);
    }

    public TreeVo(String name,String id,ArrayList<TreeNodeVo> children){
        this(name,id,children,null);
    }

    public TreeVo(String name,String id,ArrayList<TreeNodeVo> children,Object attrs){
        this.name=name;
        this.id=id;
        this.children=children;
        this.attrs=attrs;
    }

}
