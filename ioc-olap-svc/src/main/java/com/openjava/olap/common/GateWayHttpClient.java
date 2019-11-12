package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.mapper.CustomApiMapper;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/syncCustomApi/{1}/{2}", gateWayConfig.address, moduleType.toString(), customApiId.toString());
        HashMap hashMap;
        try {
            hashMap = HttpClient.delete(url, "", token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(hashMap);
    }

    public CustomApiMapper get(Long customApiId, Integer moduleType, String token) throws APIException {
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/syncCustomApi/{1}/{2}", gateWayConfig.address, moduleType.toString(), customApiId.toString());
        HashMap result;
        try {
            result = HttpClient.get(url, token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(result);
        CustomApiMapper mapper = new CustomApiMapper();
        if (result.get("data") != null) {
            HashMap map = (HashMap) result.get("data");
            if (map.get("customApiId") != null) {
                mapper.setEnctype(map.get("enctype").toString());
                mapper.setCustomApiId(Long.parseLong(map.get("customApiId").toString()));
                mapper.setApiName(map.get("apiName").toString());
                if (map.get("apiDesc") != null)
                    mapper.setApiDesc(map.get("apiDesc").toString());
                mapper.setApiPaths(map.get("apiPaths").toString());
                mapper.setApiUrl(map.get("apiUrl").toString());
                mapper.setApiProtocols(map.get("apiProtocols").toString());
                mapper.setApiMethod(map.get("apiMethod").toString());
                // 增加token返回
                HashMap credential = getCredential(customApiId, moduleType, token);
                Validate(credential);
                map = (HashMap) credential.get("data");
                String secret = map.get("secret").toString();
                String key = map.get("key").toString();
                mapper.setToken(JwtTokenUtil.generateToken(key, secret));
            }
        }
        return mapper;
    }

    private HashMap getCredential(Long customApiId, Integer moduleType, String token) throws APIException {
        String url = MessageFormat.format("{0}/gateway/apis/spInstanceApi/getCustomApiCredential?moduleType={1}&customApiId={2}", gateWayConfig.address, moduleType.toString(), customApiId.toString());
        HashMap result;
        try {
            result = HttpClient.get(url, token, HashMap.class);
        } catch (Exception ex) {
            throw new APIException(400, "网关接口异常！");
        }
        Validate(result);
        return result;
    }

    private void Validate(HashMap result) throws APIException {
        if (!result.get("code").toString().equals("200")) {
            throw new APIException(400, result.get("message").toString());
        }
    }
}
