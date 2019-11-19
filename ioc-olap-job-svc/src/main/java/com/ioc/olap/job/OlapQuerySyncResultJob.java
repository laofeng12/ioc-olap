package com.ioc.olap.job;

import com.openjava.olap.common.CubeFlags;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.service.OlapCubeBuildService;
import com.openjava.olap.service.OlapTimingrefreshService;
import com.openjava.olap.vo.DataLakeQueryJobStatusVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>定时查询批量获取同步任务状态接口</p>
 * <p>周期：1分钟</p>
 * @author linchuangang
 * @create 2019-11-06 16:17
 **/
@Component
public class OlapQuerySyncResultJob {

    private final OlapCubeBuildService olapCubeBuildService;
    private final OlapTimingrefreshService olapTimingrefreshService;
    private final CubeHttpClient cubeHttpClient;
    private Logger log = LoggerFactory.getLogger(OlapQuerySyncResultJob.class);

    @Autowired
    public OlapQuerySyncResultJob(OlapCubeBuildService olapCubeBuildService, OlapTimingrefreshService olapTimingrefreshService, CubeHttpClient cubeHttpClient) {
        this.olapCubeBuildService = olapCubeBuildService;
        this.olapTimingrefreshService = olapTimingrefreshService;
        this.cubeHttpClient = cubeHttpClient;
    }

    @Scheduled(cron = "${schedule.cubeStatus.querySyncJob:'0 0/1 * * * ?'}")
    public void querySyncJobStatus()throws Exception{
        log.info("定时查询状态开始");
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
                //构建时，只管取record里的begin和end即可，这两个时间在手动和定时入口先算好，再存到record里
                olapCubeBuildService.doBuildCube(s,record);
            } catch (Exception e) {
                log.error("构建模型失败",e);
            }
        });
        log.info("定时查询状态结束");
    }
}
