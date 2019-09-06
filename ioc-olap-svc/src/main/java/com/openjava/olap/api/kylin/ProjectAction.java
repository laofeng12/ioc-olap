package com.openjava.olap.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.ProjectHttpClient;
import com.openjava.olap.mapper.kylin.ProjectDescDataMapper;
import com.openjava.olap.mapper.kylin.ProjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Api(tags = "项目接口")
@RestController
@RequestMapping("/olap/apis/Project")
public class ProjectAction extends KylinAction {
    @Autowired
    ProjectHttpClient projectHttpClient;

    @ApiOperation(value = "获取所有的project数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public List<ProjectDescDataMapper> list() throws APIException {
        return projectHttpClient.list();
    }


    @ApiOperation(value = "创建project")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public void create(@RequestBody ProjectDescDataMapper body) throws APIException {
        projectHttpClient.create(body);
    }


    @ApiOperation(value = "修改project")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @Security(session = true)
    public ProjectDescDataMapper update(@RequestBody ProjectMapper body) throws APIException {
        return projectHttpClient.update(body);
    }


    @ApiOperation(value = "删除project")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public void delete(@RequestParam("prj_name") String prj_name) throws APIException {
        projectHttpClient.delete(prj_name);
    }
}
