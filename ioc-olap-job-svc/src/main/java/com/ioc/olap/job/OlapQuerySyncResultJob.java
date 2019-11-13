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

    @Scheduled(cron = "${schedule.cubeStatus.querySyncJob:'0 1 * * * ?'}")
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
                record.setManual(null);//开始build的时候，就设置这个字段为null，表示该模型不处于手动构建或定时构建的过程环节
                olapCubeBuildService.doBuildCube(s,begin,end,record);
            }
            } catch (Exception e) {
                log.error("构建模型失败",e);
            }
        });
    }

    private void calculateByCurrentMoment(long begin,long end,OlapTimingrefresh record,List<CubeHbaseMapper> hbases){
        //因为数据同步中的状态，所以手动构建和定时构建同一时刻只会有一个存在
        Calendar calendar = Calendar.getInstance();
        if (record.getManual() == 1){//手动点击构建
            if (hbases.size()>0){//非第一次构建
                hbases.sort(Comparator.comparing(CubeHbaseMapper::getDateRangeEnd).reversed());
                if (hbases.get(0).getDateRangeStart() == 0) {//全量构建
                    //默认begin.end=0了
                }else {//增量
                    //手动构建且是增量才会在接口设置begin和end
                    // 前端默认的begin时间会是上一次构建的结束时间，即模型列表会返回上一次构建的结束时间
                    begin = record.getBegin();
                    end = record.getEnd();
                    record.setBegin(null);
                    record.setEnd(null);
                }
            }else {//第一次构建
                //如果是第一次构建，且是手动的，则begin和end自由选择
                if (record.getBuildMode() == OlapTimingrefresh.BUILD_DELTA){//增量才会传递参数，全量就是默认0
                    begin = record.getBegin();
                    end = record.getEnd();
                    record.setBegin(null);
                    record.setEnd(null);
                }
            }
            record.setFinalExecutionTime(new Date());

        }else if (record.getManual() == 0){//否则就是定时构建
            if (hbases.size()>0){//非第一次构建
                hbases.sort(Comparator.comparing(CubeHbaseMapper::getDateRangeEnd).reversed());
                if (hbases.get(0).getDateRangeStart() == 0) {//全量，开始时间为0
                    //默认begin.end=0了
                } else {//增量
                    Long lastBuildTime = hbases.get(0).getDateRangeEnd();
                    Date lastBuildDate = new Date(lastBuildTime);
                    begin = lastBuildTime;//开始时间等于最后一次构建时间
                    calendar.setTime(lastBuildDate);
                    end = getBuildEndTime(calendar,record.getFrequencytype(),record.getInterval());//结束时间就是麒麟里最后一次构建时间+定时构建周期
                }
            }else {//第一次构建
                //开始时间设置一个很早的时间
                //结束时间就是创建时间+定时构建周期
                if (record.getBuildMode() == OlapTimingrefresh.BUILD_DELTA){
                    //定时+增量构建，则要计算起止时间
                    begin = record.getCreateTime().getTime();
                    calendar.setTime(record.getCreateTime());
                    end = getBuildEndTime(calendar,record.getFrequencytype(),record.getInterval());
                }
            }
            //最后一次执行构建的时间，应该统一以调用麒麟构建模型api接口的那一时刻为准
            //因为有先触发同步任务、查询同步任务状态的过程，所以最后构建时间并不等于模型构建接口的结束时间
            record.setFinalExecutionTime(new Date());
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
