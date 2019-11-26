package com.openjava.olap.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.common.AuditComponentProxy;
import com.openjava.olap.common.AuditLogEnum;
import com.openjava.olap.common.AuditLogParam;
import com.openjava.olap.domain.OlapFolder;
import com.openjava.olap.service.OlapFolderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * api接口
 *
 * @author xiepc
 */
@Api(tags = "文件夹接口")
@RestController
@RequestMapping("/olap/apis/olapFolder")
public class OlapFolderAction extends BaseAction {

    @Resource
    private OlapFolderService olapFolderService;

    @Resource
    private AuditComponentProxy auditComponentProxy;

    /**
     * 保存
     */
    @ApiOperation(value = "保存", nickname = "save", notes = "报文格式：content-type=application/json")
    @Security(session = true, allowResources = {"OlapAnalyze", "OlapRealQuery"})
    @RequestMapping(method = RequestMethod.POST)
    public OlapFolder doSave(@RequestBody OlapFolder body) throws APIException {
        Date date = new Date();
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        AuditLogParam param = null;
        if (body.getIsNew() == null || body.getIsNew()) {
            if (olapFolderService.checkExsitName(body.getName(), Long.parseLong(userVO.getUserId()))) {
                throw new APIException(400, "名称已经存在！");
            }
            SequenceService ss = ConcurrentSequence.getInstance();
            body.setFolderId(ss.getSequence());
            body.setIsNew(true);
            body.setCreateTime(date);
            body.setCreateId(Long.parseLong(userVO.getUserId()));
            body.setCreateName(userVO.getUserAccount());
            body.setFlags(0);
            OlapFolder dbObj = olapFolderService.doSave(body);
            if (body.getType() != null && body.getType().equalsIgnoreCase("analyze")) {
                param = new AuditLogParam(SsoContext.getRequestId(), userVO, AuditLogEnum.LOG_SERVICE_NAME,
                    AuditLogEnum.LOG_MODULES_ANALYZE, AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_PRIMARY_MINE,
                    AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_SECONDARY_MINE_PERSIST_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                    new ArrayList<Object>() {
                        {
                            add(dbObj);
                        }
                    }, new ArrayList<>());
                this.auditComponentProxy.saveAudit(param);
            }else if (body.getType() != null && body.getType().equalsIgnoreCase("realquery")){
                param = new AuditLogParam(SsoContext.getRequestId(), userVO, AuditLogEnum.LOG_SERVICE_NAME,
                    AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY, AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVED_RESULT,
                    AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_PERSIST_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                    new ArrayList<Object>() {
                        {
                            add(dbObj);
                        }
                    }, new ArrayList<>());
                this.auditComponentProxy.saveAudit(param);
            }
        } else {
            if (olapFolderService.checkExsitName(body.getName(), body.getFolderId(), Long.parseLong(userVO.getUserId()))) {
                throw new APIException(400, "名称已经存在！");
            }
            OlapFolder db = olapFolderService.get(body.getId());
            MyBeanUtils.copyPropertiesNotBlank(db, body);
            db.setIsNew(false);
            db.setUpdateTime(date);
            db.setCreateId(Long.parseLong(userVO.getUserId()));
            db.setCreateName(userVO.getUserAccount());
            olapFolderService.doSave(db);
            if (body.getType() != null && body.getType().equalsIgnoreCase("analyze")) {
                param = new AuditLogParam(SsoContext.getRequestId(),userVO, AuditLogEnum.LOG_SERVICE_NAME,
                    AuditLogEnum.LOG_MODULES_ANALYZE,AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_PRIMARY_MINE,
                    AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_SECONDARY_MINE_EDIT_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                    new ArrayList<Object>(){
                        {
                            add(body);
                        }
                    },new ArrayList<Object>(){{add(db);}});
                this.auditComponentProxy.saveAudit(param);
            }else if (body.getType() != null && body.getType().equalsIgnoreCase("realquery")){
                param = new AuditLogParam(SsoContext.getRequestId(), userVO, AuditLogEnum.LOG_SERVICE_NAME,
                    AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY, AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVED_RESULT,
                    AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_EDIT_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                    new ArrayList<Object>() {
                        {
                            add(body);
                        }
                    }, new ArrayList<Object>(){{add(db);}});
                this.auditComponentProxy.saveAudit(param);
            }
        }

        return body;
    }

    @ApiOperation(value = "删除", nickname = "delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键编码", required = true, paramType = "post"),
    })
    @Security(session = true, allowResources = {"OlapAnalyze", "OlapRealQuery"})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void doDelete(@RequestParam("id") Long id) {
        olapFolderService.doDelete(id);
        AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO) SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
            AuditLogEnum.LOG_MODULES_ANALYZE,AuditLogEnum.LOG_PRIMARY_TITLE_UNITE_SAVED_RESULT_MINE,
            AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_SECONDARY_MINE_DELETE_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
            new ArrayList<Object>(){
                {
                    add(id);
                }
            },new ArrayList<>());
        this.auditComponentProxy.saveAudit(param);
    }

    @ApiOperation(value = "批量删除", nickname = "remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键编码用,分隔", required = true, paramType = "post"),
    })
    @Security(session = true, allowResources = {"OlapAnalyze", "OlapRealQuery"})
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public void doRemove(@RequestParam("ids") String ids) {
        olapFolderService.doRemove(ids);
        AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO) SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
            AuditLogEnum.LOG_MODULES_ANALYZE,AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_PRIMARY_MINE,
            AuditLogEnum.LOG_ANALYZE_TITLE_LEVEL_SECONDARY_MINE_DELETE_FOLDER, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
            new ArrayList<Object>(){
                {
                    add(ids);
                }
            },new ArrayList<>());
        this.auditComponentProxy.saveAudit(param);
    }

    @ApiOperation(value = "获取个人的文件夹列表", nickname = "listWhthPerson")
    @Security(session = true, allowResources = {"OlapAnalyze", "OlapRealQuery"})
    @RequestMapping(value = "/listWhthPerson", method = RequestMethod.GET)
    public List<OlapFolder> listWhthPerson() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        return olapFolderService.getListByCreateId(Long.parseLong(userVO.getUserId()));
    }

    @ApiOperation(value = "根据ID获取单个文件夹信息", nickname = "folder")
    @Security(session = true, allowResources = {"OlapAnalyze", "OlapRealQuery"})
    @RequestMapping(value = "/folder", method = RequestMethod.GET)
    public OlapFolder folder(Long id) {
        return olapFolderService.get(id);
    }
}
