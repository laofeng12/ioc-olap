package com.openjava.olap.mapper.kylin;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("模型基础信息")
public class ModelsMapper {
    @JSONField(name = "uuid")
    @ApiModelProperty("唯一标识")
    public String uuid;
    @JSONField(name = "modelDescData")
    @ApiModelProperty("模型详细信息消息体")
    public ModelsDescDataMapper modelDescData;
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
}
