package com.openjava.olap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.common.RestToken;
import com.openjava.olap.domain.OlapTableSync;
import com.openjava.olap.query.OlapTableSyncParam;
import com.openjava.olap.repository.OlapTableSyncRepository;
import com.openjava.olap.vo.OlapTableSyncVo;
import lombok.extern.slf4j.Slf4j;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author linchuangang
 * @create 2019-11-05 17:38
 **/
@Service
@Slf4j
public class OlapTableSyncServiceImpl implements OlapTableSyncService,InitializingBean {

    @Value("${olap.sync.realTableNamePrefix:OLAP_}")
    private String REAL_TABLE_NAME_PREFIX;

    private final OlapTableSyncRepository repository;
    private final DataLakeConfig dataLakeConfig;
    private final RestToken restToken;

    public OlapTableSyncServiceImpl(
        OlapTableSyncRepository repository,
        DataLakeConfig dataLakeConfig, RestToken restToken) {
        this.repository = repository;
        this.dataLakeConfig = dataLakeConfig;
        this.restToken = restToken;
    }

    @Transactional
    @Override
    public OlapTableSync save(OlapTableSync sync) {
        return this.repository.save(sync);
    }

    @Override
    public OlapTableSync get(String databaseId,String resourceId) {
        return this.repository.getByDatabaseIdAndResourceId(databaseId,resourceId);
    }

    @Override
    public List<OlapTableSyncVo> available(List<OlapTableSyncParam> params)throws Exception {
        Assert.notNull(this.dataLakeConfig.getBatchCreateSyncJobUrl(),"调用批量创建同步任务接口地址不能为空");
        List<OlapTableSyncVo> results = new ArrayList<>();
        Iterator<OlapTableSyncParam> iterator = params.iterator();
        while (iterator.hasNext()){//相当于复制一份给results
            OlapTableSyncParam param = iterator.next();
            OlapTableSync sync= this.get(param.getDatabaseId(),param.getResourceId());
            //只要是请求成功“批量同步任务接口”并返回成功，则标记为1
            if (sync != null){
                if (sync.getSuccess() == null || sync.getSuccess() ==0){//失败的就拿去请求
                    param.setWriterTableSource(sync.getTableName());
                    OlapTableSyncVo vo = new OlapTableSyncVo();
                    vo.setDatabaseId(param.getDatabaseId());
                    vo.setResourceId(param.getResourceId());
                    vo.setWriterTableName(sync.getTableName());
                    vo.setSyncId(sync.getSyncId());
                    vo.setIsNew(false);
                    results.add(vo);
                }else {
                    OlapTableSyncVo vo = new OlapTableSyncVo();
                    vo.setDatabaseId(param.getDatabaseId());
                    vo.setResourceId(param.getResourceId());
                    vo.setSuccess(true);
                    vo.setWriterTableName(sync.getTableName());
                    results.add(vo);
                    iterator.remove();//成功的就不拿去请求了
                }
            }else {//从未请求过的也拿去请求
                param.setWriterTableSource(this.REAL_TABLE_NAME_PREFIX+param.getResourceId());
                OlapTableSyncVo vo = new OlapTableSyncVo();
                vo.setDatabaseId(param.getDatabaseId());
                vo.setResourceId(param.getResourceId());
                vo.setWriterTableName(param.getWriterTableSource());
                vo.setIsNew(true);
                results.add(vo);
            }
        }
        log.info("请求参数:{}",JSON.toJSONString(params));
        UserVO user = (UserVO) SsoContext.getUser();
        String token = SsoContext.getToken();
        String result = "";
        if (!params.isEmpty()){
            result = restToken.postJson(
                this.dataLakeConfig.getHost()+this.dataLakeConfig.getBatchCreateSyncJobUrl(),
                params,token);
        }
        log.info("结果返回:{}",result);
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
                    b.setSuccess(item.getBoolean("success"));
                    oo.setIsNew(b.getIsNew());
                    oo.setSuccess(item.getBoolean("success")?1:0);
                    oo.setCreateBy(user.getUserId());
                    //不为空的时候，就是更新，为空则新增
                    oo.setSyncId(b.getSyncId() == null?ConcurrentSequence.getInstance().getSequence():b.getSyncId());
                    this.save(oo);//保存记录
                });
            });
        }
        return results;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
