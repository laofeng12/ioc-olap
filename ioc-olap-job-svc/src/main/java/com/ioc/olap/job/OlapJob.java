package com.ioc.olap.job;

import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.mapper.kylin.CubeDescDataMapper;
import com.openjava.olap.mapper.kylin.CubeHbaseMapper;
import com.openjava.olap.mapper.kylin.CubeMapper;
import com.openjava.olap.service.OlapCubeService;
import com.openjava.olap.service.OlapTimingrefreshService;
import org.apache.ibatis.annotations.Case;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 定时任务例子
 */
@Component
public class OlapJob {

    @Resource
    private OlapCubeService olapCubeService;

    @Resource
    private OlapTimingrefreshService olapTimingrefreshService;

    @Autowired
    CubeHttpClient cubeHttpClient;

    private Logger logger = LoggerFactory.getLogger(OlapJob.class);

    @Scheduled(cron = "${schedule.hour.hour}")
    public void cronJob() throws Exception {
        logger.info("开始执行定时任务-小时");
        configureTasks(1);
        logger.info("结束执行定时任务-小时");
    }

    @Scheduled(cron = "${schedule.day.day}")
    public void day() throws Exception {
        logger.info("开始执行定时任务-天");
        configureTasks(2);
        logger.info("结束执行定时任务-天");
    }

    @Scheduled(cron = "${schedule.month.month}")
    public void month() throws Exception {
        logger.info("开始执行定时任务-月");
        configureTasks(3);
        logger.info("结束执行定时任务-月");
    }

    @Scheduled(cron = "${schedule.five_minute.five_minute}")
    public void minute() throws Exception {
        logger.info("开始执行定时任务五分钟");
        cubeListTasks();
        logger.info("结束执行定时任务五分钟");
    }


    private void configureTasks(int frequencyType) throws Exception {
        List<OlapTimingrefresh> timingreFresh = olapTimingrefreshService.findTiming(frequencyType);
        if (timingreFresh != null) {
            for (OlapTimingrefresh fc : timingreFresh) {
                try {
                    List<CubeHbaseMapper> hbases = cubeHttpClient.hbase(fc.getCubeName());
                    Calendar calendar = Calendar.getInstance();
                    if (hbases.size() > 0) {
                        hbases.sort(Comparator.comparing(CubeHbaseMapper::getDateRangeEnd).reversed());
                        //全量构建
                        if (hbases.get(0).getDateRangeStart() == 0) {
                            calendar.setTime(fc.getFinalExecutionTime());
                            if(isNeedExcute(calendar,fc.getFrequencytype(),fc.getInterval())){
                                cubeHttpClient.build(fc.getCubeName(), 0L, 0L);
                                fc.setFinalExecutionTime(calendar.getTime());
                                olapTimingrefreshService.doSave(fc);
                            }
                        } else {
                            Long lastBuildTime = hbases.get(0).getDateRangeEnd();
                            Date lastBuildDate = new Date(lastBuildTime);
                            calendar.setTime(lastBuildDate);
                            if(isNeedExcute(calendar,fc.getFrequencytype(),fc.getInterval())){
                                cubeHttpClient.build(fc.getCubeName(), lastBuildTime, calendar.getTimeInMillis());
                                fc.setFinalExecutionTime(calendar.getTime());
                                olapTimingrefreshService.doSave(fc);
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.info("定时构建" + fc.getCubeName() + "出现异常！", e);
                }
            }
        }
    }

    private boolean isNeedExcute(Calendar calendar, Integer frequencyType, Integer interval) {
        Long nowTime = new Date().getTime();
        switch (frequencyType) {
            case 1://小时
                calendar.add(Calendar.HOUR, interval);
                break;
            case 2://天数
                calendar.add(Calendar.DAY_OF_MONTH, interval);
                break;
            default://月
                calendar.add(Calendar.MONTH, interval);
                break;
        }
        if (calendar.getTimeInMillis() < nowTime) {
            return true;
        }
        return false;
    }

    private void cubeListTasks() throws Exception {
        Integer limit = 1000;
        Integer offset = 0;
        List<CubeMapper> result = new ArrayList<CubeMapper>();
        while (true) {
            List<CubeMapper> dateList = cubeHttpClient.list(limit, offset);
            if (dateList.size() == 0) {
                break;
            }
            result.addAll(dateList);
            offset = offset + limit;
        }
        if (result.size() != 0) {
            //遍历列表
            List<OlapCube> m = olapCubeService.findAll();
            if (m != null && m.size() > 0) {
                for (CubeMapper mapper : result) {
                    OlapCube cube = m.stream().filter(p -> p.getName().equalsIgnoreCase(mapper.getName())).findFirst().orElse(null);
                    if (cube != null) {
                        if (mapper.getStatus().equals("READY")) {
                            if (cube.getFlags() != 1) {
                                Date now = new Date();
                                cube.setFlags(1);
                                cube.setUpdateTime(now);
                                olapCubeService.doSave(cube);
                            }
                        } else {
                            if (cube.getFlags() != 0) {
                                Date now = new Date();
                                cube.setFlags(0);
                                cube.setUpdateTime(now);
                                olapCubeService.doSave(cube);
                            }
                        }
                    }
                }
            }
        }
    }
}
