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
import jxl.write.DateTime;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.result.DataApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public ArrayList<CubeMapper> list(Integer limit, Integer offset) {
        String url= MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}",config.address,limit.toString(),offset.toString());
        Class<ArrayList<CubeMapper>> clazz=(Class<ArrayList<CubeMapper>>)new ArrayList<CubeMapper>().getClass();
        ArrayList<CubeMapper> result= HttpClient.get(url,config.authorization,clazz);
        return result;
    }

    @ApiOperation(value = "创建立方体")
    @RequestMapping(value="/create",method= RequestMethod.POST)
    public CubeDescMapper create(CubeDescMapper cube) {
        String url=config.address+"/kylin/api/cubes";
        HashMap hash=new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project",cube.project);
        HashMap result=HttpClient.post(url,JSON.toJSONString(hash),config.authorization,HashMap.class);
        CubeDescMapper mapper=new CubeDescMapper();
        mapper.project=result.get("project").toString();
        mapper.cubeDescData=JSON.parseObject(result.get("cubeDescData").toString(), CubeDescDataMapper.class);
        mapper.uuid=result.get("uuid").toString();
        return mapper;
    }

    @ApiOperation(value = "修改立方体")
    @RequestMapping(value="update",method= RequestMethod.PUT)
    public CubeDescMapper update(CubeDescMapper cube) {
        String url=config.address+"/kylin/api/cubes";
        HashMap hash=new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project",cube.project);
        hash.put("cubeName",cube.cubeName);
        HashMap result=HttpClient.put(url,JSON.toJSONString(hash),config.authorization,HashMap.class);
        CubeDescMapper mapper=new CubeDescMapper();
        mapper.project=result.get("project").toString();
        mapper.cubeDescData=JSON.parseObject(result.get("cubeDescData").toString(), CubeDescDataMapper.class);
        mapper.uuid=result.get("uuid").toString();
        mapper.cubeName=result.get("cubeName").toString();
        return mapper;
    }

    @ApiOperation(value = "获取CUBE描述信息")
    @RequestMapping(value="desc",method= RequestMethod.GET)
    public CubeDescDataMapper desc(String cubeName) {
        String url=config.address+"/kylin/api/cube_desc/"+cubeName;
        CubeDescDataMapper result=HttpClient.get(url,config.authorization,CubeDescDataMapper.class);
        return result;
    }

    @ApiOperation(value = "克隆CUBE")
    @RequestMapping(value="clone",method= RequestMethod.PUT)
    public void clone(String cubeName,String projectName) {
        String url=config.address+"/kylin/api/cubes/myCube/clone";
        HashMap<String,String> hash=new HashMap<String,String>();
        hash.put("cubeName",cubeName);
        hash.put("project",projectName);
        HttpClient.put(url,JSON.toJSONString(hash),config.authorization,void.class);
    }

    @ApiOperation(value = "编译CUBE")
    @RequestMapping(value="build",method= RequestMethod.PUT)
    public void build(String cubeName, Date start, Date end) {
        String url=MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild",config.address,cubeName);
        HashMap hash=new HashMap();
        hash.put("buildType","BUILD");
        hash.put("startTime",start.getTime());
        hash.put("endTime",end.getTime());
        HttpClient.put(url,JSON.toJSONString(hash),config.authorization,void.class);
    }
}
