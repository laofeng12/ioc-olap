package com.openjava.olap.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

/**
 * 批量触发同步任务、批量获取同步任务状态的参数消息体
 * @author linchuangang
 * @create 2019-10-28 17:34
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 * @see com.openjava.olap.vo.DataLakeTriggerJobVo 批量触发同步任务返回体
 * @see com.openjava.olap.vo.DataLakeQueryJobStatusVo 批量获取同步任务状态返回体
 **/
@Data
@Builder
public class DataLakeJobQueryParam {

    private String databaseId;
    private String resourceId;
    private String type;
    private String syncSource;
    @JsonIgnore
    private String cubeName;
}
