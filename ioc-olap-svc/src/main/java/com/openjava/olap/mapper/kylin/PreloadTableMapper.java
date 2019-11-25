package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author linchuangang
 * @create 2019-11-22 16:26
 **/
@Data
public class PreloadTableMapper {

    @JSONField(name="result.loaded")
    private List<String> loaded;
    @JSONField(name = "result.unloaded")
    private List<String> unloaded;
}
