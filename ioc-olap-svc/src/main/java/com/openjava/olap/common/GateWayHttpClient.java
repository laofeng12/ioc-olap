package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.mapper.CustomApiMapper;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.HashMap;

@Component
public class GateWayHttpClient {
    public final Integer REALQUERY_MODULE_TYPE = 10;
    public final Integer ANALYZE_MODULE_TYPE = 6;

    @Resource
    protected GateWayConfig gateWayConfig;

    public HashMap registerApi(CustomApiMapper apiMapper, String token) throws APIException {
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/syncCustomApi", gateWayConfig.address);
        HashMap hashMap;
        try {
            hashMap = HttpClient.post(url, JSON.toJSONString(apiMapper), token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(hashMap);
        return hashMap;
    }

    public void delete(Long customApiId, Integer moduleType, String token) throws APIException {
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/syncCustomApi/{1}/{2}", gateWayConfig.address, moduleType, customApiId);
        HashMap hashMap;
        try {
            hashMap = HttpClient.delete(url, "", token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(hashMap);
    }

    public CustomApiMapper get(Long customApiId, Integer moduleType, String token) throws APIException {
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/syncCustomApi/{1}/{2}", gateWayConfig.address, moduleType, customApiId);
        HashMap hashMap;
        try {
            hashMap = HttpClient.get(url, token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(hashMap);
        return JSON.parseObject(hashMap.get("data").toString(), CustomApiMapper.class);
    }

    private void Validate(HashMap result) throws APIException {
        if (!result.get("code").toString().equals("0")) {
            throw new APIException(400, result.get("message").toString());
        }
    }
}
