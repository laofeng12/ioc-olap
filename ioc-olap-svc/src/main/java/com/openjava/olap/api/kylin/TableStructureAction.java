package com.openjava.olap.api.kylin;

import com.openjava.olap.common.HttpClient;
import com.openjava.olap.common.kylin.TableHttpClient;
import com.openjava.olap.mapper.kylin.TableStructureMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.component.exception.APIException;
import org.ljdp.secure.annotation.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "表结构接口")
@RestController
@RequestMapping("/olap/apis/TableStructure")
public class TableStructureAction extends KylinAction {

    @Autowired
    TableHttpClient tableHttpClient;

    @ApiOperation(value = "获取表结构信息")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Security(session = true)
    public TableStructureMapper list() throws APIException {
        return tableHttpClient.list();
    }
}
