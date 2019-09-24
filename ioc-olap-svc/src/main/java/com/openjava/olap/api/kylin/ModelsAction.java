package com.openjava.olap.api.kylin;

import com.openjava.olap.common.kylin.ModelHttpClient;
import com.openjava.olap.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "模型接口")
@RestController
@RequestMapping("/olap/apis/Models")
public class ModelsAction extends KylinAction {
    @Autowired
    protected ModelHttpClient modelHttpClient;

    @ApiOperation(value = "获取所有模型接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public List<ModelsDescDataMapper> list() throws APIException {
        return modelHttpClient.list();
    }

    @ApiOperation(value = "获取指定项目的模型")
    @RequestMapping(value = "/entity", method = RequestMethod.GET)
    @Security(session = true)
    public ModelsDescDataMapper entity(String postman) throws APIException {
        return modelHttpClient.entity(postman);
    }


    @ApiOperation(value = "新增模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public ModelsMapper create(@RequestBody ModelsMapper body) throws APIException {
        return modelHttpClient.create(body);
    }


    @ApiOperation(value = "修改指定的模型")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @Security(session = true)
    public ModelsMapper update(@RequestBody ModelsMapper body) throws APIException {
        return modelHttpClient.update(body);
    }

    @ApiOperation(value = "删除指定的模型")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public void delete(String modelsName) throws APIException {
        modelHttpClient.delete(modelsName);
    }
}
