package com.openjava.olap.common;

import com.openjava.audit.auditManagement.component.AuditComponet;
import com.openjava.audit.auditManagement.vo.AuditLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AuditComponentProxy {
    private AuditComponet auditComponet;

    private Logger logger = LoggerFactory.getLogger(AuditComponentProxy.class);

    private ExecutorService executor;

    public AuditComponentProxy(AuditComponet auditComponet) {
        Assert.notNull(auditComponet,"审计操作组件不能为空");
        this.auditComponet = auditComponet;
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        Assert.notNull(executor,"初始化线程池失败");
    }

    public void saveAudit(AuditLogParam param) {
        try {
            executor.execute(() -> {
                try {
                    AuditLogVO vo = getLog(param);
                    auditComponet.saveAuditLog(vo);
                } catch (Exception e) {
                    logger.error("保存审计日志失败！", e);
                }
            });
        }catch (Exception var1){
            logger.error("异步提交执行审计代码保存的任务出错");
        }
    }

    private AuditLogVO getLog(AuditLogParam param){
        AuditLogVO vo = new AuditLogVO();
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
        return vo;
    }

}
