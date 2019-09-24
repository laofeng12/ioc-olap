package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.openjava.olap.common.JsonUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelsNewMapper {
    @JSONField(name = "uuid")
    public String uuid;
    @JSONField(name = "modelDescData")
    public String modelDescData;
    @JSONField(name = "modelName")
    public String modelName;
    @JSONField(name = "successful")
    public boolean successful;
    @JSONField(name = "message")
    public String message;
    @JSONField(name = "project")
    public String project;
    @JSONField(name = "ccInCheck")
    public String ccInCheck;
    @JSONField(name = "seekingExprAdvice")
    public boolean seekingExprAdvice;

    public ModelsMapper resolve(){
        ModelsMapper mapper=new ModelsMapper();
        mapper.modelDescData= JsonUtil.jsonToPojo(modelDescData,ModelsDescDataMapper.class);
        mapper.uuid=uuid;
        mapper.modelName=modelName;
        mapper.project=project;
        mapper.ccInCheck=ccInCheck;
        mapper.message=message;
        mapper.seekingExprAdvice=seekingExprAdvice;
        mapper.successful=successful;
        return mapper;
    }
}
