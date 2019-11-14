package com.openjava.olap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openjava.olap.common.CubeFlags;
import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.common.RestToken;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.repository.OlapDatalaketableRepository;
import com.openjava.olap.vo.DataLakeQueryJobStatusVo;
import com.openjava.olap.vo.DataLakeTriggerJobVo;
import com.openjava.olap.vo.OlapCubeBuildVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * OlapCubeBuildService的实现类
 * @author linchuangang
 * @create 2019-11-07 14:28
 * @see com.openjava.olap.service.OlapCubeBuildService
 **/
@Service
@Slf4j
@AllArgsConstructor
public class OlapCubeBuildServiceImpl implements OlapCubeBuildService,InitializingBean {

    /**同步来源，固定值，3代表是OLAP组**/
    public static final String syncSource = "3";
    /**数据集系统相关接口地址**/
    private final DataLakeConfig dataLakeConfig;
    /**调用数据湖子模块系统的http简单封装**/
    private final RestToken restToken;
    /**构建cube时调用**/
    private final CubeHttpClient cubeAction;
    /**用于更新cube状态**/
    private final OlapCubeService olapCubeService;
    /**保存构建时间**/
    private final OlapTimingrefreshService olapTimingrefreshService;
    private final OlapDatalaketableRepository olapDatalaketableRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.olapCubeService.queryByFlags(CubeFlags.DISABLED.getFlags());
    }

    @Override
    public List<DataLakeJobQueryParam> queryCubeByFlags(Integer flags) {
        Map<String,List<OlapDatalaketable>> result = this.olapCubeService.queryByFlags(flags);
        List<DataLakeJobQueryParam> list = new ArrayList<>();
        //一次性封装好所有待查询同步任务状态的表的参数消息体
        result.forEach((key,value)-> value.forEach(s->{
            list.add(DataLakeJobQueryParam.builder()
                .resourceId(s.getResourceId())
                .syncSource(syncSource)
                .type(s.getType())
                .databaseId(s.getDatabaseId())
                .cubeName(s.getCubeName())
                .build());
        }));
        return list;
    }

    /**
     * <p>自身类的preBuild方法调用</p>
     * <p>用于手动点击构建和定时构建循环模型时调用</p>
     * @param cubeName
     * @return
     */
    private List<DataLakeJobQueryParam> queryJobQueryParamsByCubeName(String cubeName){
        List<DataLakeJobQueryParam> list = new ArrayList<>();
        List<OlapDatalaketable> result = this.olapDatalaketableRepository.queryListInCubeNameList(new ArrayList<String>(){
            {
                add(cubeName);
            }
        });
        result.forEach(s-> list.add(DataLakeJobQueryParam.builder()
            .resourceId(s.getResourceId())
            .syncSource(syncSource)
            .type(s.getType())
            .databaseId(s.getDatabaseId())
            .cubeName(s.getCubeName())
            .build()));
        return list;
    }

    @Override
    public List<DataLakeTriggerJobVo> batchTriggerJob(List<DataLakeJobQueryParam> params) throws Exception {
        List<DataLakeTriggerJobVo> list = new ArrayList<>();
        if (params == null || params.size() == 0){
            return list;
        }
        log.info("请求批量触发同步任务接口参数:{}", JSON.toJSONString(params));
        String result = this.restToken.postJson(this.dataLakeConfig.getHost()+this.dataLakeConfig.getBatchActiveSyncJobUrl(),
            params);

        log.info("请求批量触发同步任务接口返回:{}",result);
        JSONArray array;
        JSONObject object = JSONObject.parseObject(result);
        if (object != null && object.containsKey("rows") && (array = object.getJSONArray("rows")) != null){
            array.forEach(s->{
                JSONObject item = (JSONObject) s;
                DataLakeTriggerJobVo vo = new DataLakeTriggerJobVo();
                BeanUtils.copyProperties(item,vo);
                list.add(vo);
            });
        }
        return list;
    }

    @Override
    public List<DataLakeQueryJobStatusVo> batchQueryJobStatus(List<DataLakeJobQueryParam> params) throws Exception {
        List<DataLakeQueryJobStatusVo> list = new ArrayList<>();
        if (params == null || params.size() == 0){
            return list;
        }
        log.info("批量查询同步任务状态参数:{}",JSON.toJSONString(params));
        String result = this.restToken.postJson(this.dataLakeConfig.getHost()+this.dataLakeConfig.getQuerySyncJobStateUrl(),params);
        log.info("批量查询同步任务状态结果:{}",result);
        JSONArray array;
        JSONObject object = JSONObject.parseObject(result);
        if (object != null && object.containsKey("data") && (array = object.getJSONArray("data")) != null){
            array.forEach(s->{

                JSONObject item = (JSONObject) s;
                Optional<DataLakeJobQueryParam> optional = params.stream()
                    .filter(a->a.getResourceId().equals(item.getString("resourceId"))).findFirst();
                DataLakeQueryJobStatusVo vo = new DataLakeQueryJobStatusVo();
                vo.setStatus(item.getInteger("status"));
                vo.setResourceId(item.getString("resourceId"));
                vo.setBusinessId(item.getString("businessId"));
                optional.ifPresent(dataLakeJobQueryParam -> {
                    vo.setCubeName(dataLakeJobQueryParam.getCubeName());
                    list.add(vo);
                });
            });
        }
        return list;
    }

    @Override
    public List<String> updateCubeStatus(List<DataLakeQueryJobStatusVo> result) throws Exception {
        List<String> cubeNameList = new ArrayList<>();
        if (result != null && result.size() > 0){
            Map<String,List<DataLakeQueryJobStatusVo>> map = result.stream()
                .collect(Collectors.groupingBy(DataLakeQueryJobStatusVo::getCubeName));
            map.forEach((key,value)->{
                int status = -1;
                //统计成功同步的总数
                long suc = value.stream().filter(s->s.getStatus() != null && s.getStatus() == 4).count();
                //统计同步失败的总数
                long failed = value.stream().filter(s->s.getStatus() != null && s.getStatus() == 5).count();
                if (value.size() == suc){
                    //全部表同步成功，cube才能设为构建中
                    status = CubeFlags.BUILDING.getFlags();
                    cubeNameList.add(key);
                }
                if (failed>0){
                    //只要有一个失败，这个cube就相当于同步失败
                    status = CubeFlags.SYNC_FAILED.getFlags();
                }
                OlapCube cube = this.olapCubeService.findTableInfo(key);
                if (cube != null && status != -1){
                    cube.setFlags(status);
                    cube.setUpdateTime(new Date());
                    log.info("更新立方体:{}",JSON.toJSONString(cube));
                    this.olapCubeService.doSave(cube);
                }
            });
        }
        return cubeNameList;
    }

    @Override
    public OlapCubeBuildVo doBuildCube(String cubeName, Long begin,Long end,OlapTimingrefresh olapTimingrefresh) throws Exception {
        OlapCubeBuildVo vo = new OlapCubeBuildVo();
        try {
            log.info("构建模型时的参数:{},{},{},{}",cubeName,begin,end,JSON.toJSONString(olapTimingrefresh));
            this.olapTimingrefreshService.doSave(olapTimingrefresh);
            cubeAction.build(cubeName,begin,end);
            vo.setMsg("构建操作请求成功");
            vo.setStatus(1);
        }catch (Exception var1){
            vo.setMsg("构建操作请求失败");
            vo.setStatus(0);
            throw var1;
        }
        return vo;
    }

    @Override
    public boolean assertCubeStatus(int expectStatus, int actualStatus) {
        return expectStatus == actualStatus;
    }

    @Override
    public OlapCubeBuildVo preBuild(String cubeName,Long begin, Long end,Integer manual) throws Exception {
        OlapCube cube = this.olapCubeService.findTableInfo(cubeName);
        OlapCubeBuildVo vo = new OlapCubeBuildVo();
        vo.setStatus(1);
        vo.setMsg("请求成功");
        if (cube == null){
            vo.setMsg("查询不到该模型，请检查参数有效性");
            vo.setStatus(0);
            return vo;
        }
        if (
            assertCubeStatus(CubeFlags.DISABLED.getFlags(),cube.getFlags())
                || assertCubeStatus(CubeFlags.ON_SYNC.getFlags(),cube.getFlags())
            ){
            vo.setMsg("请求被拒绝，该模型的状态为：["+CubeFlags.getByFlags(cube.getFlags())
                +"],状态为\"禁用\"或\"数据同步中\"的模型不能进行构建操作");
            vo.setStatus(0);
            return vo;
        }
        List<DataLakeJobQueryParam> params = queryJobQueryParamsByCubeName(cubeName);
        //批量调用触发同步任务接口，开始同步数据
        this.batchTriggerJob(params);
        OlapTimingrefresh olapTimingrefresh = this.olapTimingrefreshService.findTableInfo(cubeName);
        if (olapTimingrefresh!=null){
            olapTimingrefresh.setBegin(begin);
            olapTimingrefresh.setEnd(end);
            olapTimingrefresh.setManual(manual);
            this.olapTimingrefreshService.doSave(olapTimingrefresh);
        }
        cube.setFlags(CubeFlags.ON_SYNC.getFlags());
        cube.setUpdateTime(new Date());
        this.olapCubeService.doSave(cube);
        return vo;
    }
}
