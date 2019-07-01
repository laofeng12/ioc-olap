package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.EncodingDataMapper;
import com.openjava.platform.mapper.kylin.ProjectDescDataMapper;
import com.openjava.platform.mapper.kylin.ProjectMapper;
import com.openjava.platform.mapper.kylin.TableStructureMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;

@Api(tags = "表结构接口")
@RestController
@RequestMapping("/olap/apis/TableStructure")
public class TableStructureAction extends KylinAction {

    @ApiOperation(value = "获取表结构信息")
    @RequestMapping(value = "/List", method = RequestMethod.GET)
    public TableStructureMapper List() {
        String url = config.address + "/kylin/api/tables/postman/KYLIN.KYLIN_SALES";
        Class<TableStructureMapper> clazz = (Class<TableStructureMapper>) new TableStructureMapper().getClass();
        TableStructureMapper result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }
}
