package com.openjava.platform.api.kylin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.openjava.platform.common.KylinConfig;
import com.openjava.platform.common.ResponseMapper;
import com.openjava.platform.mapper.UserDto;
import com.openjava.platform.mapper.UserTypeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.openjava.platform.common.HttpClient;
import org.ljdp.common.http.HttpClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags="用户接口")
@RestController
@RequestMapping("/olap/apis/user")
public class UserAction extends KylinAction
{
    @ApiOperation(value = "授权")
    @RequestMapping(value="/Authentication",method= RequestMethod.GET)
    public String Authentication()
    {
        String aa=HttpClient.get("http://www.baidu.com",String.class);
        return aa;
    }

    @ApiOperation(value = "测试POST")
    @RequestMapping(value="/PTest",method= RequestMethod.POST)
    public String PTest()
    {
        UserDto dto=new UserDto();
        dto.NAME="阿萨德爱是的";
        dto.Type=new UserTypeDto();
        dto.Type.ID="gg";
        dto.Type.NAME="存储层";
        String pp= JSON.toJSONString(dto, SerializerFeature.DisableCircularReferenceDetect);
        ResponseEntity<ResponseMapper> aa=HttpClient.post("http://localhost:53466/api/User/Save",pp, ResponseMapper.class);
        return aa.getBody().info;
    }
}
