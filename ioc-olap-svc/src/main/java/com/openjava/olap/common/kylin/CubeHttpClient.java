package com.openjava.olap.common.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.*;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class CubeHttpClient extends KylinHttpClient {
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
        CubeMapper[] result = HttpClient.get2(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    public List<CubeMapper> list(Integer limit, Integer offset) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}", config.address, limit.toString(), offset.toString());
        Class<CubeMapper[]> claszz = CubeMapper[].class;
        CubeMapper[] result = HttpClient.get2(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    public CubeMapper get(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}", config.address, cubeName);
        CubeMapper result = HttpClient.get2(url, config.authorization, CubeMapper.class);
        return result;
    }

    public CubeDescNewMapper create(CubeDescMapper cube) throws APIException {
        String url = config.address + "/kylin/api/cubes";
        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        CubeDescNewMapper result = HttpClient.post2(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
        return result;
    }

    public CubeDescNewMapper update(CubeDescMapper cube) throws APIException {
        String url = config.address + "/kylin/api/cubes";
        HashMap hash = new HashMap();
        hash.put("cubeDescData", JSON.toJSONString(cube.cubeDescData));
        hash.put("project", cube.project);
        hash.put("cubeName", cube.cubeName);
        CubeDescNewMapper result = HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, CubeDescNewMapper.class);
        return result;
    }

    public CubeDescDataMapper desc(String cubeName) throws APIException {
        String url = config.address + "/kylin/api/cube_desc/" + cubeName;
        Class<CubeDescDataMapper[]> claszz = CubeDescDataMapper[].class;
        CubeDescDataMapper[] result = HttpClient.get2(url, config.authorization, claszz);
        if (result == null || result.length == 0) {
            return null;
        } else {
            return result[0];
        }
    }

    public void clone(String cubeName, String cubeNameClone, String projectName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/clone", config.address, cubeName);
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("cubeName", cubeNameClone);
        hash.put("project", projectName);
        HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public void build(String cubeName, Long start, Long end) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "BUILD");
        hash.put("startTime", start);
        hash.put("endTime", end);
        HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public void refresh(String cubeName, Long start, Long end) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "REFRESH");
        hash.put("startTime", start);
        hash.put("endTime", end);
        HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public void merge(String cubeName, Long start, Long end) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "MERGE");
        hash.put("startTime", start);
        hash.put("endTime", end);
        HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public void disable(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/disable", config.address, cubeName);
        HttpClient.put2(url, "", config.authorization, String.class);
    }

    public void enable(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/enable", config.address, cubeName);
        HttpClient.put2(url, "", config.authorization, String.class);
    }

    public QueryResultMapper query(String sql, Integer offset, Integer limit, String project) throws APIException {
        HashMap hash = new HashMap();
        hash.put("sql", sql);
        hash.put("offset", offset);
        hash.put("limit", limit);
        hash.put("project", project);
        hash.put("acceptPartial", true);
        String url = MessageFormat.format("{0}/kylin/api/query", config.address);
        QueryResultMapper mapper = HttpClient.post2(url, JSON.toJSONString(hash), config.authorization, QueryResultMapper.class);
        return mapper;
    }

    public QueryTableColumnMapper[] queryTableColumn(String project) throws APIException {
        String url = config.address + "/kylin/api/tables_and_columns?project=" + project;
        Class<QueryTableColumnMapper[]> clazz = QueryTableColumnMapper[].class;
        QueryTableColumnMapper[] result = HttpClient.get2(url, config.authorization, clazz);
        return result;
    }

    public void delete(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}", config.address, cubeName);
        HttpClient.delete2(url, "", config.authorization, String.class);
    }

    public List<CubeHbaseMapper> hbase(String cubeName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/hbase", config.address, cubeName);
        Class<CubeHbaseMapper[]> clazz = CubeHbaseMapper[].class;
        return Arrays.asList(HttpClient.get2(url, config.authorization, clazz));
    }
}
