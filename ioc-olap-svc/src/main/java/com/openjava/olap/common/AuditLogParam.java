package com.openjava.olap.common;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.ljdp.plugin.sys.vo.UserVO;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author linchuangang
 * @create 2019-11-25 15:22
 **/
@Data
public class AuditLogParam {

    private Long requestId;
    private UserVO operator;
    private AuditLogEnum service;
    private AuditLogEnum module;
    private AuditLogEnum primaryTitle;
    private AuditLogEnum secondaryTitle;
    private AuditLogEnum.AuditLogEvent type;
    /**新增、更新时before就是方法里的实体参数，查询时则为方法里所有参数的json格式**/
    private List<Object> before;
    private List<Object> after;

    public AuditLogParam(Long requestId, UserVO operator, AuditLogEnum service, AuditLogEnum module, AuditLogEnum primaryTitle, AuditLogEnum secondaryTitle, AuditLogEnum.AuditLogEvent type, List<Object> before, List<Object> after) {
        Assert.notNull(requestId,"参数不能为空");
        Assert.notNull(operator,"参数不能为空");
        Assert.notNull(service,"参数不能为空");
        Assert.notNull(module,"参数不能为空");
        Assert.notNull(primaryTitle,"参数不能为空");
        Assert.notNull(secondaryTitle,"参数不能为空");
        Assert.notNull(type,"参数不能为空");
        Assert.notNull(before,"参数不能为空");
        Assert.notNull(after,"参数不能为空");
        this.requestId = requestId;
        this.operator = operator;
        this.service = service;
        this.module = module;
        this.primaryTitle = primaryTitle;
        this.secondaryTitle = secondaryTitle;
        this.type = type;
        this.before = before;
        this.after = after;
    }

    public String getBefore() {
        return JSON.toJSONString(before);
    }

    public String getAfter() {
        return JSON.toJSONString(after);
    }
}
