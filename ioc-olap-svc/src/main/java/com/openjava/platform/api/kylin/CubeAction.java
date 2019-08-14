package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.common.JsonUtil;
import com.openjava.platform.common.Response;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jxl.write.DateTime;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.result.DataApiResponse;
import org.ljdp.secure.sso.SsoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
    public List<CubeMapper> list(Integer limit, Integer offset) {
        String url = MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}", config.address, limit.toString(), offset.toString());
        Class<CubeMapper[]> claszz=CubeMapper[].class;
        CubeMapper[] result = HttpClient.get(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    @ApiOperation(value = "创建立方体")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CubeDescNewMapper create(CubeDescMapper cube, String modelName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //写死一组COUNT
        MeasureMapper measire = new MeasureMapper();
        measire.setName("_COUNT_");
        measire.setShowDim(true);
        FunctionMapper function = new FunctionMapper();
        function.setExpression("COUNT");
        function.setReturntype("bigint");
        ParameterMapper parameter = new ParameterMapper();
        parameter.setType("constant");
        parameter.setValue("1");
        function.setParameter(parameter);
        measire.setFunction(function);
        cube.cubeDescData.measures.add(measire);
//
//        for (MeasureMapper measure : cube.cubeDescData.measures) {
//            if (measure.function.getExpression().equals("AVG")) {
//                measure.function.setExpression("SUM");
//            }
//        }
        String url = config.address + "/kylin/api/cubes";

        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        CubeDescNewMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
        if (result == null) {
            //立方体不成功则删除models
            modelsAction.delete(modelName);
            throw new APIException("网络错误!");
        }
        return result;
    }

    @ApiOperation(value = "修改立方体")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public CubeDescNewMapper update(CubeDescMapper cube, String modelName) throws APIException {
        String url = config.address + "/kylin/api/cubes";
        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        hash.put("cubeName", cube.cubeName);
        CubeDescNewMapper result = HttpClient.post(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
        if (result == null) {
            throw new APIException("网络错误!");
        }
        return result;
    }

    @ApiOperation(value = "获取CUBE描述信息")
    @RequestMapping(value = "desc", method = RequestMethod.GET)
    public List<CubeDescDataMapper> desc(String cubeName) {
        String url = config.address + "/kylin/api/cube_desc/" + cubeName;
        Class<CubeDescDataMapper[]> claszz=CubeDescDataMapper[].class;
        CubeDescDataMapper[] result = HttpClient.get(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    @ApiOperation(value = "克隆CUBE")
    @RequestMapping(value = "clone", method = RequestMethod.PUT)
    public void clone(String cubeName, String projectName) {
        String url = config.address + "/kylin/api/cubes/myCube/clone";
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("cubeName", cubeName);
        hash.put("project", projectName);
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "编译CUBE")
    @RequestMapping(value = "build", method = RequestMethod.PUT)
    public void build(String cubeName, Date start, Date end) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "BUILD");
        hash.put("startTime", start.getTime());
        hash.put("endTime", end.getTime());
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "刷新CUBE")
    @RequestMapping(value = "refresh", method = RequestMethod.PUT)
    public void refresh(String cubeName, Date start, Date end) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "REFRESH");
        hash.put("startTime", start.getTime());
        hash.put("endTime", end.getTime());
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "合并CUBE")
    @RequestMapping(value = "merge", method = RequestMethod.PUT)
    public void merge(String cubeName, Date start, Date end) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "MERGE");
        hash.put("startTime", start.getTime());
        hash.put("endTime", end.getTime());
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    @ApiOperation(value = "禁用CUBE")
    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    public void disable(String cubeName) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/disable", config.address, cubeName);
        HttpClient.put(url, "", config.authorization, String.class);
    }

    @ApiOperation(value = "启用CUBE")
    @RequestMapping(value = "enable", method = RequestMethod.PUT)
    public void enable(String cubeName) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/enable", config.address, cubeName);
        HttpClient.put(url, "", config.authorization, String.class);
    }

    @ApiOperation(value = "查询立方体数据")
    @RequestMapping(value = "query", method = RequestMethod.POST)
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
    public QueryTableColumnMapper[] queryTableColumn(String project) {
        String url = config.address + "/kylin/api/tables_and_columns?project=" + project;
        Class<QueryTableColumnMapper[]> clazz = QueryTableColumnMapper[].class;
        QueryTableColumnMapper[] result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }

    @ApiOperation(value = "删除CUBE")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public void delete(String cubeName) {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}", config.address, cubeName);
        HttpClient.put(url, "", config.authorization, String.class);
    }

}
