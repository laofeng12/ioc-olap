package com.openjava.olap.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.common.kylin.ModelHttpClient;
import com.openjava.olap.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ModelHttpClient modelHttpClient;
    @Autowired
    CubeHttpClient cubeHttpClient;

    @ApiOperation(value = "获取所有的立方体")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "限制数据量", required = true),
            @ApiImplicitParam(name = "offset", value = "从多少条开始查起", required = true)
    })
    @Security(session = true)
    public List<CubeMapper> list(String cubeName, String projectName, Integer limit, Integer offset) throws APIException {
        return cubeHttpClient.list(cubeName,projectName,limit,offset);
    }

    @ApiOperation(value = "创建立方体")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public CubeDescNewMapper create(CubeDescMapper cube) throws APIException {
        return cubeHttpClient.create(cube);
    }

    @ApiOperation(value = "修改立方体")
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    @Security(session = true)
    public CubeDescNewMapper update(CubeDescMapper cube) throws APIException {
       return cubeHttpClient.update(cube);
    }

    @ApiOperation(value = "获取CUBE描述信息")
    @RequestMapping(value = "desc", method = RequestMethod.GET)
    @Security(session = true)
    public List<CubeDescDataMapper> desc(String cubeName) throws APIException {
        return cubeHttpClient.desc(cubeName);
    }

    @ApiOperation(value = "克隆CUBE")
    @RequestMapping(value = "clone", method = RequestMethod.PUT)
    @Security(session = true)
    public void clone(String cubeName, String cubeNameClone, String projectName) throws APIException {
        cubeHttpClient.clone(cubeName,cubeNameClone,projectName);
    }

    @ApiOperation(value = "编译CUBE")
    @RequestMapping(value = "build", method = RequestMethod.PUT)
    @Security(session = true)
    public void build(String cubeName, Long start, Long end) throws APIException {
        cubeHttpClient.build(cubeName,start,end);
    }

    @ApiOperation(value = "刷新CUBE")
    @RequestMapping(value = "refresh", method = RequestMethod.PUT)
    @Security(session = true)
    public void refresh(String cubeName, Long start, Long end) throws APIException {
        cubeHttpClient.refresh(cubeName,start,end);
    }

    @ApiOperation(value = "合并CUBE")
    @RequestMapping(value = "merge", method = RequestMethod.PUT)
    @Security(session = true)
    public void merge(String cubeName, Long start, Long end) throws APIException {
        cubeHttpClient.merge(cubeName,start,end);
    }

    @ApiOperation(value = "禁用CUBE")
    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    @Security(session = true)
    public boolean disable(String cubeName) throws APIException {
       cubeHttpClient.disable(cubeName);
       return true;
    }

    @ApiOperation(value = "启用CUBE")
    @RequestMapping(value = "enable", method = RequestMethod.PUT)
    @Security(session = true)
    public boolean enable(String cubeName) throws APIException {
        cubeHttpClient.enable(cubeName);
        return true;
    }

    @ApiOperation(value = "查询立方体数据")
    @RequestMapping(value = "query", method = RequestMethod.POST)
    @Security(session = true)
    public QueryResultMapper query(String sql, Integer offset, Integer limit, String project) throws APIException {
        return cubeHttpClient.query(sql,offset,limit,project);
    }

    @ApiOperation(value = "获取立方体可查询的表与列")
    @RequestMapping(value = "/queryTableColumn", method = RequestMethod.GET)
    @Security(session = true)
    public QueryTableColumnMapper[] queryTableColumn(String project) throws APIException {
       return cubeHttpClient.queryTableColumn(project);
    }

    @ApiOperation(value = "删除CUBE")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public void delete(String cubeName) throws APIException {
        cubeHttpClient.delete(cubeName);
    }

}
