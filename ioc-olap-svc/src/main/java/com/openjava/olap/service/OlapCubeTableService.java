package com.openjava.olap.service;

import java.util.ArrayList;
import java.util.List;

import com.openjava.olap.domain.OlapCubeTable;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.mapper.kylin.ModelsMapper;
import com.openjava.olap.query.OlapCubeTableDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 文件夹表业务层接口
 *
 * @author xiepc
 */
public interface OlapCubeTableService {
    Page<OlapCubeTable> query(OlapCubeTableDBParam params, Pageable pageable);

    List<OlapCubeTable> queryDataOnly(OlapCubeTableDBParam params, Pageable pageable);

    OlapCubeTable get(Long id);

    OlapCubeTable doSave(OlapCubeTable m);

    void doDelete(Long id);

    void doRemove(String ids);

    void deleteCubeId(Long cubeId);

    ArrayList<OlapCubeTable> getListByCubeId(Long cubeId);

    ArrayList<OlapCubeTable> findByTable(String cubeName);

    List<OlapCubeTable> saveCubeTable(ModelsMapper models, CubeDescMapper cube, Long cubeId);
}
