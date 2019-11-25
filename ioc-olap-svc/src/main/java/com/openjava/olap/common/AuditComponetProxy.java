package com.openjava.olap.common;

import com.openjava.audit.auditManagement.component.AuditComponet;
import com.openjava.audit.auditManagement.vo.AuditLogVO;
import org.ljdp.component.exception.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AuditComponetProxy {
    @Resource
    private AuditComponet auditComponet;

    private Logger logger = LoggerFactory.getLogger(AuditComponetProxy.class);

    ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String OPERATION_SERVICE = "BI分析报表";
    public final Long MANAGER_TYPE = 1L, SELECT_TYPE = 2L, EXPORT_TYPE = 3L, IMPORT_TYPE = 4L;
    public final String OLAP_MODEL_MODULE = "OLAP建模", REAL_QUERY_MODULE = "即席查询", OLAP_ANALYZE_MODULE = "OLAP分析";

    public void audit(String module, String fuctionLev1, String functionLev2, Long type, String recordId, Long fileId, String fileUrl, String dataBeforeOperat
            , String dataAfterOperat) {
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
    }
}
