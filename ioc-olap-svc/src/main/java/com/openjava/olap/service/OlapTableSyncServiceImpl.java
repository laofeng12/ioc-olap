package com.openjava.olap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.common.RestToken;
import com.openjava.olap.common.kylin.HiveHttpClient;
import com.openjava.olap.common.kylin.ProjectHttpClient;
import com.openjava.olap.domain.OlapTableSync;
import com.openjava.olap.mapper.kylin.OverrideKylinPropertiesMapper;
import com.openjava.olap.mapper.kylin.ProjectDescDataMapper;
import com.openjava.olap.mapper.kylin.TableStructureMapper;
import com.openjava.olap.query.OlapTableSyncParam;
import com.openjava.olap.repository.OlapTableSyncRepository;
import com.openjava.olap.vo.OlapTableSyncVo;
import lombok.extern.slf4j.Slf4j;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author linchuangang
 * @create 2019-11-05 17:38
 **/
@Service
@Slf4j
public class OlapTableSyncServiceImpl implements OlapTableSyncService,InitializingBean {

    @Value("${olap.sync.realTableNamePrefix:OLAP_}")
    private String REAL_TABLE_NAME_PREFIX;

    @Value("${olap.kylin.databaseName:olap}")
    private String DATABASE_NAME;

    private final OlapTableSyncRepository repository;
    private final DataLakeConfig dataLakeConfig;
    private final RestToken restToken;
    private final HiveHttpClient hiveHttpClient;
    private final ProjectHttpClient projectHttpClient;

    public OlapTableSyncServiceImpl(
        OlapTableSyncRepository repository,
        DataLakeConfig dataLakeConfig,
        RestToken restToken,
        HiveHttpClient hiveHttpClient,
        ProjectHttpClient projectHttpClient) {
        this.repository = repository;
        this.dataLakeConfig = dataLakeConfig;
        this.restToken = restToken;
        this.hiveHttpClient = hiveHttpClient;
        this.projectHttpClient = projectHttpClient;
    }

    /**
     * <p>这里加事务不因异常而回滚的原因在于，不管save方法的前面或后面发生什么异常，都要保存同步记录，这样才能防止一张表多次请求同步</p>
     * @param sync
     * @return
     */
    @Override
    @Transactional(noRollbackFor = Exception.class)
    public OlapTableSync save(OlapTableSync sync) {
        return this.repository.save(sync);
    }

    @Override
    public OlapTableSync get(String databaseId,String resourceId,String createBy) {
        return this.repository.getByDatabaseIdAndResourceIdAndCreateBy(databaseId,resourceId,createBy);
    }

    @Override
    @Security(session = true)
    public Map<String,Object> available(List<OlapTableSyncParam> params)throws Exception {
        //表名的生成：固定前缀+ConcurrentSequence.getInstance().getSequence()。判断是否同步过，查询databaseId,resourceId,userId即可
        Assert.notNull(this.dataLakeConfig.getBatchCreateSyncJobUrl(),"调用批量创建同步任务接口地址不能为空");
        Map<String,Object> map = new HashMap<>();
        int success = 1;
        String msg = "同步成功";
        UserVO user = (UserVO) SsoContext.getUser();
        String token = SsoContext.getToken();
        List<OlapTableSyncVo> results = new ArrayList<>();
        StringBuilder sb = new StringBuilder(1024);
        try {
            Iterator<OlapTableSyncParam> iterator = params.iterator();
            log.info("前端传递的表：{}", JSON.toJSONString(params));
            while (iterator.hasNext()){//相当于复制一份给results
                OlapTableSyncParam param = iterator.next();
                OlapTableSync sync= this.get(param.getDatabaseId(),param.getResourceId(),user.getUserId());
                //只要是请求成功“批量同步任务接口”并返回成功，则标记为1
                if (sync != null){
                    if (sync.getSuccess() == null || sync.getSuccess() ==0){//失败的就拿去请求
                        param.setWriterTableSource(sync.getTableName());
                        param.setBusinessId(sync.getBusinessId());
                        OlapTableSyncVo vo = new OlapTableSyncVo();
                        vo.setBusinessId(sync.getBusinessId());
                        vo.setDatabaseId(param.getDatabaseId());
                        vo.setResourceId(param.getResourceId());
                        vo.setVirtualTableName(sync.getVirtualTableName());
                        vo.setWriterTableName(sync.getTableName());
                        vo.setSyncId(sync.getSyncId());
                        vo.setIsNew(false);
                        results.add(vo);
                    }else {
                        OlapTableSyncVo vo = new OlapTableSyncVo();
                        vo.setDatabaseId(param.getDatabaseId());
                        vo.setResourceId(param.getResourceId());
                        vo.setSuccess(true);
                        vo.setBusinessId(sync.getBusinessId());
                        vo.setVirtualTableName(sync.getVirtualTableName());
                        vo.setWriterTableName(sync.getTableName());
                        results.add(vo);
                        iterator.remove();//成功的就不拿去请求了
                    }
                }else {//从未请求过的也拿去请求
                    //这里设置目标表名的生成规则;虚拟表名+自定义id
                    // 如果是该服务作为分布式部署，则这里的id生成需要分布式支持
                    Long id = ConcurrentSequence.getInstance().getSequence();
                    String tableName = param.getVirtualTableName()+"_"+id;
                    param.setWriterTableSource(tableName);
                    param.setBusinessId(id.toString());
                    OlapTableSyncVo vo = new OlapTableSyncVo();
                    vo.setBusinessId(id.toString());
                    vo.setDatabaseId(param.getDatabaseId());
                    vo.setResourceId(param.getResourceId());
                    vo.setVirtualTableName(param.getVirtualTableName());
                    vo.setWriterTableName(param.getWriterTableSource());
                    vo.setIsNew(true);
                    results.add(vo);
                }
            }
            log.info("请求创建同步任务的参数:{}",JSON.toJSONString(params));
            String result = "";
            if (!params.isEmpty()){
                result = restToken.postJson(
                    this.dataLakeConfig.getHost()+this.dataLakeConfig.getBatchCreateSyncJobUrl(),
                    params,token);
            }
            log.info("请求创建同步任务结果返回:{}",result);
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray array;
            if (jsonObject!= null && jsonObject.containsKey("data") && (array=jsonObject.getJSONArray("data"))!= null ){
                array.forEach(s->{
                    JSONObject item = (JSONObject) s;
                    OlapTableSync oo = new OlapTableSync();
                    //循环那些拿去请求的参数
                    results.stream().filter(a->a.getDatabaseId().equals(item.getString("databaseId"))
                        && a.getResourceId().equals(item.getString("resourceId")))
                        .forEach(b->{
                            oo.setTableName(b.getWriterTableName());//设置真实表名
                            oo.setDatabaseId(b.getDatabaseId());
                            oo.setResourceId(b.getResourceId());
                            oo.setBusinessId(b.getBusinessId());
                            oo.setVirtualTableName(b.getVirtualTableName());
                            b.setSuccess(item.getBoolean("success"));
                            b.setMessage(item.getString("message"));
                            //第一步，先判断success是否为false
                            //如果是false,再判断repeatCreate是否为true，为true表示该资源已经创建过同步任务了
                            //在success=false，repeatCreate=false的情况下，就可以认为资源创建同步任务失败了
                            //这里采集错误信息，统一返回给前端
                            if (item.getBoolean("success") == null || !item.getBoolean("success")){
                                if (item.getBoolean("repeatCreate") != null && item.getBoolean("repeatCreate")){
                                    b.setSuccess(true);
                                }else {
                                    sb.append("[表:")
                                        .append(b.getVirtualTableName())
                                        .append("加载失败,")
                                        .append(item.getString("message"))
                                        .append("]\n");
                                }
                            }
                            oo.setIsNew(b.getIsNew());
                            oo.setSuccess(b.getSuccess()?1:0);
                            oo.setCreateBy(user.getUserId());
                            //不为空的时候，就是更新，为空则新增
                            if (b.getIsNew()) {
                                oo.setSyncId(ConcurrentSequence.getInstance().getSequence());
                            }else {
                                oo.setSyncId(b.getSyncId());
                            }
                            log.debug("保存记录:{}",JSON.toJSONString(oo));
                            this.save(oo);//保存记录
                        });
                });
            }
            int failed = (int) results.stream().filter(s-> s.getSuccess() == null || !s.getSuccess()).count();
            if (failed>0){
                success = 0;
            }
            queryHiveTableMeta(results);
        }catch (Exception var1){
            log.error("加载麒麟表结构失败",var1);
            success = 0;
            msg = "加载表结构失败";
        }
        map.put("success",success);
        map.put("msg",success == 1?msg:msg+"\n"+sb.toString());
        map.put("result",results);
        return map;
    }

    /**
     * 查询hive表结构，字段信息
     * @param list
     * @throws Exception
     */
    @Security(session = true)
    private void queryHiveTableMeta(List<OlapTableSyncVo> list)throws Exception{
        UserVO userVO = ((UserVO)SsoContext.getUser());
        ProjectDescDataMapper project = projectHttpClient.get(userVO.getUserId());
        if (project == null) {
            ProjectDescDataMapper projectDesc = new ProjectDescDataMapper();
            projectDesc.setName(userVO.getUserId());
            projectDesc.setDescription(userVO.getUserName());
            OverrideKylinPropertiesMapper override = new OverrideKylinPropertiesMapper();
            override.setAuthor(userVO.getUserName());
            projectDesc.setOverride_kylin_properties(override);
            projectHttpClient.create(projectDesc);
        }
        if (list != null && !list.isEmpty()){
            for (OlapTableSyncVo s : list) {
                this.hiveHttpClient.preloadTable(userVO.getUserId(),DATABASE_NAME,s.getWriterTableName());
                TableStructureMapper meta = this.hiveHttpClient.getTableMeta(userVO.getUserId(), DATABASE_NAME, s.getWriterTableName());
                s.setMeta(meta);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
