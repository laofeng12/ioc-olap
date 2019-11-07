package com.openjava.olap.service;

import com.openjava.olap.common.DataLakeConfig;
import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.query.DataLakeJobQueryParam;
import com.openjava.olap.query.OlapDatalaketableDBParam;
import com.openjava.olap.repository.OlapDatalaketableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
