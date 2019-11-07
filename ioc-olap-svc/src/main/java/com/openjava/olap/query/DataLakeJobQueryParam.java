package com.openjava.olap.query;

import lombok.Builder;
import lombok.Data;

/**
 * 批量触发同步任务、批量获取同步任务状态的参数消息体
 * @author linchuangang
 * @create 2019-10-28 17:34
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 **/
@Data
@Builder
public class DataLakeJobQueryParam {

    private String databaseId;
    private String resourceId;
    private String type;
    private String syncSource;
}
