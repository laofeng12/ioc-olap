package com.ioc.olap.job;


import com.ioc.olap.job.service.CubeService;
import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapTimingrefresh;
import com.openjava.platform.service.OlapCubeService;
import com.openjava.platform.service.OlapTimingrefreshService;
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
    private CubeService cubeService;//注入bean（例子，按需注入）

    @Resource
    private OlapCubeService olapCubeService;

    @Resource
    private OlapTimingrefreshService olapTimingrefreshService;

    @Scheduled(cron = "${schedule.execution.waitqueue}")
    public void cronJob() throws Exception {
        configureTasks(1);
    }

    @Scheduled(cron = "${schedule.day.day}")
    public void day() throws Exception {
        configureTasks(3);
    }

    @Scheduled(cron = "${schedule.month.month}")
    public void month() throws Exception {
        configureTasks(3);
    }

    @Scheduled(cron = "${schedule.five_minute.five_minute}")
    public void minute() throws Exception {
        cubeListTasks(100, 0);
    }


    private void configureTasks(int frequencyType) throws Exception {
        List<OlapTimingrefresh> timingreFresh = olapTimingrefreshService.findTiming(frequencyType);
        if (timingreFresh != null) {

            //执行bui
            for (OlapTimingrefresh fc : timingreFresh) {
                cubeService.build(fc.getCubeName(), fc.getFinalExecutionTime(), fc.getNextExecutionTime());

                Date nextDate = fc.getNextExecutionTime();
                int interval = fc.getInterval().intValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(nextDate);
                switch (frequencyType) {
                    case 1://小时
                        calendar.add(Calendar.HOUR, interval);
                        break;
                    case 2://天数
                        calendar.add(Calendar.DAY_OF_MONTH, +interval);
                        break;
                    default://月
                        calendar.add(Calendar.MONTH, +interval);
                        break;
                }
                Date dateCalendar = calendar.getTime();
                fc.setFinalExecutionTime(nextDate);  //最后执行时间
                fc.setNextExecutionTime(dateCalendar); //下一次执行执行时间
                fc.setIsNew(false);
                olapTimingrefreshService.doSave(fc);
            }
        }
    }

    private void cubeListTasks(Integer limit, Integer offset) throws Exception {
        ArrayList<HashMap> result = cubeService.list(limit,offset);

        if (result != null) {
            //遍历列表
            for (int i=0;i< result.size();i++) {
                String cubeName=result.get(i).get("name").toString();
                String status=result.get(i).get("status").toString();

                List<OlapCube> m=olapCubeService.findAll();
                if(m!=null) {
                    for (OlapCube fc : m){
                    //for (int j=0;i< m.size();j++) {
                        if(fc.getName().equals(cubeName)) {
                            if (status.equals("READY")) {
                                if (fc.getFlags() != 1) {
                                    Date now = new Date();
                                    fc.setFlags(1);
                                    fc.setUpdateTime(now);
                                    olapCubeService.doSave(fc);
                                }
                            }
                            else {
                                if (fc.getFlags() != 0) {
                                    Date now = new Date();
                                    fc.setFlags(0);
                                    fc.setUpdateTime(now);
                                    olapCubeService.doSave(fc);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
