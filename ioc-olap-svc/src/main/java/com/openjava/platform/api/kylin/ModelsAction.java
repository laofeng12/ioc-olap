package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.result.ApiResponse;
import org.ljdp.component.result.BasicApiResponse;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Api(tags = "模型接口")
@RestController
@RequestMapping("/olap/apis/Models")
public class ModelsAction extends KylinAction {

    @ApiOperation(value = "获取所有模型接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ArrayList<ModelsDescDataMapper> list() {
        String url = config.address + "/kylin/api/models";
        Class<ArrayList<ModelsDescDataMapper>> clazz = (Class<ArrayList<ModelsDescDataMapper>>) new ArrayList<ModelsDescDataMapper>().getClass();
        ArrayList<ModelsDescDataMapper> result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }

    @ApiOperation(value = "获取指定项目的模型")
    @RequestMapping(value = "/entity", method = RequestMethod.GET)
    public ModelsDescDataMapper entity(String postman) {
        String url = config.address + "/kylin/api/model/" + postman;
        Class<ModelsDescDataMapper> clazz = (Class<ModelsDescDataMapper>) new ModelsDescDataMapper().getClass();
        ModelsDescDataMapper result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "新增模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelsNewMapper create(@RequestBody ModelsMapper body) throws APIException {
        String url = config.address + "/kylin/api/models";
        body.setProject("learn_kylin");
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        Class<ModelsNewMapper> clazz = (Class<ModelsNewMapper>) new ModelsNewMapper().getClass();
        ModelsNewMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, clazz);
        if (result == null) {
            throw new APIException("网络错误!");
        }
        return result;
    }


    @ApiOperation(value = "修改指定的模型")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ModelsNewMapper update(@RequestBody ModelsMapper body) throws APIException {
        String url = config.address + "/kylin/api/models";
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        hash.put("modelName", body.modelName);
        Class<ModelsNewMapper> clazz = (Class<ModelsNewMapper>) new ModelsNewMapper().getClass();
        ModelsNewMapper result = HttpClient.put(url, JSON.toJSONString(hash), config.authorization, clazz);
        if (result == null) {
            throw new APIException("网络错误!");
        }
        return result;
    }

    @ApiOperation(value = "删除指定的模型")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public void delete(String modelsName) {
        String url = MessageFormat.format("{0}/kylin/api/models/{1}", config.address, modelsName);
        HttpClient.delete(url, "", config.authorization, void.class);
    }
}
