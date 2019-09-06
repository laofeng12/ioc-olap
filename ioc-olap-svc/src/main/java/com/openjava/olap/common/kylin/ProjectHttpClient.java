package com.openjava.olap.common.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.ProjectDescDataMapper;
import com.openjava.olap.mapper.kylin.ProjectMapper;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@Component
public class ProjectHttpClient extends KylinHttpClient {

    public List<ProjectDescDataMapper> list() throws APIException {
        String url = config.address + "/kylin/api/projects";
        Class<ProjectDescDataMapper[]> claszz=ProjectDescDataMapper[].class;
        ProjectDescDataMapper[] result= HttpClient.get2(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    public void create(@RequestBody ProjectDescDataMapper body) throws APIException {
        String url = config.address + "/kylin/api/projects";
        HashMap hash = new HashMap();
        hash.put("projectDescData", JSON.toJSONString(body));
        HttpClient.post2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public ProjectDescDataMapper update(@RequestBody ProjectMapper body) throws APIException {
        String url = config.address + "/kylin/api/projects";
        HashMap hash = new HashMap();
        hash.put("formerProjectName", body.formerProjectName);
        hash.put("projectDescData", JSON.toJSONString(body.projectDescData));
        ProjectDescDataMapper mapper = HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, ProjectDescDataMapper.class);
        return mapper;
    }

    public void delete(@RequestParam("prj_name") String prj_name) throws APIException {
        String url = config.address + "/kylin/api/projects/" + prj_name;
        HttpClient.delete2(url, prj_name, config.authorization, void.class);
    }
}
