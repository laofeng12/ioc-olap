package com.openjava.olap.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.common.AuditComponentProxy;
import com.openjava.olap.common.AuditLogEnum;
import com.openjava.olap.common.AuditLogParam;
import com.openjava.olap.dto.ShareUserDto;
import com.openjava.olap.service.OlapShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "共享接口")
@RestController
@RequestMapping("/olap/apis/olapShare")
public class OlapShareAction {
    @Resource
    private OlapShareService olapShareService;

    @Resource
    private AuditComponentProxy auditComponentProxy;

    @ApiOperation(value = "保存共享")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Security(session = true, allowResources = {"OlapRealQuery", "OlapAnalyze", "OlapModel"})
    public void save(Long[] userIds, String sourceType, Long sourceId, String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        if (userIds != null) {
            for (Long userId : userIds) {
                if (userId.equals(Long.parseLong(userVO.getUserId()))) {
                    throw new APIException(400, "不能分享给自己！");
                }
            }
        }
        if (StringUtils.isNotBlank(cubeName)) {
            olapShareService.save(userIds, sourceType, sourceId, Long.parseLong(userVO.getUserId()), userVO.getUserName(), cubeName);
        } else {
            olapShareService.save(userIds, sourceType, sourceId, Long.parseLong(userVO.getUserId()), userVO.getUserName());
        }
        if (sourceType.equalsIgnoreCase("Analyze")){
            AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO) SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                AuditLogEnum.LOG_MODULES_ANALYZE,AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_PRIMARY_MINE,
                AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_SECONDARY_MINE_SHARING, AuditLogEnum.AuditLogEvent.LOG_EVENT_QUERY,
                new ArrayList<Object>(){{add(userIds);add(sourceType);add(sourceId);add(cubeName);}},new ArrayList<Object>(){{}});
            //olap分析 - 共享
            this.auditComponentProxy.saveAudit(param);
        }else if (sourceType.equalsIgnoreCase("RealQuery")){
            //即席查询 - 共享
            AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO) SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                AuditLogEnum.LOG_MODULES_ANALYZE,AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVED_RESULT,
                AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_SHARING, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                new ArrayList<Object>(){{add(userIds);add(sourceType);add(sourceId);add(cubeName);}},new ArrayList<Object>(){{}});
            this.auditComponentProxy.saveAudit(param);
        }else if (sourceType.equalsIgnoreCase("cube")){
            //模型列表-共享
            AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                AuditLogEnum.LOG_MODULES_OLAP_MODEL,AuditLogEnum.LOG_OLAP_MODEL_TITLE_LEVEL_PRIMARY_MODEL_LIST,
                AuditLogEnum.LOG_OLAP_MODEL_TITLE_LEVEL_SECONDARY_MODEL_LIST_SHARE, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                new ArrayList<Object>(){
                    {
                        add(userIds);
                        add(sourceId);
                        add(cubeName);
                        add(sourceType);
                    }
                },new ArrayList<Object>(){{}});
            this.auditComponentProxy.saveAudit(param);
        }
    }

    @ApiOperation(value = "读取共享")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @Security(session = true, allowResources = {"OlapRealQuery", "OlapAnalyze", "OlapModel"})
    public List<ShareUserDto> get(String sourceType, String sourceId, String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        if (StringUtils.isNotBlank(cubeName)) {
            if (sourceType.equalsIgnoreCase("cube")){
                //共享列表-查看
                AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                    AuditLogEnum.LOG_MODULES_OLAP_MODEL,AuditLogEnum.LOG_OLAP_MODEL_TITLE_LEVEL_PRIMARY_SHARE_LIST,
                    AuditLogEnum.LOG_OLAP_MODEL_TITLE_LEVEL_SECONDARY_SHARE_LIST_VIEW, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                    new ArrayList<Object>(){
                        {
                            add(sourceId);
                            add(cubeName);
                            add(sourceType);
                        }
                    },new ArrayList<Object>(){{}});
                this.auditComponentProxy.saveAudit(param);
            }
            return olapShareService.getList(sourceType, sourceId, Long.parseLong(userVO.getUserId()), cubeName);
        } else {
            return olapShareService.getList(sourceType, sourceId, Long.parseLong(userVO.getUserId()));
        }
    }

    @PostMapping(value = "/batchDelete")
    @ApiOperation(value = "批量删除共享")
    public Object batchDelete(@ApiParam(value = "共享id主键列表",name = "shareIds")@RequestBody List<Long> shareIds){
        ResponseEntity<Map<String,Object>> response;
        if (shareIds == null || shareIds.isEmpty()){
            response = ResponseEntity.ok(new HashMap<>());
            return response;
        }
        shareIds.forEach(s->this.olapShareService.doDelete(s));
        response = ResponseEntity.ok(new HashMap<>());
        response.getBody().put("success",true);
        response.getBody().put("msg","删除成功");
        response.getBody().put("data",shareIds);
        return response;
    }

}
