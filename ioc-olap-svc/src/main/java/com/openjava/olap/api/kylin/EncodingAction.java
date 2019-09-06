package com.openjava.olap.api.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.common.kylin.TableHttpClient;
import com.openjava.olap.mapper.kylin.EncodingDataMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Api(tags = "Encoding接口")
@RestController
@RequestMapping("/olap/apis/Encoding")
public class EncodingAction extends KylinAction {
    @Autowired
    TableHttpClient tableHttpClient;

    @ApiOperation(value = "获取Encoding")
    @RequestMapping(value = "/encodingDataType", method = RequestMethod.GET)
    @Security(session = true)
    public EncodingDataMapper encodingDataType() throws APIException {
        return tableHttpClient.encodingDataType();
    }


    @ApiOperation(value = "获取Encoding2")
    @RequestMapping(value = "/encodingDataTypeCount", method = RequestMethod.GET)
    @Security(session = true)
    public Map<String, Integer> encodingDataTypeCount() throws APIException {
        return  tableHttpClient.encodingDataTypeCount();
    }
}
