package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@Api(tags = "模型接口")
@RestController
@RequestMapping("/olap/apis/Models")
public class ModelsAction extends KylinAction {

    @ApiOperation(value = "获取所有模型接口")
    @RequestMapping(value = "/List", method = RequestMethod.GET)
    public ArrayList<ModelsDescDataMapper> List() {
        String url = config.address + "/kylin/api/models";
        Class<ArrayList<ModelsDescDataMapper>> clazz = (Class<ArrayList<ModelsDescDataMapper>>) new ArrayList<ModelsDescDataMapper>().getClass();
        ArrayList<ModelsDescDataMapper> result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }

    @ApiOperation(value = "获取指定项目的模型")
    @RequestMapping(value = "/Entity", method = RequestMethod.GET)
    public ArrayList<ModelsDescDataMapper> Entity(String postman) {
        String url = config.address + "/kylin/api/models/" + postman;
        Class<ArrayList<ModelsDescDataMapper>> clazz = (Class<ArrayList<ModelsDescDataMapper>>) new ArrayList<ModelsDescDataMapper>().getClass();
        ArrayList<ModelsDescDataMapper> result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "新增模型")
    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public ModelsMapper Create(@RequestBody ModelsMapper body) {
        String url = config.address + "/kylin/api/models";
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        Class<ModelsMapper> clazz = (Class<ModelsMapper>) new ModelsMapper().getClass();
        ModelsMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "修改指定的模型")
    @RequestMapping(value = "/Update", method = RequestMethod.PUT)
    public ModelsMapper Update(@RequestBody ModelsMapper body) {
        String url = config.address + "/kylin/api/models";
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        hash.put("modelName", body.modelName);
        Class<ModelsMapper> clazz = (Class<ModelsMapper>) new ModelsMapper().getClass();
        ModelsMapper result = HttpClient.put(url, JSON.toJSONString(hash), config.authorization, clazz);
        return result;
    }
}
