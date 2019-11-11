package com.openjava.olap.service;

import com.openjava.olap.domain.OlapCubeTable;
import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.query.OlapCubeTableDBParam;
import com.openjava.olap.repository.OlapCubeTableRepository;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapCubeTableServiceImpl implements com.openjava.olap.service.OlapCubeTableService {

    @Resource
    private OlapCubeTableRepository olapCubeTableRepository;

    public Page<OlapCubeTable> query(OlapCubeTableDBParam params, Pageable pageable) {
        Page<OlapCubeTable> pageresult = olapCubeTableRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapCubeTable> queryDataOnly(OlapCubeTableDBParam params, Pageable pageable) {
        return olapCubeTableRepository.queryDataOnly(params, pageable);
    }

    public OlapCubeTable get(Long id) {
        Optional<OlapCubeTable> o = olapCubeTableRepository.findById(id);
        if (o.isPresent()) {
            OlapCubeTable m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapCubeTable：" + id);
        return null;
    }

    public OlapCubeTable doSave(OlapCubeTable m) {
        return olapCubeTableRepository.save(m);
    }

    public void doDelete(Long id) {
        olapCubeTableRepository.deleteById(id);
    }

    public void deleteCubeId(Long cubeId) {
        olapCubeTableRepository.deleteCubeId(cubeId);
    }


    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapCubeTableRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public ArrayList<OlapCubeTable> getListByCubeId(Long cubeId) {
        return olapCubeTableRepository.findByCubeId(cubeId);
    }

    public ArrayList<OlapCubeTable> findByTable(String cubeName) {
        return olapCubeTableRepository.findByTable(cubeName);
    }

    //保存OLAP_CUBE_TABLE表
    public List<OlapCubeTable> saveCubeTable(ModelsMapper models, CubeDescMapper cube, Long cubeId, List<CubeDatalaketableNewMapper> cubeDatalaketableNew) {
        ModelsDescDataMapper modelDescData = models.modelDescData;
        SequenceService ss = ConcurrentSequence.getInstance();
        List<OlapCubeTable> cubeTablesList = new ArrayList<>();
        String factFull = models.modelDescData.getFact_table();
        String factDatabase = factFull.substring(factFull.indexOf(".") + 1);
        String factTable = factFull.substring(factFull.indexOf(".") + 1);
        OlapCubeTable cubeTable = new OlapCubeTable();
        cubeTable.setCubeTableId(ss.getSequence());
        cubeTable.setName(factTable);//表中文名称
        cubeTable.setCubeId(cubeId);//立方体ID
        cubeTable.setTableName(factTable);//表名称
        cubeTable.setTableAlias(factTable);//表别名
        cubeTable.setIsDict(1);//是否是事实表
        cubeTable.setDatabaseName(factDatabase);//数据库名称
        LookupsMapper dictMapper = modelDescData.getLookups().stream().filter(p -> p.getJoinAlias().equals(factTable)).findFirst().orElse(null);
        cubeTable.setSAxis(dictMapper.getSAxis());//S轴
        cubeTable.setYAxis(dictMapper.getYAxis());//Y轴
        cubeTable.setTableId(dictMapper.getJoinId());
        cubeTable.setIsNew(true);
        cubeTablesList.add(cubeTable);

        for (LookupsMapper lm : modelDescData.getLookups()) {
            String libraryName = lm.getTable().substring(0, lm.getTable().indexOf("."));
            String tableName = lm.getTable().substring(lm.getTable().indexOf(".") + 1);
            cubeTable = new OlapCubeTable();
            cubeTable.setCubeTableId(ss.getSequence());
            cubeTable.setName(lm.getAlias());//表中文名称
            cubeTable.setCubeId(cubeId);//立方体ID
            cubeTable.setTableName(tableName);//表名称
            cubeTable.setVirtualTableName(lm.getVirtualTableName());// 虚拟表名
            cubeTable.setTableAlias(lm.getAlias());//表别名
            cubeTable.setIsDict(0);//是否是事实表
            cubeTable.setDatabaseName(libraryName);//数据库名称
            cubeTable.setSAxis(lm.getSAxis());//S轴
            cubeTable.setYAxis(lm.getYAxis());//Y轴
            cubeTable.setJoinSAxis(lm.getJoinSAxis());//S轴
            cubeTable.setJoinYAxis(lm.getJoinYAxis());//Y轴
            cubeTable.setJoinTable(lm.getJoinTable());
            cubeTable.setJoinId(lm.getJoinId());
            cubeTable.setJoinAlias(lm.getJoinAlias());
            cubeTable.setTableId(lm.getId());
            cubeTable.setIsNew(true);
            cubeTablesList.add(cubeTable);
        }
        return cubeTablesList;
    }

}
