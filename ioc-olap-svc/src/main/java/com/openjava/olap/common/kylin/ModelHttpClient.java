package com.openjava.olap.common.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.ModelsDescDataMapper;
import com.openjava.olap.mapper.kylin.ModelsMapper;
import com.openjava.olap.mapper.kylin.ModelsNewMapper;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@Component
public class ModelHttpClient extends KylinHttpClient {
    public List<ModelsDescDataMapper> list() throws APIException {
        String url = config.address + "/kylin/api/models";
        Class<ModelsDescDataMapper[]> claszz = ModelsDescDataMapper[].class;
        ModelsDescDataMapper[] result = HttpClient.get2(url, config.authorization, claszz);
        return Arrays.asList(result);
    }

    public ModelsDescDataMapper entity(String postman) throws APIException {
        String url = config.address + "/kylin/api/model/" + postman;
        ModelsDescDataMapper result = HttpClient.get2(url, config.authorization, ModelsDescDataMapper.class);
        return result;
    }

    public ModelsMapper create(ModelsMapper body) throws APIException {
        String url = config.address + "/kylin/api/models";
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        ModelsNewMapper result = HttpClient.post2(url, JSON.toJSONString(hash), config.authorization, ModelsNewMapper.class);
        return result.resolve();
    }

    public ModelsMapper update(ModelsMapper body) throws APIException {
        String url = config.address + "/kylin/api/models";
        HashMap hash = new HashMap();
        hash.put("modelDescData", JSON.toJSONString(body.modelDescData));
        hash.put("project", body.project);
        hash.put("modelName", body.modelName);
        ModelsNewMapper result = HttpClient.put2(url, JSON.toJSONString(hash), config.authorization, ModelsNewMapper.class);
        return result.resolve();
    }

    public void delete(String modelsName) throws APIException {
        String url = MessageFormat.format("{0}/kylin/api/models/{1}", config.address, modelsName);
        HttpClient.delete2(url, "", config.authorization, void.class);
    }
}
