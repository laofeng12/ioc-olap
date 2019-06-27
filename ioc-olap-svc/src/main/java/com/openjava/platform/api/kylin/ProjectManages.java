package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.openjava.platform.common.Response;
import com.openjava.platform.mapper.kylin.ProjectDescData;
import com.openjava.platform.mapper.kylin.ProjectManage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.openjava.platform.common.HttpClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@Api(tags = "项目管理")
@RestController
@RequestMapping("/platform/api/ProjectManage")
public class ProjectManages extends KylinAction {

    @ApiOperation(value = "列表分页查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Response<ArrayList<ProjectDescData>> search() {

        String url = config.address + "/kylin/api/projects";
        Class<ArrayList<ProjectDescData>> clazz = (Class<ArrayList<ProjectDescData>>) new ArrayList<ProjectDescData>().getClass();
        Response<ArrayList<ProjectDescData>> result = HttpClient.get(url, "", clazz);
        return result;
    }


    /**
     * 保存
     */
    @ApiOperation(value = "保存", nickname = "save", notes = "报文格式：content-type=application/json")
    @RequestMapping(method = RequestMethod.POST)
    public Response doSave(@RequestBody ProjectDescData body) {
        String url = config.address + "/kylin/api/projects";
        String ProjectDescDataJson = JSON.toJSONString(body, SerializerFeature.DisableCircularReferenceDetect);
        Class<ProjectDescData> clazz = (Class<ProjectDescData>) new ProjectDescData().getClass();

        Response<ProjectDescData> result = HttpClient.post(url, ProjectDescDataJson, config.authorization, clazz);

        return result;
    }


    /**
     * 修改
     */
    @ApiOperation(value = "修改", nickname = "Update", notes = "报文格式：content-type=application/json")
    @RequestMapping(method = RequestMethod.POST)
    public Response doUpdate(@RequestBody ProjectManages body) {
//        String url = config.address + "/kylin/api/projects";
        String url = "http://19.104.59.1:7070/kylin/api/projects";


        String ProjectManageJson = JSON.toJSONString(body, SerializerFeature.DisableCircularReferenceDetect);

        Class<ProjectManage> clazz = (Class<ProjectManage>) new ProjectManage().getClass();
        Response<ProjectManage> result = HttpClient.post(url, ProjectManageJson, config.authorization, clazz);

        return result;
    }


    @ApiOperation(value = "删除", nickname = "delete")
    @RequestMapping(method = RequestMethod.POST)
    public Response doDelete(@RequestParam("prj_name") String prj_name) {
        String url = config.address + "/kylin/api/projects";

        Class<ProjectManage> clazz = (Class<ProjectManage>) new ProjectManage().getClass();
        Response<ProjectManage> result = HttpClient.delete(url, "", config.authorization, clazz);

        return result;
    }
}
