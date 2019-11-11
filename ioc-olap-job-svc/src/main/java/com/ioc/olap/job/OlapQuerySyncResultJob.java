package com.ioc.olap.job;

import com.openjava.olap.common.CubeFlags;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.mapper.kylin.CubeHbaseMapper;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.service.OlapCubeBuildService;
import com.openjava.olap.service.OlapTimingrefreshService;
import com.openjava.olap.vo.DataLakeQueryJobStatusVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * <p>定时查询批量获取同步任务状态接口</p>
 * <p>周期：1分钟</p>
 * @author linchuangang
 * @create 2019-11-06 16:17
 **/
@Component
@Slf4j
@AllArgsConstructor
public class OlapQuerySyncResultJob {

    private final OlapCubeBuildService olapCubeBuildService;
    private final OlapTimingrefreshService olapTimingrefreshService;
    private final CubeHttpClient cubeHttpClient;

    @Scheduled(cron = "${schedule.cubeStatus.querySyncJob:'0 1 * * * * ?'}")
    public void querySyncJobStatus()throws Exception{
        //查询出正在处于“数据同步中”的模型
        List<DataLakeJobQueryParam> params = olapCubeBuildService.queryCubeByFlags(CubeFlags.ON_SYNC.getFlags());
        //封装成查询同步任务状态接口需要的参数消息体
        List<DataLakeQueryJobStatusVo> list = olapCubeBuildService.batchQueryJobStatus(params);
        //把查询同步任务状态结果更新到数据库
        List<String> successJobOfCubeNameList = olapCubeBuildService.updateCubeStatus(list);
        //循环构建已经同步任务成功的模型
        successJobOfCubeNameList.forEach(s->{
            OlapTimingrefresh record = olapTimingrefreshService.findTableInfo(s);
            try {
            if (record != null && record.getManual() != null){
                List<CubeHbaseMapper> hbases = cubeHttpClient.hbase(s);
                long begin = 0L;
                long end = 0L;
                calculateByCurrentMoment(begin,end,record,hbases);
                olapCubeBuildService.doBuildCube(s,begin,end,record);
            }
            } catch (Exception e) {
                log.error("构建模型失败",e);
            }
        });
    }

    private void calculateByCurrentMoment(long begin,long end,OlapTimingrefresh record,List<CubeHbaseMapper> hbases){
        //因为手动构建和定时构建，同一时刻只会有一个存在
        Calendar calendar = Calendar.getInstance();
        if (record.getManual() == 1){//手动点击构建
            if (hbases.size()>0){
                hbases.sort(Comparator.comparing(CubeHbaseMapper::getDateRangeEnd).reversed());
                if (hbases.get(0).getDateRangeStart() == 0) {//全量构建
                    record.setFinalExecutionTime(new Date(record.getEnd()));
                }else {//增量
                    begin = record.getBegin();//增量才需要begin和end
                    end = record.getEnd();
                    record.setBegin(null);
                    record.setEnd(null);
                    record.setFinalExecutionTime(new Date(record.getEnd()));
                }
            }
        }else if (record.getManual() == 0){//否则就是定时构建
            if (hbases.size()>0){
                hbases.sort(Comparator.comparing(CubeHbaseMapper::getDateRangeEnd).reversed());
                if (hbases.get(0).getDateRangeStart() == 0) {//全量
                    if (record.getFinalExecutionTime() != null) {
                        calendar.setTime(record.getFinalExecutionTime());
                        getBuildEndTime(calendar,record.getFrequencytype(),record.getInterval());
                        record.setFinalExecutionTime(calendar.getTime());
                    } else {
                        record.setFinalExecutionTime(new Date());
                    }
                } else {//增量
                    Long lastBuildTime = hbases.get(0).getDateRangeEnd();
                    Date lastBuildDate = new Date(lastBuildTime);
                    begin = lastBuildTime;
                    end = calendar.getTimeInMillis();
                    calendar.setTime(lastBuildDate);
                    getBuildEndTime(calendar,record.getFrequencytype(),record.getInterval());
                    record.setFinalExecutionTime(calendar.getTime());
                }
            }
        }
    }

    private Long getBuildEndTime(Calendar calendar, Integer frequencyType, Integer interval){
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
        return calendar.getTimeInMillis();
    }


}
