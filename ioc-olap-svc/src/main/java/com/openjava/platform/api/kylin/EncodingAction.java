package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.platform.common.HttpClient;
import com.openjava.platform.mapper.kylin.EncodingDataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Api(tags = "Encoding接口")
@RestController
@RequestMapping("/olap/apis/Encoding")
public class EncodingAction extends KylinAction {

    @ApiOperation(value = "获取Encoding")
    @RequestMapping(value = "/encodingDataType", method = RequestMethod.GET)
    public EncodingDataMapper encodingDataType()  {
        String url = config.address + "/kylin/api/encodings/valid_encodings";
        Class<EncodingDataMapper> clazz = (Class<EncodingDataMapper>) new EncodingDataMapper().getClass();
        EncodingDataMapper result = HttpClient.get(url, config.authorization, clazz);
        return result;
    }


    @ApiOperation(value = "获取Encoding2")
    @RequestMapping(value = "/encodingDataTypeCount", method = RequestMethod.GET)
    public Map<String, Integer> encodingDataTypeCount() {
        String url = config.address + "/kylin/api/cubes/validEncodings";
        Class<ArrayList<Map<String, Integer>>> clazz = (Class<ArrayList<Map<String, Integer>>>) new ArrayList<Map<String, Integer>>().getClass();
        Map<String, Integer> result = (Map<String, Integer>) HttpClient.get(url, config.authorization, clazz);
        return result;
    }
}
