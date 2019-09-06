package com.openjava.olap.common.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.UserMapper;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component
public class UserHttpClient extends KylinHttpClient {
    public UserMapper authentication() throws APIException {
        String url=config.address+"/kylin/api/user/authentication";
        Class<HashMap<String,UserMapper>> clazz=(Class<HashMap<String,UserMapper>>)new HashMap<String,UserMapper>().getClass();
        HashMap<String,UserMapper> result= HttpClient.post2(url,"",config.authorization,clazz);
        return result.get("userDetails");
    }
}
