package com.openjava.platform.api.kylin;

import com.openjava.platform.common.HttpClient;
import com.openjava.platform.common.Response;
import com.openjava.platform.mapper.kylin.CubeMapper;
import com.openjava.platform.mapper.kylin.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(tags="立方体接口")
@RestController
@RequestMapping("/olap/apis/cube")
public class CubeAction extends KylinAction{

    @ApiOperation(value = "获取所有的CUBE数据")
    @RequestMapping(value="/list",method= RequestMethod.GET)
    public Response<ArrayList<CubeMapper>> list(Integer limit, Integer offset) {
        String url= MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}",config.address,limit.toString(),offset.toString());
        Class<ArrayList<CubeMapper>> clazz=(Class<ArrayList<CubeMapper>>)new ArrayList<CubeMapper>().getClass();
        Response<ArrayList<CubeMapper>> result= HttpClient.get(url,config.authorization,clazz);
        return result;
    }
}
