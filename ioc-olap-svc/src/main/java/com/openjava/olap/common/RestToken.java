package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import com.openjava.admin.component.IocAuthorizationToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.Response;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author linchuangang
 * @create 2019-11-05 18:15
 **/
@Slf4j
@Configuration
@Component
public class RestToken{

    @Resource
    private IocAuthorizationToken iocAuthorizationToken;
    @Autowired
    private OkHttpClient okHttpClient;

    @Bean
    public OkHttpClient okHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);
        return builder.build();
    }

    public String postFormData(String url,Map<String,Object>json)throws Exception{
        Headers headers = getHeaders();
        return post(headers,url,JSON.toJSONString(json),"application/x-www-form-urlencoded;charset=utf-8");
    }

    public String postJson(String url,Object params,String token)throws Exception{
        Headers headers = Headers.of(new HashMap<String, String>(){
            {
                put("Authorization",token);
                put("User-Agent", ((UserVO)SsoContext.getUser()).getUserAgent());
            }
        });
        return post(headers,url,JSON.toJSONString(params),"application/json;charset=utf-8");
    }
    public String postJson(String url,Object params)throws Exception{
        Headers headers = getHeaders();
       return post(headers,url,JSON.toJSONString(params),"application/json;charset=utf-8");
    }
    public String get(String url,Map<String,String>params)throws Exception{
        Headers headers = getHeaders();
        Request request = new Request.Builder()
            .url(getQueryString(url,params).toString())
            .headers(headers)
            .get()
            .build();
        return execNewCall(request);
    }

    public String post(Headers headers,String url,String json,String contentType)throws Exception{
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .headers(headers)
            .build();
        return execNewCall(request);
    }

    private Headers getHeaders()throws Exception{
        String token = this.iocAuthorizationToken.generateAesToken();
        return Headers.of(new HashMap<String, String>(){
            {
                put("Authorization",token);
                put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            }
        });
    }
    private String execNewCall(Request request){
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error("okhttp3 put error >> ex = {}", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return "";
    }

    public static StringBuffer getQueryString(String url,Map<String,String> queries){
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        return sb;
    }
}
