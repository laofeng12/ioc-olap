package com.openjava.olap.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.HiveHttpClient;
import com.openjava.olap.common.kylin.TableHttpClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Api(tags = "Hive库接口")
@RestController
@RequestMapping("/olap/apis/Hive")
public class HiveAction extends KylinAction {
    @Autowired
    HiveHttpClient hiveHttpClient;

    @ApiOperation(value = "读取hive库底下的表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public String[] list(String libraryName) throws APIException {
        return hiveHttpClient.list(libraryName);
    }

    @ApiOperation(value = "添加hive库的表到kylin")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Security(session = true)
    public void create(List<String> tableNameList, String cubeName) throws APIException {
        hiveHttpClient.create(tableNameList, cubeName);
    }
}
