package com.ioc.example.job;


import com.openjava.platform.service.OlapAnalyzeAxisFilterService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务例子
 */
@Component
public class ExampleJob {

    @Resource
    private OlapAnalyzeAxisFilterService olapAnalyzeAxisFilterService;//注入bean（例子，按需注入）

    @Scheduled(cron="${schedule.example.example}")
    public void cronJob() throws Exception{
        /* 每5秒打印一次 */
        System.out.println("=====> 执行定时任务 <=====");
    }

}
