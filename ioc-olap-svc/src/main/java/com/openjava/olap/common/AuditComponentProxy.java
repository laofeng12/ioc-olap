package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import com.openjava.audit.auditManagement.component.AuditComponet;
import com.openjava.audit.auditManagement.vo.AuditLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AuditComponentProxy {
    private AuditComponet auditComponet;

    private Logger logger = LoggerFactory.getLogger(AuditComponentProxy.class);


    public AuditComponentProxy(AuditComponet auditComponet) {
        Assert.notNull(auditComponet,"审计操作组件不能为空");
        this.auditComponet = auditComponet;
    }

    public void saveAudit(AuditLogParam param) {
        try {
            AuditLogVO vo = getLog(param);
            auditComponet.saveAuditLog(vo);
        } catch (Exception e) {
            logger.error("保存审计日志失败！", e);
        }
    }

    private AuditLogVO getLog(AuditLogParam param){
        AuditLogVO vo = new AuditLogVO();
        Assert.notNull(param,"构建日志实体时，参数不能为空");
        vo.setUserId(Long.parseLong(param.getOperator().getUserId()));
        vo.setAccount(param.getOperator().getUserAccount());
        vo.setRequestId(param.getRequestId());
        vo.setOperationModule(param.getModule().getValue());
        vo.setOperationService(param.getService().getValue());
        vo.setFunctionLev1(param.getPrimaryTitle().getValue());
        vo.setFunctionLev2(param.getSecondaryTitle().getValue());
        vo.setDataBeforeOperat(param.getBefore());
        vo.setDataAfterOperat(param.getAfter());
        vo.setType((long)param.getType().getIndex());
        logger.debug("生成的审计日志实体：{}", JSON.toJSONString(vo));
        return vo;
    }

}
