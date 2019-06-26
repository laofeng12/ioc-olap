package com.openjava.platform.api.kylin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.openjava.platform.common.ResponseMapper;
import com.openjava.platform.mapper.ProjectManageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.openjava.platform.common.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.result.ApiResponse;
import org.ljdp.component.result.BasicApiResponse;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(tags = "项目管理")
@RestController
@RequestMapping("/platform/api/ProjectManage")
public class ProjectManage {

    @ApiOperation(value = "列表分页查询")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search() {
        ResponseEntity<ResponseMapper> result = HttpClient.post("http://localhost:7070/kylin/api/projects", "", ResponseMapper.class);
        return result.getBody().info;
    }



    /**
     * 保存
     */
    @ApiOperation(value = "保存", nickname = "save", notes = "报文格式：content-type=application/json")
//    @Security(session = false)
    @RequestMapping(method = RequestMethod.POST)
    public ApiResponse doSave(@RequestBody ProjectManageDto ProjectManage) {


        if (StringUtils.isNotBlank(ProjectManage.formerProjectName.toString())) {
            String projectDescDataJson = JSON.toJSONString(ProjectManage.projectDescData, SerializerFeature.DisableCircularReferenceDetect);
            ResponseEntity<ResponseMapper> result = HttpClient.post("http://localhost:7070/kylin/api/projects", projectDescDataJson, ResponseMapper.class);
        } else {
            ResponseEntity<ResponseMapper> result = HttpClient.post("http://19.104.59.1:7070/kylin/api/projects", "", ResponseMapper.class);
        }
        BasicApiResponse resp = new BasicApiResponse();
        return resp;
    }
}
