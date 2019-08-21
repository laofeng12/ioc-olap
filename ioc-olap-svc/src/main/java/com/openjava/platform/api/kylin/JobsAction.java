package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

@Api(tags = "Jobs接口")
@RestController
@RequestMapping("/olap/apis/Jobs")
public class JobsAction extends KylinAction {


    @ApiOperation(value = "获取正在执行的作业")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public JobsMapper[] list(Long limit, Long offset, String projectName, String cubeName) throws APIException {
        String url = config.address + "/kylin/api/jobs?";
        StringBuffer sBuffer = new StringBuffer(url);
        if (StringUtils.isNotBlank(cubeName)) {
            sBuffer.append("cubeName=" + cubeName + "&");
        }
        sBuffer.append("jobSearchMode=ALL");
        sBuffer.append("&limit=" + limit);
        sBuffer.append("&offset=" + offset);
        sBuffer.append("&projectName=" + projectName);
        sBuffer.append("&timeFilter=1");
        url = sBuffer.toString();
        Class<JobsMapper[]> clazz = JobsMapper[].class;
        JobsMapper[] result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "删除JOB")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public JobsMapper delete(String jobsId) {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/drop", config.address, jobsId);
        Class<JobsMapper> clazz = (Class<JobsMapper>) new JobsMapper().getClass();
        JobsMapper result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "运行")
    @RequestMapping(value = "/resume", method = RequestMethod.PUT)
    @Security(session = true)
    public void resume(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/resume", config.address, jobsId);
        HttpClient.put(url, "{}", config.authorization, String.class);
    }

    @ApiOperation(value = "暂停")
    @RequestMapping(value = "/pause", method = RequestMethod.PUT)
    @Security(session = true)
    public void pause(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/pause", config.address, jobsId);
        HttpClient.put(url, "", config.authorization, String.class);
    }

    @ApiOperation(value = "停止")
    @RequestMapping(value = "/cancel", method = RequestMethod.PUT)
    @Security(session = true)
    public void cancel(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/cancel", config.address, jobsId);
        HttpClient.put(url, "", config.authorization, String.class);
    }

    @ApiOperation(value = "获取构建某一个节点的详细日志")
    @RequestMapping(value = "/output", method = RequestMethod.GET)
    @Security(session = true)
    public JobStepOutputMapper output(String jobsId,String stepId) {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/steps/{2}/output", config.address, jobsId,stepId);
        JobStepOutputMapper result = HttpClient.get(url, config.authorization, JobStepOutputMapper.class);
        return result;
    }
}
