package com.openjava.olap.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {
    public int code;
    public String info;
    public T data;

    public static int SUCCESS_CODE=200;
    public static int FAIL_CODE=400;
    public static int EXCEPTION_CODE=500;

    public static<W> Response<W> error(){
        Response<W> response=new Response<W>();
        response.code=EXCEPTION_CODE;
        response.info="请求超时,请稍后再试！";
        return response;
    }

    public static<W> Response<W> fail(String info){
        Response<W> response=new Response<W>();
        response.code=FAIL_CODE;
        response.info=info;
        return response;
    }

    public static<W> Response<W> success(String info,W data){
        Response<W> response=new Response<W>();
        response.code=SUCCESS_CODE;
        response.data=data;
        response.info=info;
        return response;
    }
}
