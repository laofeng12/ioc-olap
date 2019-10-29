package com.openjava.olap.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 调用数据湖子系统的接口时相关配置信息
 * @author linchuangang
 * @create 2019-10-28 16:30
 **/
@ConfigurationProperties(prefix = "olap.datalake")
@Component
@Data
public class DataLakeConfig {
    /**数据湖模块地址**/
    private String host;
    /**批量获取同步任务状态接口地址**/
    private String querySyncJobStateUrl;
}
