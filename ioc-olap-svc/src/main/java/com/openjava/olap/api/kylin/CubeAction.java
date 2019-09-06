package com.openjava.olap.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

@Api(tags = "立方体接口")
@RestController
@RequestMapping("/olap/apis/cube")
public class CubeAction extends KylinAction {

    @Resource
    ModelsAction modelsAction;

    @ApiOperation(value = "获取所有的立方体")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "限制数据量", required = true),
            @ApiImplicitParam(name = "offset", value = "从多少条开始查起", required = true)
    })
    @Security(session = true)
    public List<CubeMapper> list(String cubeName, String projectName, Integer limit, Integer offset) throws APIException {
        String url = config.address + "/kylin/api/cubes?";
        StringBuffer sBuffer = new StringBuffer(url);
        if (StringUtils.isNotBlank(cubeName)) {
            sBuffer.append("cubeName=" + cubeName + "&");
        }
        sBuffer.append("&limit=" + limit);
        sBuffer.append("&offset=" + offset);
        sBuffer.append("&projectName=" + projectName);
        url = sBuffer.toString();
        Class<CubeMapper[]> claszz = CubeMapper[].class;
        CubeMapper[] result = HttpClient.get(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    @ApiOperation(value = "创建立方体")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public CubeDescNewMapper create(CubeDescMapper cube, String modelName) throws APIException {
        //3、avg这个kylin前端上面不显示，但是在我们的配置页面需要加上，avg这个对应kylin是sum
        for (MeasureMapper measure : cube.cubeDescData.measures) {
            if (measure.function.getExpression().equals("AVG")) {
                measure.function.setExpression("SUM");
            }
        }
        String url = config.address + "/kylin/api/cubes";
        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        try {
            CubeDescNewMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
            return result;
        } catch (HttpServerErrorException ex) {
            String error = new String(ex.getResponseBodyAsByteArray());
            JSONObject errorEntity = JSONObject.fromObject(error);
            errorEntity.get("msg");
            String info = String.valueOf(errorEntity.get("msg"));
            throw new APIException(10002, info);
        }
    }

    @ApiOperation(value = "修改立方体")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @Security(session = true)
    public CubeDescNewMapper update(CubeDescMapper cube, String modelName) throws APIException {
        String url = config.address + "/kylin/api/cubes";
        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        hash.put("cubeName", cube.cubeName);
        try {
            CubeDescNewMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
            return result;
        } catch (HttpServerErrorException ex) {
            String error = new String(ex.getResponseBodyAsByteArray());
            JSONObject errorEntity = JSONObject.fromObject(error);
            errorEntity.get("msg");
            String info = String.valueOf(errorEntity.get("msg"));
            throw new APIException(10002, info);
        }
    }

    @ApiOperation(value = "获取CUBE描述信息")
    @RequestMapping(value = "desc", method = RequestMethod.GET)
    @Security(session = true)
    public List<CubeDescDataMapper> desc(String cubeName) {
        String url = config.address + "/kylin/api/cube_desc/" + cubeName;
        Class<CubeDescDataMapper[]> claszz = CubeDescDataMapper[].class;
        CubeDescDataMapper[] result = HttpClient.get(url, config.authorization, claszz);
        if (result == null) {
            List<CubeDescDataMapper> resultList = new ArrayList<CubeDescDataMapper>();
            return resultList;
        } else {
            return Arrays.asList(result);
        }
    }

    @ApiOperation(value = "克隆CUBE")
    @RequestMapping(value = "clone", method = RequestMethod.PUT)
    @Security(session = true)
    public void clone(String cubeName, String cubeNameClone, String projectName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/clone", config.address, cubeName);
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("cubeName", cubeNameClone);
        hash.put("project", projectName);
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "编译CUBE")
    @RequestMapping(value = "build", method = RequestMethod.PUT)
    @Security(session = true)
    public void build(String cubeName, Long start, Long end) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "BUILD");
        hash.put("startTime", start);
        hash.put("endTime", end);
        try {
            HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
        } catch (HttpServerErrorException ex) {
            String error = new String(ex.getResponseBodyAsByteArray());
            JSONObject errorEntity = JSONObject.fromObject(error);
            errorEntity.get("msg");
            String info = String.valueOf(errorEntity.get("msg"));
            throw new APIException(10002, info);
        }
    }

    @ApiOperation(value = "刷新CUBE")
    @RequestMapping(value = "refresh", method = RequestMethod.PUT)
    @Security(session = true)
    public void refresh(String cubeName, Long start, Long end) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "REFRESH");
        hash.put("startTime", start);
        hash.put("endTime", end);
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "合并CUBE")
    @RequestMapping(value = "merge", method = RequestMethod.PUT)
    @Security(session = true)
    public void merge(String cubeName, Long start, Long end) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "MERGE");
        hash.put("startTime", start);
        hash.put("endTime", end);
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "禁用CUBE")
    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    @Security(session = true)
    public boolean disable(String cubeName) {
        try {
            String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/disable", config.address, cubeName);
            HttpClient.put(url, "", config.authorization, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @ApiOperation(value = "启用CUBE")
    @RequestMapping(value = "enable", method = RequestMethod.PUT)
    @Security(session = true)
    public boolean enable(String cubeName) {
        try {
            String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/enable", config.address, cubeName);
            HttpClient.put(url, "", config.authorization, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @ApiOperation(value = "查询立方体数据")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    @Security(session = true)
    public QueryResultMapper query(String sql, Integer offset, Integer limit, String project) {
        HashMap hash = new HashMap();
        hash.put("sql", sql);
        hash.put("offset", offset);
        hash.put("limit", limit);
        hash.put("project", project);
        hash.put("acceptPartial", true);
        String url = MessageFormat.format("{0}/kylin/api/query", config.address);
        QueryResultMapper mapper = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, QueryResultMapper.class);
        return mapper;
    }

    @ApiOperation(value = "获取立方体可查询的表与列")
    @RequestMapping(value = "/queryTableColumn", method = RequestMethod.GET)
    @Security(session = true)
    public QueryTableColumnMapper[] queryTableColumn(String project) {
        String url = config.address + "/kylin/api/tables_and_columns?project=" + project;
        Class<QueryTableColumnMapper[]> clazz = QueryTableColumnMapper[].class;
        QueryTableColumnMapper[] result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }

    @ApiOperation(value = "删除CUBE")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public void delete(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}", config.address, cubeName);
        HttpClient.delete(url, "", config.authorization, String.class);
    }

}
