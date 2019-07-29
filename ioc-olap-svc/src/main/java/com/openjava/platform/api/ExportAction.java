package com.openjava.platform.api;

import com.openjava.platform.common.Export;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(tags="导出接口")
@RestController
@RequestMapping(value="/olap/apis/export")
public class ExportAction {
    @ApiOperation(value = "导出即时查询", nickname="exportWithRealQuery", notes = "报文格式：content-type=application/json")
    @RequestMapping(value="/exportWithRealQuery",method= RequestMethod.GET)
    public void exportWithRealQuery(@RequestBody QueryResultMapper queryResult, HttpServletResponse response)
    {
        Export.dualDate(queryResult,response);
    }

    @ApiOperation(value = "测试")
    @RequestMapping(value="/callExample1",method= RequestMethod.GET)
    public void callExample1(HttpServletResponse response) throws Exception {
        Export.dualDateExportExcel2(response);
    }



}
