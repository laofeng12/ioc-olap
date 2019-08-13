package com.ioc.example.job.service;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.api.kylin.KylinAction;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.CubeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Service
@Transactional
public class CubeServiceImpl extends KylinAction implements CubeService {

    @Override
    public void build(String cubeName, Date start, Date end) throws Exception {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "BUILD");
        hash.put("startTime", start.getTime());
        hash.put("endTime", end.getTime());
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, void.class);
    }

    public ArrayList<CubeMapper> list(Integer limit, Integer offset) {
        String url = MessageFormat.format("{0}/kylin/api/cubes?limit={1}&offset={2}", config.address, limit.toString(), offset.toString());
        Class<ArrayList<CubeMapper>> clazz = (Class<ArrayList<CubeMapper>>) new ArrayList<CubeMapper>().getClass();
        ArrayList<CubeMapper> result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }
}
