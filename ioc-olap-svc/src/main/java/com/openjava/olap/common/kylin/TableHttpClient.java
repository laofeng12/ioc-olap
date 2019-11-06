package com.openjava.olap.common.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.EncodingDataMapper;
import com.openjava.olap.mapper.kylin.TableStructureMapper;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Component
public class TableHttpClient extends KylinHttpClient {

    public EncodingDataMapper encodingDataType() throws APIException {
        String url = config.address + "/kylin/api/encodings/valid_encodings";
        Class<EncodingDataMapper> clazz = (Class<EncodingDataMapper>) new EncodingDataMapper().getClass();
        EncodingDataMapper result = HttpClient.get2(url, config.authorization, clazz);
        return result;
    }

    public HashMap encodingDataTypeCount() throws APIException {
        String url = config.address + "/kylin/api/cubes/validEncodings";
        HashMap result = HttpClient.get2(url, config.authorization, HashMap.class);
        return result;
    }

    public TableStructureMapper list() throws APIException {
        String url = config.address + "/kylin/api/tables/postman/KYLIN.KYLIN_SALES";
        Class<TableStructureMapper> clazz = (Class<TableStructureMapper>) new TableStructureMapper().getClass();
        TableStructureMapper result = HttpClient.get2(url, config.authorization, clazz);
        return result;
    }
}
