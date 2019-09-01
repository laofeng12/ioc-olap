package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    /**
     * 将对象转换成json字符串。
     * <p>Title: pojoToJson</p>
     * <p>Description: </p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            return JSON.toJSONString(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            if(StringUtils.isNotBlank(jsonData)){
                T t = JSON.parseObject(jsonData, beanType);
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 将json结果集转化为指定类型的map容器
     *
     * @param jsonData json数据
     * @param beanType map容器对象类型
     * @return
     */
    public static <T> Map<String,T> jsonToTypeMap(String jsonData, Class<T> beanType) {
        try {
            if(StringUtils.isNotBlank(jsonData)){
                Map<String, JSONObject> map = JSON.parseObject(jsonData, Map.class);
                Map<String,T> ret = new HashMap<>();
                for (String key:map.keySet()){
                    JSONObject jsonObject = map.get(key);
                    T t = jsonToPojo(jsonObject.toJSONString(),beanType);
                    ret.put(key,t);
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {

        try {
            if(StringUtils.isNotBlank(jsonData)){
                List<T> list = JSON.parseArray(jsonData, beanType);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }


}
