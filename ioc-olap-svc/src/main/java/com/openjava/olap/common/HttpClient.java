package com.openjava.olap.common;

import net.sf.json.JSONObject;
import org.ljdp.component.exception.APIException;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;

public class HttpClient {

    public static <T> T get(String url,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.GET,authorization,"",clazz);
    }

    public static <T> T get2(String url,String authorization,Class<T> clazz) throws APIException {
        return exeute2(url,HttpMethod.GET,authorization,"",clazz);
    }

    public static <T> T post(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.POST,authorization,requestJson,clazz);
    }

    public static <T> T post2(String url,String requestJson,String authorization,Class<T> clazz) throws APIException {
        return exeute2(url,HttpMethod.POST,authorization,requestJson,clazz);
    }

    public static <T> T put(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.PUT,authorization,requestJson,clazz);
    }

    public static <T> T put2(String url,String requestJson,String authorization,Class<T> clazz) throws APIException {
        return exeute2(url,HttpMethod.PUT,authorization,requestJson,clazz);
    }

    public static <T> T delete(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.DELETE,authorization,requestJson,clazz);
    }

    public static <T> T delete2(String url,String requestJson,String authorization,Class<T> clazz) throws APIException {
        return exeute2(url,HttpMethod.DELETE,authorization,requestJson,clazz);
    }

    private static <T> T exeute(String url,HttpMethod method,String authorization,String requestJson,Class<T> clazz){
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if(headers.containsKey("Authorization")){
            headers.set("Authorization",authorization);
        }
        else{
            headers.add("Authorization",authorization);
        }
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<T> response = rest.exchange(url, method,entity,clazz);
        return response.getBody();
    }

    private static <T> T exeute2(String url, HttpMethod method, String authorization, String requestJson, Class<T> clazz) throws APIException {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if(headers.containsKey("Authorization")){
            headers.set("Authorization",authorization);
        }
        else{
            headers.add("Authorization",authorization);
        }
        try {
            HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
            ResponseEntity<T> response = rest.exchange(url, method, entity, clazz);
            return response.getBody();
        }
        catch (HttpServerErrorException ex){
            JSONObject errorEntity = JSONObject.fromObject(ex.getResponseBodyAsString());
            String info = String.valueOf(errorEntity.get("msg"));
            throw new APIException(10002, info);
        }
    }
}
