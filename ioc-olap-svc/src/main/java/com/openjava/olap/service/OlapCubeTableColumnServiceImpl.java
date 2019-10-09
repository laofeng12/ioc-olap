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

    //保存OLAP_CUBE_TABLE_COLUMN表
    public void saveCubeTableColumn(CubeDescMapper cube, Long cubeId, List<OlapCubeTable> dmEntity, ArrayList<MeasureMapper> countMappers) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        ArrayList<MeasureMapper> measuresList = cubeDescData.getMeasures();
        //Cube里dimensions的处理
        for (DimensionMapper mm : cubeDescData.getDimensions()) {
            //dimensions需要用别名去验证改列属于哪个表
            Optional<OlapCubeTable> dmCube = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(mm.getTable())).findFirst();
            if (dmCube.isPresent()) {
                OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
                CubeTableColumn.setCubeTableColumnId(ConcurrentSequence.getInstance().getSequence());
                CubeTableColumn.setName(mm.getName());//列中文名称
                CubeTableColumn.setCubeId(cubeId);//立方体ID
                CubeTableColumn.setTableId(dmCube.get().getId());//表ID
                if (StringUtils.isNotBlank(mm.getColumn())) {
                    CubeTableColumn.setColumnName(mm.getColumn());//列名称
                } else {
                    String str = StringUtils.join(mm.getDerived(), ",");
                    CubeTableColumn.setColumnName(str);//列名称
                }
                CubeTableColumn.setColumnType(mm.getColumn_type());//列类型 HIVE基本数据类型
                CubeTableColumn.setLibraryTable(mm.getId());//前端需要的-库名表名
                CubeTableColumn.setColumnAlias(mm.getName());//列别名
                CubeTableColumn.setIsNew(true);
                CubeTableColumn.setExpressionFull("{0}.{1} as {2}");//完整表达式
                doSave(CubeTableColumn);
            }
        }


        //Cube里measures的处理
        for (MeasureMapper mm : measuresList) {
            if (mm.function.getExpression().equals("COUNT")) {
                continue;
            }
            save(cubeId, dmEntity, mm);
        }

        for (MeasureMapper mm : countMappers) {
            save(cubeId, dmEntity, mm);
        }
    }

    private void save(Long cubeId, List<OlapCubeTable> dmEntity, MeasureMapper mm) {
        String tableColumn = mm.function.parameter.getValue().substring(0, mm.function.parameter.getValue().indexOf("."));
        Optional<OlapCubeTable> dmCube = dmEntity.stream()
                .filter(p -> p.getTableAlias().equals(tableColumn)).findFirst();

        if (dmCube.isPresent()) {
            OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
            CubeTableColumn.setCubeTableColumnId(ConcurrentSequence.getInstance().getSequence());
            CubeTableColumn.setCubeId(cubeId);//立方体ID
            CubeTableColumn.setTableId(dmCube.get().getId());//表ID

            String columnTableName = mm.function.parameter.getValue().substring(mm.function.parameter.getValue().indexOf(".") + 1);
            CubeTableColumn.setColumnAlias(mm.getName());//列别名
            CubeTableColumn.setName(mm.getName());//列中文名称
            CubeTableColumn.setColumnName(columnTableName);//真实列名称
            CubeTableColumn.setColumnType(mm.function.getReturntype());//列类型 HIVE基本数据类型
            CubeTableColumn.setIsNew(true);
            CubeTableColumn.setExpressionType(mm.function.getRequestExpression());//表达式类型max、min、sum
            if (mm.function.getRequestExpression().equalsIgnoreCase("COUNT_DISTINCT")) {
                CubeTableColumn.setExpressionFull("COUNT(DISTINCT {0}.{1}) as {2}");//完整表达式
            } else {
                CubeTableColumn.setExpressionFull(mm.function.getRequestExpression() + "({0}.{1}) as {2}");//完整表达式
            }
            doSave(CubeTableColumn);
        }
    }
}
