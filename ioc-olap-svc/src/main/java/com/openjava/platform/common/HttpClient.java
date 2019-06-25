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

    public static <T> T get(String url,Class<T> clazz){
        RestTemplate rest = new RestTemplate();
        T result= rest.getForObject(url, clazz);
        return result;
    }

    public static <T> ResponseEntity<T> post(String url,String requestJson,Class<T> clazz){
        RestTemplate rest = new RestTemplate();
        rest.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<T> response = rest.exchange(url, HttpMethod.POST,entity,clazz);
        return response;
    }

    public static <T> ResponseEntity<T> put(String url,String requestJson,Class<T> clazz){
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<T> response = rest.exchange(url, HttpMethod.PUT,entity,clazz);
        return response;
    }

    public static <T> ResponseEntity<T> delete(String url,String requestJson,Class<T> clazz){
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        ResponseEntity<T> response = rest.exchange(url, HttpMethod.DELETE,entity,clazz);
        return response;
    }
}
