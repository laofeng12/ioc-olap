package com.openjava.platform.service;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapDatalaketable;
import com.openjava.platform.mapper.kylin.*;
import com.openjava.platform.query.OlapCubeTableDBParam;
import com.openjava.platform.repository.OlapCubeTableRepository;
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
public class OlapCubeTableServiceImpl implements OlapCubeTableService {

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

        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        List<OlapCubeTable> cubeTablesList = new ArrayList<>();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除table里的数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            deleteCubeId(cubeId);
        }

        String factTable = models.modelDescData.getFact_table();

        for (LookupsMapper lm : modelDescData.getLookups()) {
            String libraryName = lm.getTable().substring(0, lm.getTable().indexOf("."));
            String tableName = lm.getTable().substring(lm.getTable().indexOf(".") + 1);

            String factTableName = factTable.substring(factTable.indexOf(".") + 1);
            OlapCubeTable cubeTable = new OlapCubeTable();
            cubeTable.setCubeTableId(ss.getSequence());
            cubeTable.setName(lm.getAlias());//表中文名称
            cubeTable.setCubeId(cubeId);//立方体ID
            cubeTable.setTableName(tableName);//表名称
            cubeTable.setTableAlias(lm.getAlias());//表别名
            cubeTable.setIsDict(lm.getJoinTable() == factTableName ? 1 : 0);//是否是事实表
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

            //通过cubeDatalaketableNew去查找子表的库名等信息
            OlapDatalaketable datalaketable = new OlapDatalaketable();
            for (CubeDatalaketableNewMapper tableList : cubeDatalaketableNew) {
                for (OlapDatalaketable t : tableList.getTableList()) {
                    if (t.getTable_id().equals(lm.getJoinId())) {
                        datalaketable = t;
                        break;
                    }
                }
            }
            OlapCubeTable cubeJoinTable = new OlapCubeTable();
            cubeJoinTable.setCubeTableId(ss.getSequence());
            cubeJoinTable.setName(lm.getJoinTable());//表中文名称
            cubeJoinTable.setCubeId(cubeId);//立方体ID
            cubeJoinTable.setTableName(datalaketable.getTable_name());//表名称
            cubeJoinTable.setTableAlias(lm.getJoinAlias());//表别名
            cubeJoinTable.setIsDict(lm.getJoinTable() == factTableName ? 1 : 0);//是否是事实表
            cubeJoinTable.setDatabaseName(datalaketable.getDatabase());//数据库名称
            cubeJoinTable.setSAxis(lm.getSAxis());//S轴
            cubeJoinTable.setYAxis(lm.getYAxis());//Y轴
            cubeJoinTable.setJoinSAxis(lm.getJoinSAxis());//S轴
            cubeJoinTable.setJoinYAxis(lm.getJoinYAxis());//Y轴
            cubeJoinTable.setJoinTable(lm.getJoinTable());
            cubeJoinTable.setJoinId(lm.getJoinId());
            cubeJoinTable.setJoinAlias(lm.getJoinAlias());
            cubeJoinTable.setTableId(lm.getId());
            cubeJoinTable.setIsNew(true);
            cubeTablesList.add(cubeJoinTable);
        }
        List<OlapCubeTable> cubeTables = cubeTablesList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(OlapCubeTable::getTableAlias))), ArrayList::new)
        );
        return cubeTables;
    }

}
