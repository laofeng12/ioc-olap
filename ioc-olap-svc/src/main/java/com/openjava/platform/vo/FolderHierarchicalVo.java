package com.openjava.platform.vo;

import com.openjava.platform.domain.OlapFolder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FolderHierarchicalVo<T> extends OlapFolder {
    private List<T> leafs;

    public FolderHierarchicalVo(){
        leafs=new ArrayList<T>();
    }
}
