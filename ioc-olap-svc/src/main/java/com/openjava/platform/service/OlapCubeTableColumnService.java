package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;

import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapCubeTableColumn;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.mapper.kylin.ModelsDescDataMapper;
import com.openjava.platform.query.OlapCubeTableColumnDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 文件夹表业务层接口
 *
 * @author xiepc
 */
public interface OlapCubeTableColumnService {
    Page<OlapCubeTableColumn> query(OlapCubeTableColumnDBParam params, Pageable pageable);

    List<OlapCubeTableColumn> queryDataOnly(OlapCubeTableColumnDBParam params, Pageable pageable);

    OlapCubeTableColumn get(Long id);

    OlapCubeTableColumn doSave(OlapCubeTableColumn m);

    void saveCubeTableColumn(CubeDescMapper cube, ModelsDescDataMapper modelDescData, Long cubeId, List<OlapCubeTable> dmEntity);

    void doDelete(Long id);

    void doRemove(String ids);

    void deleteCubeId(Long cubeId);

    ArrayList<OlapCubeTableColumn> findByColumn(String cubeName);

    ArrayList<OlapCubeTableColumn> findByCubeTableId(Long tableId);

    ArrayList<OlapCubeTableColumn> getListByTableId(Long cubeTableId);
}
