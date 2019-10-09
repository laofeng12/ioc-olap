package com.openjava.olap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.olap.domain.OlapCubeTable;
import com.openjava.olap.domain.OlapCubeTableColumn;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.query.OlapCubeTableColumnDBParam;
import com.openjava.olap.repository.OlapCubeTableColumnRepository;
import com.openjava.olap.service.OlapCubeTableColumnService;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapCubeTableColumnServiceImpl implements OlapCubeTableColumnService {

    @Resource
    private OlapCubeTableColumnRepository olapCubeTableColumnRepository;

    public Page<OlapCubeTableColumn> query(OlapCubeTableColumnDBParam params, Pageable pageable) {
        Page<OlapCubeTableColumn> pageresult = olapCubeTableColumnRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapCubeTableColumn> queryDataOnly(OlapCubeTableColumnDBParam params, Pageable pageable) {
        return olapCubeTableColumnRepository.queryDataOnly(params, pageable);
    }

    public OlapCubeTableColumn get(Long id) {
        Optional<OlapCubeTableColumn> o = olapCubeTableColumnRepository.findById(id);
        if (o.isPresent()) {
            OlapCubeTableColumn m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapCubeTableColumn：" + id);
        return null;
    }

    public ArrayList<OlapCubeTableColumn> findByColumn(String cubeName) {
        return olapCubeTableColumnRepository.findByColumn(cubeName);
    }

    public ArrayList<OlapCubeTableColumn> findByCubeTableId(Long tableId) {
        return olapCubeTableColumnRepository.findByCubeTableId(tableId);
    }

    public OlapCubeTableColumn doSave(OlapCubeTableColumn m) {
        return olapCubeTableColumnRepository.save(m);
    }

    public void doDelete(Long id) {
        olapCubeTableColumnRepository.deleteById(id);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapCubeTableColumnRepository.deleteById(new Long(items[i]));
        }
    }

    public void deleteCubeId(Long cubeId) {
        olapCubeTableColumnRepository.deleteCubeId(cubeId);
    }


    @Override
    public ArrayList<OlapCubeTableColumn> getListByTableId(Long cubeTableId) {
        return olapCubeTableColumnRepository.findByTableId(cubeTableId);
    }


}
