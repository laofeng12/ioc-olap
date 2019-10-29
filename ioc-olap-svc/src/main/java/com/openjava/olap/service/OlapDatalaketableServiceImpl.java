package com.openjava.olap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.mapper.kylin.CubeMapper;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.query.OlapDatalaketableDBParam;
import com.openjava.olap.repository.OlapDatalaketableRepository;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 构建选择表业务层
 *
 * @author zy
 */
@Service
@Transactional
public class OlapDatalaketableServiceImpl implements OlapDatalaketableService {

    @Resource
    private OlapDatalaketableRepository olapDatalaketableRepository;

    @Autowired
    DataLakeConfig dataLakeConfig;

    public Page<OlapDatalaketable> query(OlapDatalaketableDBParam params, Pageable pageable) {
        Page<OlapDatalaketable> pageresult = olapDatalaketableRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapDatalaketable> queryDataOnly(OlapDatalaketableDBParam params, Pageable pageable) {
        return olapDatalaketableRepository.queryDataOnly(params, pageable);
    }

    public OlapDatalaketable get(Long id) {
        Optional<OlapDatalaketable> o = olapDatalaketableRepository.findById(id);
        if (o.isPresent()) {
            OlapDatalaketable m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapDatalaketable：" + id);
        return null;
    }

    public OlapDatalaketable doSave(OlapDatalaketable m) {
        return olapDatalaketableRepository.save(m);
    }

    public void deleteCubeName(String cubeName) {
        olapDatalaketableRepository.deleteCubeName(cubeName);
    }

    public void doDelete(Long id) {
        olapDatalaketableRepository.deleteById(id);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapDatalaketableRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public List<OlapDatalaketable> getListByCubeName(String cubeName) {
        return olapDatalaketableRepository.getListByCubeName(cubeName);
    }

    @Override
    public List<DataLakeJobQueryParam> queryListInCubeNameList(List<String> cubeNameList) {
        List<OlapDatalaketable> list = olapDatalaketableRepository.queryListInCubeNameList(cubeNameList);
        List<DataLakeJobQueryParam> params = new ArrayList<>();
        list.forEach(s->params.add(DataLakeJobQueryParam.builder().cubeName(s.getCubeName())
            .databaseId(s.getDatabaseId().toString()).type(s.getType()).syncSource(s.getType()).build()));
        return params;
    }

    /**
     * 查询即将返回的模型列表的每个cube对应的同步数据状态
     * @param cubeList
     * @return
     */
    public List<CubeMapper> querySyncState(List<CubeMapper> cubeList){
        String token = SsoContext.getToken();
        List<String> cubeNameList = new ArrayList<>();
        cubeList.forEach(s-> cubeNameList.add(s.getName()));
        List<DataLakeJobQueryParam> list = this.queryListInCubeNameList(cubeNameList);
        JSONObject obj = HttpClient.post(dataLakeConfig.getHost()+dataLakeConfig.getQuerySyncJobStateUrl(), JSON.toJSONString(list),token,JSONObject.class);
        if (obj.containsKey("data")){
            JSONArray array = obj.getJSONArray("data");
            for (int i = 0; i< array.size();i++){
                int finalI = i;
                list.stream().filter(s->s.getResourceId().equals(array.getJSONObject(finalI).getString("resourceId"))).forEach(s->s.setSyncStatus(array.getJSONObject(finalI).getInteger("status")));
            }
        }
        list.forEach(s-> cubeList.stream().filter(t->t.getName().equals(s.getCubeName()))
            .forEach(r->r.setSyncStatus(s.getSyncStatus())));
        return cubeList;
    }
}
