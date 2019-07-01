package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.api.BaseAction;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.common.KylinConfig;
import com.openjava.platform.mapper.kylin.HiveMapper;
import com.openjava.platform.mapper.kylin.ProjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.result.ApiResponse;
import org.ljdp.component.result.BasicApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Api(tags = "Hive库接口")
@RestController
@RequestMapping("/olap/apis/Hive")
public class HiveAction extends KylinAction {

    @ApiOperation(value = "读取hive库底下的表")
    @RequestMapping(value = "/List", method = RequestMethod.GET)
    public String[] List(String LibraryName) {
        String url = config.address + "/kylin/api/tables/hive/" + LibraryName;
        String[] result = HttpClient.get(url, config.authorization, String[].class);
        return result;
    }


    @ApiOperation(value = "添加hive库的表到kylin")
    @RequestMapping(value = "/Create", method = RequestMethod.POST)
    public void Create(@RequestBody HiveMapper body) {
        String TableName = "";
        for (String s : body.tableNameArr) {
            TableName += s.toString() + ",";
        }
        String url = config.address + "/kylin/api/tables/" + TableName + "/" + body.libraryName;
        HashMap hash = new HashMap();
        hash.put("projectDescData", body.calculate);
        HttpClient.post(url, JSON.toJSONString(hash), config.authorization, void.class);
    }
}
