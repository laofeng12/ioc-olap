package com.openjava.olap.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量触发同步任务接口消息返回体
 * @author linchuangang
 * @create 2019-11-07 14:45
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 * @see com.openjava.olap.query.DataLakeJobQueryParam 对应的消息请求体
 **/
@Getter
@Setter
public class DataLakeTriggerJobVo {

    private String businessId;
    private Integer code;
    private String databaseId;
    private String message;
    private String resourceId;
    private Boolean success;
}
