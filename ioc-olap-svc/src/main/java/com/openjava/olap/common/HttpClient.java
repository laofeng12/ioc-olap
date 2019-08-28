package com.openjava.olap.common;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpClient {

    public static <T> T get(String url,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.GET,authorization,"",clazz,"");
    }

    public static <T> T post(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.POST,authorization,requestJson,clazz,successMsg);
    }

    public static <T> T post(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.POST,authorization,requestJson,clazz,"");
    }

    public static <T> T put(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.PUT,authorization,requestJson,clazz,successMsg);
    }

    public static <T> T put(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.PUT,authorization,requestJson,clazz,"");
    }

    public static <T> T delete(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.DELETE,authorization,requestJson,clazz,successMsg);
    }

    public static <T> T delete(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.DELETE,authorization,requestJson,clazz,"");
    }

    private static <T> T exeute(String url,HttpMethod method,String authorization,String requestJson,Class<T> clazz,String successMsg){
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
}
