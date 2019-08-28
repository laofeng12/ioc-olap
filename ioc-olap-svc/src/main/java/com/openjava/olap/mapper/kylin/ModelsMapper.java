package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ModelsMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "modelDescData")
    public ModelsDescDataMapper modelDescData;
    @JSONField(name = "modelName")
    public String modelName;
    @JSONField(name = "successful")
    public boolean successful;
    @JSONField(name = "message")
    public String message;
    @JSONField(name = "metrics")
    public ArrayList<String> metrics;
    @JSONField(name = "project")
    public String project;
    @JSONField(name = "ccInCheck")
    public String ccInCheck;
    @JSONField(name = "seekingExprAdvice")
    public boolean seekingExprAdvice;
}
