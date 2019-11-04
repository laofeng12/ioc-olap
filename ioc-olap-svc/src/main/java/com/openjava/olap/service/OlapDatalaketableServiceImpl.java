package com.openjava.olap.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.mapper.kylin.CubeMapper;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.query.OlapDatalaketableDBParam;
import com.openjava.olap.repository.OlapDatalaketableRepository;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        list.forEach(s->params.add(DataLakeJobQueryParam.builder().resourceId(s.getResourceId()).cubeName(s.getCubeName())
            .databaseId(String.valueOf(s.getDatabaseId())).type(s.getType()).syncSource(s.getType()).build()));
        return params;
    }

    /**
     * 查询即将返回的模型列表的每个cube对应的同步数据状态
     * @param cubeList
     * @return
     */
    public List<CubeMapper> querySyncState(List<CubeMapper> cubeList){
        String token = SsoContext.getToken();
        String userAgent = ((OaUserVO)SsoContext.getUser()).getUserAgent();
        List<String> cubeNameList = new ArrayList<>();
        cubeList.forEach(s-> cubeNameList.add(s.getName()));
        List<DataLakeJobQueryParam> list = this.queryListInCubeNameList(cubeNameList);
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization",token);
        headers.set("User-Agent",userAgent);
        HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(list), headers);
        JSONObject obj = rest.postForObject(dataLakeConfig.getHost()+dataLakeConfig.getQuerySyncJobStateUrl(),entity,JSONObject.class);
        JSONArray array;
        if (obj != null && obj.containsKey("data") && (array = obj.getJSONArray("data")) != null && array.size()>0){
            for (int i = 0; i< array.size();i++){
                int finalI = i;
                list.stream().filter(s->s.getResourceId().equals(array.getJSONObject(finalI).getString("resourceId"))).forEach(s->s.setSyncStatus(array.getJSONObject(finalI).getInteger("status")));
            }
            for (CubeMapper c : cubeList){
                int state = 0;//默认为不成功
                for (DataLakeJobQueryParam p : list){
                    // 只要有一个job返回的状态是4-成功，则该cube能创建
                    if (c.getName().equals(p.getCubeName()) && p.getSyncStatus()!=null && p.getSyncStatus() == 4){
                        state = 1;
                        break;
                    }
                }
                c.setSyncStatus(state);
            }
        }
        return cubeList;
    }
}
