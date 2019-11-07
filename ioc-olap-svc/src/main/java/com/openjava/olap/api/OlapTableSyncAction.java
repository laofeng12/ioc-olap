package com.openjava.olap.api;

import com.openjava.olap.query.OlapTableSyncParam;
import com.openjava.olap.service.OlapTableSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.secure.annotation.Security;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author linchuangang
 * @create 2019-11-05 15:26
 **/
@Api(tags = "模型关联的表同步到hive的记录")
@RestController
@RequestMapping(value = "/olap/apis/olapSync")
public class OlapTableSyncAction{

    @Resource
    private OlapTableSyncService olapTableSyncService;

    @PostMapping(value = "/save")
    @ApiOperation(value = "保存模型关联的表同步到hive的记录")
    @Security(session = true)
    public Object save(@RequestBody List<OlapTableSyncParam> params)throws Exception{
        ResponseEntity<HashMap<String,Object>> response;
        if (params == null){
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new HashMap<>());
            response.getBody().put("msg","消息体参数不能为空");
            return response;
        }
        response = ResponseEntity.ok(new HashMap<>());
        response.getBody().put("data",this.olapTableSyncService.available(params));
        response.getBody().put("msg","同步成功");
        return response;
    }
}
