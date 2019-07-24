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
import java.util.HashMap;

@Api(tags = "项目接口")
@RestController
@RequestMapping("/olap/apis/Project")
public class ProjectAction extends KylinAction {

    @ApiOperation(value = "获取所有的project数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ArrayList<ProjectDescDataMapper> list() {
        String url = config.address + "/kylin/api/projects";
        Class<ArrayList<ProjectDescDataMapper>> clazz = (Class<ArrayList<ProjectDescDataMapper>>) new ArrayList<ProjectDescDataMapper>().getClass();
        ArrayList<ProjectDescDataMapper> result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "创建project")
    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    public void create(@RequestBody ProjectDescDataMapper body) {
        String url = config.address + "/kylin/api/projects";
        HashMap hash = new HashMap();
        hash.put("projectDescData", JSON.toJSONString(body));
        HttpClient.post(url, JSON.toJSONString(hash), config.authorization, void.class);
    }


    @ApiOperation(value = "修改project")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public void update(@RequestBody ProjectMapper body) {
        String url = config.address + "/kylin/api/projects";
        HashMap hash = new HashMap();
        hash.put("formerProjectName", body.formerProjectName);
        hash.put("projectDescData", JSON.toJSONString(body.projectDescData));
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }


    @ApiOperation(value = "删除project")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam("prj_name") String prj_name) {
        String url = config.address + "/kylin/api/projects/" + prj_name;
        HttpClient.delete(url, prj_name, config.authorization, void.class);
    }
}
