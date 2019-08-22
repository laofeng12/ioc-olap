package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapCubeTableRelation;
import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.mapper.kylin.ModelsMapper;
import com.openjava.platform.query.OlapCubeTableDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


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
