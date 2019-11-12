package com.ioc.olap.job;

import com.openjava.olap.common.CubeFlags;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.common.kylin.JobHttpClient;
import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.mapper.kylin.CubeMapper;
import com.openjava.olap.mapper.kylin.JobsMapper;
import com.openjava.olap.service.OlapCubeBuildService;
import com.openjava.olap.service.OlapCubeService;
import com.openjava.olap.service.OlapTimingrefreshService;
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

    @Resource
    private OlapCubeBuildService olapCubeBuildService;

    @Autowired
    CubeHttpClient cubeHttpClient;

    @Autowired
    private JobHttpClient jobHttpClient;

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
//        cubeListTasks();
        queryBuildingModelAndUpdateByJob();
        logger.info("结束执行定时任务五分钟");
    }


    private void configureTasks(int frequencyType) throws Exception {
        List<OlapTimingrefresh> timingreFresh = olapTimingrefreshService.findTiming(frequencyType);
        if (timingreFresh != null) {
            Calendar calendar = Calendar.getInstance();
            for (OlapTimingrefresh fc : timingreFresh) {
                if (fc.getFinalExecutionTime() != null) {//如果最后一次执行时间不为空，则用最后一次时间+构建周期<=当前时间来判断该模型是否到了定时构建的时刻
                    calendar.setTime(fc.getFinalExecutionTime());
                    if (isNeedExecute(calendar, fc.getFrequencytype(), fc.getInterval())) {
                        OlapCube cube = this.olapCubeService.findTableInfo(fc.getCubeName());
                        if (cube != null){
                            //该方法会先判断模型状态是否满足构建
                            this.olapCubeBuildService.preBuild(cube.getName(),0L,0L,0);
                        }
                    }
                } else {//如果最后一次执行时间都为空，说明从未被手动或定时构建过，判断是否到了定时构建的时刻则以创建时间为起点
                    calendar.setTime(fc.getCreateTime());
                    if (isNeedExecute(calendar, fc.getFrequencytype(), fc.getInterval())) {
                        OlapCube cube = this.olapCubeService.findTableInfo(fc.getCubeName());
                        if (cube != null){
                            //该方法会先判断模型状态是否满足构建
                            this.olapCubeBuildService.preBuild(cube.getName(),0L,0L,0);
                        }
                    }
                }
            }
        }
    }

    private boolean isNeedExecute(Calendar calendar, Integer frequencyType, Integer interval) {
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
        if (calendar.getTimeInMillis() <= nowTime) {
            return true;
        }
        return false;
    }

    /**
     * <p>定时执行该方法</p>
     * <p>查询“构建中”的模型集合出来遍历</p>
     * <p>遍历模型，查询对应的job</p>
     * <p>如果麒麟里模型状态不是启用，则查询job的状态，判断是否是“ERROR”，如果是ERROR，则更新数据库模型的状态为构建失败，否则忽略</p>
     * @throws Exception
     */
    private void queryBuildingModelAndUpdateByJob()throws Exception{
        List<OlapCube> result = this.olapCubeService.queryListByFlags(CubeFlags.BUILDING.getFlags());
        for (OlapCube s : result) {
            Integer status = null;
            //用list查询，cubeName作为参数传递，其实麒麟是模糊查询且忽略大小写，恶心。。
            //如果有100万条模型的名称是包含A，那么传A进去就得到了100万条记录，这里得循环很多次才能找到对应的job
            JobsMapper[] list = jobHttpClient.list(65555L, 0L, null, s.getName());
            if (list.length == 0){
                continue;
            }
            List<JobsMapper> tmp = Arrays.asList(list);
            //找出模型对应的job，按照执行job结束时间来倒序，取第一个
            Optional<JobsMapper> job = tmp.stream().filter(a->s.getName().equals(a.getRelated_cube()))
                .sorted(Comparator.comparing(JobsMapper::getExec_end_time)).findFirst();
            if (job.isPresent()){
                String jobStatus = job.get().getJob_status();
                switch (jobStatus){
                    case "FINISHED"://job执行完成，则模型为启用状态
                        status = CubeFlags.ENABLED.getFlags();
                        break;
                    case "ERROR"://job错误时，则模型为构建失败状态
                        status = CubeFlags.BUILD_FAILED.getFlags();
                    default:break;
                }
            }
            if (status != null){
                s.setFlags(status);
                s.setUpdateTime(new Date());
                this.olapCubeService.doSave(s);
            }
        }
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
