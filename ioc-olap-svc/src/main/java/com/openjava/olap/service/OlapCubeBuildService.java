package com.openjava.olap.service;

import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.vo.DataLakeQueryJobStatusVo;
import com.openjava.olap.vo.DataLakeTriggerJobVo;
import com.openjava.olap.vo.OlapCubeBuildVo;

import java.util.List;

/**
 * <p>模型手动-定时构建公共调用</p>
 * <p>在ioc-olap-job-svc模块的OlapJob类下会用到，主要是定时构建时调用</p>
 * <p>在ioc-olap-job-svc模块的OlapQuerySyncResultJob类下会用到，主要是定时任务查询同步任务状态返回后的后续操作</p>
 * @author Annie
 * @create 2019-11-06 15:44
 * @see com.openjava.olap.vo.OlapCubeBuildVo 构建接口消息返回体
 * @see OlapTimingrefreshServiceImpl 保存构建开始时间、结束时间
 * @see OlapTableSyncServiceImpl available方法，用于调用批量创建同步任务
 */
public interface OlapCubeBuildService {

    /**
     * <p>主要用于查询符合状态的模型列表出来，然后封装成参数体</p>
     * <p>多用于批量触发同步任务或批量获取同步任务状态</p>
     * @param flags
     * @return
     */
    List<DataLakeJobQueryParam> queryCubeByFlags(Integer flags);
    /**
     * 批量触发同步任务
     * @param params
     * @return
     */
    List<DataLakeTriggerJobVo> batchTriggerJob(List<DataLakeJobQueryParam> params)throws Exception;

    /**
     * 批量查询同步任务状态
     * @param params
     * @return
     */
    List<DataLakeQueryJobStatusVo> batchQueryJobStatus(List<DataLakeJobQueryParam> params)throws Exception;

    /**
     * 批量获取同步任务状态返回的时候更新模型状态
     * @param result
     */
    List<String> updateCubeStatus(List<DataLakeQueryJobStatusVo> result)throws Exception;

    /**
     * <p>手动、自动构建都要调用的接口</p>
     * <p>调用是会根据cube的状态判断是否可以构建</p>
     * @param cubeName
     * @param olapTimingrefresh 定时构建任务
     * @return OlapCubeBuildVo 手动点击构建时返回的消息体，定时构建不返回
     */
    OlapCubeBuildVo doBuildCube(String cubeName, OlapTimingrefresh olapTimingrefresh)throws Exception;


    /**
     * <p>断言操作</p>
     * <p>判断当前模型的状态值是否等于期待的状态值</p>
     * @param expectStatus 期待的状态值
     * @param actualStatus 实际上的状态值
     * @return true:相等,满足;false:不相等，不满足.
     */
    boolean assertCubeStatus(final int expectStatus,final int actualStatus);

    /**
     * <p>手动构建时，先判断状态是否满足</p>
     * <p>保存用户选择构建的开始、结束时间或定时构建时计算好的开始、结束时间到OLAP_TIMINGREFRESH</p>
     * <p>调用触发同步任务方法</p>
     * @param cubeName 模型名称
     * @return
     * @throws Exception
     */
    OlapCubeBuildVo preBuild(String cubeName,Long begin, Long end)throws Exception;

}
