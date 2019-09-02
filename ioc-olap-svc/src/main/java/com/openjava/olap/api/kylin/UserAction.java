package com.openjava.olap.api.kylin;

import com.openjava.olap.mapper.kylin.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.openjava.olap.common.HttpClient;
import org.ljdp.secure.annotation.Security;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Api(tags="用户接口")
@RestController
@RequestMapping("/olap/apis/user")
public class UserAction extends KylinAction
{
    @ApiOperation(value = "鉴权")
    @RequestMapping(value="/authentication",method= RequestMethod.POST)
    @Security(session = true)
    public UserMapper authentication() {
        String url=config.address+"/kylin/api/user/authentication";
        Class<HashMap<String,UserMapper>> clazz=(Class<HashMap<String,UserMapper>>)new HashMap<String,UserMapper>().getClass();
        HashMap<String,UserMapper> result=HttpClient.post(url,"",config.authorization,clazz);
        return result.get("userDetails");
    }
}
