package com.openjava.olap.mapper;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomApiMapper {
    @JSONField(name = "apiDesc")
    private String apiDesc;
    @JSONField(name = "apiMethod")
    private String apiMethod;
    @JSONField(name = "apiName")
    private String apiName;
    @JSONField(name = "apiPaths")
    private String apiPaths;
    @JSONField(name = "customApiId")
    private Long customApiId;
    @JSONField(name = "enctype")
    private String enctype;
    @JSONField(name = "moduleType")
    private Integer moduleType;
    @JSONField(name = "apiProtocols")
    private String apiProtocols;
    @JSONField(name = "apiUrl")
    private String apiUrl;
    @JSONField(name = "token")
    private String token;
}
