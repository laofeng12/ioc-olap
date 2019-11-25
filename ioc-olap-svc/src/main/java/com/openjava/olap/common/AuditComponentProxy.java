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
    }

    private final String OPERATION_SERVICE = "BI分析报表";
    public final Long MANAGER_TYPE = 1L, SELECT_TYPE = 2L, EXPORT_TYPE = 3L, IMPORT_TYPE = 4L;
    public final String OLAP_MODEL_MODULE = "OLAP建模", REAL_QUERY_MODULE = "即席查询", OLAP_ANALYZE_MODULE = "OLAP分析";

    public void audit(String module, String fuctionLev1, String functionLev2, Long type, String recordId, Long fileId, String fileUrl, String dataBeforeOperat
            , String dataAfterOperat) {
        try {
            executor.execute(() -> {
                AuditLogVO vo = new AuditLogVO();
                vo.setType(type);
                vo.setOperationService(OPERATION_SERVICE);
                vo.setOperationModule(module);
                vo.setFunctionLev1(fuctionLev1);
                vo.setFunctionLev2(functionLev2);
                vo.setRecordId(recordId);
                vo.setDataBeforeOperat(dataBeforeOperat);
                vo.setDataAfterOperat(dataAfterOperat);
                vo.setFileId(fileId);
                vo.setFileUrl(fileUrl);
                try {
                    auditComponet.saveAuditLog(vo);
                } catch (Exception e) {
                    logger.error("保存审计日志失败！", e);
                }
            });
        }catch (Exception var1){
            logger.error("异步提交执行审计代码保存的任务出错");
        }
    }

    private AuditLogVO getLog(){
        AuditLogVO vo = new AuditLogVO();
        return vo;
    }


}
