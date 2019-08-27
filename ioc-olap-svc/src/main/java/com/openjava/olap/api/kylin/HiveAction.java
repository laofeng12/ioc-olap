package com.openjava.olap.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Api(tags = "Hive库接口")
@RestController
@RequestMapping("/olap/apis/Hive")
public class HiveAction extends KylinAction {

    @ApiOperation(value = "读取hive库底下的表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public String[] list(String LibraryName) {
        String url = config.address + "/kylin/api/tables/hive/" + LibraryName;
        String[] result = HttpClient.get(url, config.authorization, String[].class);
        return result;
    }

    @ApiOperation(value = "添加hive库的表到kylin")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public void create(List<String> tableNameList, String cubeName) throws APIException {
        try {
            String TableName = StringUtils.join(tableNameList, ",") + ",";
            String url = config.address + "/kylin/api/tables/" + TableName + "/" + cubeName;
            HashMap hash = new HashMap();
            hash.put("calculate", true);
            HttpClient.post(url, JSON.toJSONString(hash), config.authorization, String.class);
        } catch (Exception e) {
            throw new APIException("拉取表信息错误！");
        }
    }
}
