package com.openjava.olap.common.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.JobStepOutputMapper;
import com.openjava.olap.mapper.kylin.JobsMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.MessageFormat;
@Component
public class JobHttpClient extends KylinHttpClient {

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
        JobsMapper[] result = HttpClient.get2(url, config.authorization, clazz);
        return result;
    }

    public JobsMapper delete(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/drop", config.address, jobsId);
        Class<JobsMapper> clazz = (Class<JobsMapper>) new JobsMapper().getClass();
        JobsMapper result = HttpClient.delete2(url, "", config.authorization, clazz);
        return result;
    }

    public void resume(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/resume", config.address, jobsId);
        HttpClient.put2(url, "{}", config.authorization, String.class);
    }

    public void pause(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/pause", config.address, jobsId);
        HttpClient.put2(url, "", config.authorization, String.class);
    }

    public void cancel(String jobsId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/cancel", config.address, jobsId);
        HttpClient.put2(url, "", config.authorization, String.class);
    }

    public JobStepOutputMapper output(String jobsId, String stepId) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/jobs/{1}/steps/{2}/output", config.address, jobsId, stepId);
        JobStepOutputMapper result = HttpClient.get2(url, config.authorization, JobStepOutputMapper.class);
        return result;
    }
}
