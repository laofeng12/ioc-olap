package com.openjava.olap.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomApiMapper {
    @ApiModelProperty("接口描述")
    @JSONField(name = "apiDesc")
    private String apiDesc;
    @ApiModelProperty("接口请求方法")
    @JSONField(name = "apiMethod")
    private String apiMethod;
    @ApiModelProperty("接口名称")
    @JSONField(name = "apiName")
    private String apiName;
    @ApiModelProperty("接口地址")
    @JSONField(name = "apiPaths")
    private String apiPaths;
    @ApiModelProperty("关联业务系统的接口Id")
    @JSONField(name = "customApiId")
    private Long customApiId;
    @ApiModelProperty("请求方式 默认application/json")
    @JSONField(name = "enctype")
    private String enctype;
    @ApiModelProperty("接口所属模块 1:智能API数据查询；6:olap分析；9:数据湖数据推送；10:即席查询")
    @JSONField(name = "moduleType")
    private Integer moduleType;
    @ApiModelProperty("接口所属模块名称")
    @JSONField(name = "moduleTypeName")
    private String moduleTypeName;
    @ApiModelProperty("接口协议")
    @JSONField(name = "apiProtocols")
    private String apiProtocols;
    @ApiModelProperty("接口对外访问地址")
    @JSONField(name = "apiUrl")
    private String apiUrl;
    @ApiModelProperty("接口对外使用token")
    @JSONField(name = "token")
    private String token;
}
