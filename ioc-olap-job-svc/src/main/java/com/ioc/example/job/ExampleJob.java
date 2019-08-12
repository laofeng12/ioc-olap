package com.ioc.example.job;


import com.ioc.example.job.service.CubeService;
import com.openjava.platform.domain.OlapTimingrefresh;
import com.openjava.platform.service.OlapTimingrefreshService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时任务例子
 */
@Component
public class ExampleJob {

    @Resource
    private CubeService cubeService;//注入bean（例子，按需注入）

    @Resource
    private OlapTimingrefreshService olapTimingrefreshService;

    @Scheduled(cron = "${schedule.execution.waitqueue}")
    public void cronJob() throws Exception {
        System.out.println("=====> 执行定时任务-小时 <=====");
        configureTasks(1);
    }

    @Scheduled(cron = "${schedule.day.day}")
    public void day() throws Exception {
        System.out.println("=====> 执行定时任务-天 <=====");
        configureTasks(2);
    }

    @Scheduled(cron = "${schedule.month.month}")
    public void month() throws Exception {
        System.out.println("=====> 执行定时任务-月 <=====");
        configureTasks(3);
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
}
