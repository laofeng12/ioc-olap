package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.common.Response;
import com.openjava.platform.mapper.kylin.CubeDescDataMapper;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.mapper.kylin.CubeMapper;
import com.openjava.platform.mapper.kylin.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

    @ApiOperation(value = "获取所有的立方体")
    @RequestMapping(value="/list",method= RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "限制数据量", required = true),
            @ApiImplicitParam(name = "offset", value = "从多少条开始查起", required = true)
    })
    public Response<ArrayList<CubeMapper>> list(Integer limit, Integer offset) {
        String url= MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}",config.address,limit.toString(),offset.toString());
        Class<ArrayList<CubeMapper>> clazz=(Class<ArrayList<CubeMapper>>)new ArrayList<CubeMapper>().getClass();
        Response<ArrayList<CubeMapper>> result= HttpClient.get(url,config.authorization,clazz);
        return result;
    }

    @ApiOperation(value = "创建立方体")
    @RequestMapping(value="/create",method= RequestMethod.POST)
    public Response<CubeDescMapper> create(CubeDescMapper cube) {
        String url=config.address+"/kylin/api/cubes";
        HashMap hash=new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project",cube.project);
        Response<HashMap> result=HttpClient.post(url,JSON.toJSONString(hash),config.authorization,HashMap.class);
        if(result.code==Response.SUCCESS_CODE){
            CubeDescMapper mapper=new CubeDescMapper();
            mapper.project=result.data.get("project").toString();
            mapper.cubeDescData=JSON.parseObject(result.data.get("cubeDescData").toString(), CubeDescDataMapper.class);
            mapper.uuid=result.data.get("uuid").toString();
            return Response.success("创建成功！",mapper);
        }
        else{
            return Response.error();
        }
    }

    @ApiOperation(value = "修改立方体")
    @RequestMapping(value="update",method= RequestMethod.PUT)
    public Response<CubeDescMapper> update(CubeDescMapper cube) {
        String url=config.address+"/kylin/api/cubes";
        HashMap hash=new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project",cube.project);
        hash.put("cubeName",cube.cubeName);
        Response<HashMap> result=HttpClient.put(url,JSON.toJSONString(hash),config.authorization,HashMap.class);
        if(result.code==Response.SUCCESS_CODE){
            CubeDescMapper mapper=new CubeDescMapper();
            mapper.project=result.data.get("project").toString();
            mapper.cubeDescData=JSON.parseObject(result.data.get("cubeDescData").toString(), CubeDescDataMapper.class);
            mapper.uuid=result.data.get("uuid").toString();
            mapper.cubeName=result.data.get("cubeName").toString();
            return Response.success("修改成功！",mapper);
        }
        else{
            return Response.error();
        }
    }
}
