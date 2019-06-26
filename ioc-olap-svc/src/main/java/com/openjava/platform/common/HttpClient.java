package com.openjava.platform.common;

import org.apache.commons.io.Charsets;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {

    public static <T> Response<T> get(String url,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.GET,authorization,"",clazz,"");
    }

    public static <T> Response<T> post(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.POST,authorization,requestJson,clazz,successMsg);
    }

    public static <T> Response<T> post(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.POST,authorization,requestJson,clazz,"");
    }

    public static <T> Response<T> put(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.PUT,authorization,requestJson,clazz,successMsg);
    }

    public static <T> Response<T> put(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.PUT,authorization,requestJson,clazz,"");
    }

    public static <T> Response<T> delete(String url,String requestJson,String authorization,Class<T> clazz,String successMsg){
        return exeute(url,HttpMethod.DELETE,authorization,requestJson,clazz,successMsg);
    }

    public static <T> Response<T> delete(String url,String requestJson,String authorization,Class<T> clazz){
        return exeute(url,HttpMethod.DELETE,authorization,requestJson,clazz,"");
    }

    private static <T> Response<T> exeute(String url,HttpMethod method,String authorization,String requestJson,Class<T> clazz,String successMsg){
        try {
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
            return  Response.success(successMsg,response.getBody());
        }
        catch (Exception ex){
            return Response.error();
        }
    }
}
