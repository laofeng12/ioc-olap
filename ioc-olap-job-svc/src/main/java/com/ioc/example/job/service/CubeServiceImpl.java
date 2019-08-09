package com.ioc.example.job.service;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.api.kylin.KylinAction;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.domain.OlapTimingrefresh;
import com.openjava.platform.service.OlapTimingrefreshService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class CubeServiceImpl extends KylinAction implements CubeService {

    @Resource
    private OlapTimingrefreshService olapTimingrefreshService;

    @Override
    public void build(String cubeName, Date start, Date end) throws Exception {
        String url = MessageFormat.format("{0}/kylin/api/cubes/{1}/rebuild", config.address, cubeName);
        HashMap hash = new HashMap();
        hash.put("buildType", "BUILD");
        hash.put("startTime", start.getTime());
        hash.put("endTime", end.getTime());
        HttpClient.put(url, JSON.toJSONString(hash), config.authorization, void.class);
    }

    @Override
    public List<OlapTimingrefresh> findTableInfo(int frequencyType) throws Exception {
        List<OlapTimingrefresh> timingList = olapTimingrefreshService.findTableInfo(frequencyType);
        return timingList;
    }

    @Override
    public void doSave(OlapTimingrefresh ot) throws Exception {
        olapTimingrefreshService.doSave(ot);
    }
}
