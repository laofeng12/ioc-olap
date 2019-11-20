package com.openjava.olap.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量获取同步任务状态 接口返回消息体
 * @author linchuangang
 * @create 2019-11-07 14:40
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 **/
@Getter
@Setter
public class DataLakeQueryJobStatusVo {

    private String businessId;
    private String   resourceId;
    private Integer status;
    private Boolean  success;
    private String cubeName;
}
