package com.openjava.olap.api.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.HiveHttpClient;
import com.openjava.olap.common.kylin.JobHttpClient;
import com.openjava.olap.mapper.kylin.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@Api(tags = "Jobs接口")
@RestController
@RequestMapping("/olap/apis/Jobs")
public class JobsAction extends KylinAction {
    @Autowired
    JobHttpClient jobHttpClient;

    @ApiOperation(value = "获取正在执行的作业")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public JobsMapper[] list(Long limit, Long offset, String projectName, String cubeName) throws APIException {
        return jobHttpClient.list(limit,offset,projectName,cubeName);
    }


    @ApiOperation(value = "删除JOB")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public JobsMapper delete(String jobsId) throws APIException {
        return jobHttpClient.delete(jobsId);
    }


    @ApiOperation(value = "运行")
    @RequestMapping(value = "/resume", method = RequestMethod.PUT)
    @Security(session = false)
    public void resume(String jobsId) throws APIException {
        jobHttpClient.resume(jobsId);
    }

    @ApiOperation(value = "暂停")
    @RequestMapping(value = "/pause", method = RequestMethod.PUT)
    @Security(session = false)
    public void pause(String jobsId) throws APIException {
        jobHttpClient.pause(jobsId);
    }

    @ApiOperation(value = "停止")
    @RequestMapping(value = "/cancel", method = RequestMethod.PUT)
    @Security(session = true)
    public void cancel(String jobsId) throws APIException {
        jobHttpClient.cancel(jobsId);
    }

    @ApiOperation(value = "获取构建某一个节点的详细日志")
    @RequestMapping(value = "/output", method = RequestMethod.GET)
    @Security(session = true)
    public JobStepOutputMapper output(String jobsId, String stepId) throws APIException {
        return jobHttpClient.output(jobsId,stepId);
    }
}
