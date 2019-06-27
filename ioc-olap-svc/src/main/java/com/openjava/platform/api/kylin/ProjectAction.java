package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.common.Response;
import com.openjava.platform.mapper.kylin.ProjectDescDataMapper;
import com.openjava.platform.mapper.kylin.ProjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Api(tags = "项目接口")
@RestController
@RequestMapping("/olap/apis/Project")
public class ProjectAction extends KylinAction {

    @ApiOperation(value = "获取所有的project数据")
    @RequestMapping(value = "/List", method = RequestMethod.GET)
    public Response<ArrayList<ProjectDescDataMapper>> List() {
        String url = config.address + "/kylin/api/projects";
        Class<ArrayList<ProjectDescDataMapper>> clazz = (Class<ArrayList<ProjectDescDataMapper>>) new ArrayList<ProjectDescDataMapper>().getClass();
        Response<ArrayList<ProjectDescDataMapper>> result = HttpClient.get(url, "", clazz);
        return result;
    }


    @ApiOperation(value = "创建project")
    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public Response Create(@RequestBody ProjectDescDataMapper body) {
        String url = config.address + "/kylin/api/projects";
        String ProjectDescDataJson = JSON.toJSONString(body, SerializerFeature.DisableCircularReferenceDetect);
        Class<ProjectDescDataMapper> clazz = (Class<ProjectDescDataMapper>) new ProjectDescDataMapper().getClass();

        Response<ProjectDescDataMapper> result = HttpClient.post(url, ProjectDescDataJson, config.authorization, clazz);

        return result;
    }


    @ApiOperation(value = "修改project")
    @RequestMapping(value = "/Update", method = RequestMethod.POST)
    public Response Update(@RequestBody ProjectMapper body) {
//        String url = config.address + "/kylin/api/projects";
        String url = "http://19.104.59.1:7070/kylin/api/projects";


        String ProjectManageJson = JSON.toJSONString(body, SerializerFeature.DisableCircularReferenceDetect);

        Class<ProjectMapper> clazz = (Class<ProjectMapper>) new ProjectMapper().getClass();
        Response<ProjectMapper> result = HttpClient.post(url, ProjectManageJson, config.authorization, clazz);

        return result;
    }


    @ApiOperation(value = "删除project")
    @RequestMapping(value = "/Delete", method = RequestMethod.POST)
    public Response Delete(@RequestParam("prj_name") String prj_name) {
        String url = config.address + "/kylin/api/projects";

        Class<ProjectMapper> clazz = (Class<ProjectMapper>) new ProjectMapper().getClass();
        Response<ProjectMapper> result = HttpClient.delete(url, "", config.authorization, clazz);

        return result;
    }
}
