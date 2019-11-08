package com.openjava.olap.service;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.*;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.query.OlapCubeDBParam;
import com.openjava.olap.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.common.bean.MyBeanUtils;
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
public class OlapCubeServiceImpl implements OlapCubeService {

    @Resource
    private OlapCubeRepository olapCubeRepository;

    @Resource
    private OlapCubeTableRelationRepository olapCubeTableRelationRepository;

    @Resource
    private OlapFilterCondidionRepository olapFilterCondidionRepository;

    @Resource
    private OlapCubeTableRepository olapCubeTableRepository;

    @Resource
    private OlapCubeTableColumnRepository olapCubeTableColumnRepository;

    @Resource
    private OlapFilterRepository olapFilterRepository;

    @Resource
    private OlapDatalaketableRepository olapDatalaketableRepository;

    @Resource
    private OlapTimingrefreshRepository olapTimingrefreshRepository;

    public Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable) {
        Page<OlapCube> pageresult = olapCubeRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapCube> queryDataOnly(OlapCubeDBParam params, Pageable pageable) {
        return olapCubeRepository.queryDataOnly(params, pageable);
    }

    public OlapCube findTableInfo(String cubeName, Long createId) {
        Optional<OlapCube> o = olapCubeRepository.findTableInfo(cubeName, createId);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        return null;
    }

    public OlapCube findTableInfo(String cubeName) {
        Optional<OlapCube> o = olapCubeRepository.findTableInfo(cubeName);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        return null;
    }

    public OlapCube get(Long id) {
        Optional<OlapCube> o = olapCubeRepository.findById(id);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapCube：" + id);
        return null;
    }

    public OlapCube doSave(OlapCube m) {
        return olapCubeRepository.save(m);
    }

    public void doDelete(Long id) {
        olapCubeRepository.deleteById(id);
    }

    public void deleteCubeName(String cubeName) {
        olapCubeRepository.deleteCubeName(cubeName);
    }

    public List<OlapCube> getOlapShareByShareUserId(String shareUserId) {
        return olapCubeRepository.getOlapShareByShareUserId(shareUserId);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapCubeRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public List<OlapCube> findByUserId(Long createId) {
        return olapCubeRepository.findByUserId(createId);
    }

    @Override
    public List<OlapCube> findAll() {
        return olapCubeRepository.findAll();
    }

    @Override
    public ArrayList<OlapCube> getValidListByUserId(Long userId) {
        return olapCubeRepository.findByCreateIdAndFlags(userId, 1);
    }

    //保存OLAP_CUBE表
    public OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO, Long dimensionLength, Long dimensionFiledLength, Long measureFiledLength) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        //根据名称查询是否已经包含数据
        OlapCube olapCube = null;
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            olapCube = findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setDimensionLength(dimensionLength);
            olapCube.setDimensionFiledLength(dimensionFiledLength);
            olapCube.setMeasureFiledLength(measureFiledLength);
            olapCube.setUpdateId(Long.parseLong(userVO.getUserId()));
            olapCube.setUpdateName(userVO.getUserAccount());
            olapCube.setUpdateTime(date);
            olapCube.setIsNew(false);
        } else {
            olapCube = new OlapCube();
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setCubeId(ConcurrentSequence.getInstance().getSequence());
            olapCube.setDimensionLength(dimensionLength);
            olapCube.setDimensionFiledLength(dimensionFiledLength);
            olapCube.setMeasureFiledLength(measureFiledLength);
            olapCube.setCreateTime(date);
            olapCube.setCreateId(Long.parseLong(userVO.getUserId()));
            olapCube.setCreateName(userVO.getUserAccount());
            olapCube.setFlags(0);
            olapCube.setIsNew(true);
        }
        return olapCube;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean saveTable(OlapCube olapCube, List<OlapCubeTable> cubeTablesList, List<OlapCubeTableRelation> olapcubeList,
                             List<CubeDatalaketableNewMapper> cubeDatalaketableNew, CubeDescMapper cube, ModelsDescDataMapper modelDescData,
                             OlapTimingrefresh timingreFresh, Date date, OaUserVO userVO, List<OlapFilterCondidion> condidions,
                             ArrayList<MeasureMapper> countMappers) throws Exception {
        OlapFilter olapFilter = null;
        if (olapCube.getIsNew() == false) {
            olapCubeTableRepository.deleteCubeId(olapCube.getCubeId());
            olapCubeTableColumnRepository.deleteCubeId(olapCube.getCubeId());
            olapCubeTableRelationRepository.deleteCubeId(olapCube.getCubeId());
            olapFilterCondidionRepository.deleteByCubeName(olapCube.getName());
            olapDatalaketableRepository.deleteCubeName(olapCube.getName());
            olapFilter = olapFilterRepository.findTableInfo(olapCube.getName()).orElse(null);
            if (olapFilter != null) {
                olapFilter.setFilterSql(modelDescData.getFilter_condition());
                olapFilter.setUpdateId(Long.parseLong(userVO.getUserId()));
                olapFilter.setUpdateName(userVO.getUserAccount());
                olapFilter.setUpdateTime(date);
                olapFilter.setIsNew(false);
                olapFilterRepository.save(olapFilter);
            } else {
                olapFilter = new OlapFilter();
                olapFilter.setId(ConcurrentSequence.getInstance().getSequence());
                olapFilter.setFilterSql(modelDescData.getFilter_condition());
                olapFilter.setCubeName(olapCube.getName());
                olapFilter.setCreateTime(date);//创建时间
                olapFilter.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
                olapFilter.setCreateName(userVO.getUserAccount());//创建人名称
                olapFilter.setIsNew(true);
                olapFilterRepository.save(olapFilter);
            }
        } else {
            olapFilter = new OlapFilter();
            olapFilter.setId(ConcurrentSequence.getInstance().getSequence());
            olapFilter.setFilterSql(modelDescData.getFilter_condition());
            olapFilter.setCubeName(olapCube.getName());
            olapFilter.setCreateTime(date);//创建时间
            olapFilter.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
            olapFilter.setCreateName(userVO.getUserAccount());//创建人名称
            olapFilter.setIsNew(true);
            olapFilterRepository.save(olapFilter);
        }

        for (OlapFilterCondidion fc : condidions) {
            OlapFilterCondidion filterCondion = new OlapFilterCondidion();
            filterCondion.setId(ConcurrentSequence.getInstance().getSequence());
            filterCondion.setFilterId(olapFilter.getId());            //过滤表ID
            filterCondion.setTableName(fc.getTableName());  //表名称
            filterCondion.setField(fc.getField());          //表字段
            filterCondion.setPattern(fc.getPattern());      //过滤方式
            filterCondion.setParameter(fc.getParameter());  //过滤值
            filterCondion.setIds(fc.getIds());                //前端需要的ID
            filterCondion.setIsNew(true);
            filterCondion.setCubeName(olapCube.getName());
            olapFilterCondidionRepository.save(filterCondion);
        }

        olapCubeRepository.save(olapCube);

        for (OlapCubeTable tableItem : cubeTablesList) {
            olapCubeTableRepository.save(tableItem);
        }

        for (OlapCubeTableRelation relationItem : olapcubeList) {
            olapCubeTableRelationRepository.save(relationItem);
        }

        if (cubeDatalaketableNew != null) {
            //保存构建时选择的第一步表
            for (CubeDatalaketableNewMapper cdn : cubeDatalaketableNew) {
                for (OlapDatalaketable od : cdn.tableList) {
                    od.setId(ConcurrentSequence.getInstance().getSequence());
                    od.setOrgName(od.getDatabase());
                    od.setIsNew(true);
                    od.setCubeName(olapCube.getName());
                    olapDatalaketableRepository.save(od);
                }
            }
        }
        //保存OLAP_CUBE_TABLE_COLUMN表
        saveCubeTableColumn(cube, olapCube.getCubeId(), cubeTablesList, countMappers);
        //保存CUBE刷新频率
        timingTasks(timingreFresh, cube, date, userVO);
        return true;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTableClone(OlapCube olapCube, ArrayList<OlapCubeTable> cubeTablesList, ArrayList<OlapCubeTableColumn> findByColumn, OlapFilter findTableInfo, ArrayList<OlapCubeTableRelation> olapcubeList, OlapTimingrefresh olapTimingrefresh, List<OlapDatalaketable> datalaketables, OaUserVO loginUser, String cloneCubeName) {
        SequenceService ss = ConcurrentSequence.getInstance();

        OlapCube cloneCube = new OlapCube();
        cloneCube.setFlags(0);
        cloneCube.setIsNew(true);
        cloneCube.setName(cloneCubeName);
        cloneCube.setCubeId(ss.getSequence());
        cloneCube.setDimensionFiledLength(olapCube.getDimensionFiledLength());
        cloneCube.setDimensionLength(olapCube.getDimensionLength());
        cloneCube.setMeasureFiledLength(olapCube.getMeasureFiledLength());
        cloneCube.setRemark(olapCube.getRemark());
        cloneCube.setCreateId(Long.parseLong(loginUser.getUserId()));
        cloneCube.setCreateName(loginUser.getUserAccount());
        cloneCube.setCreateTime(new Date());
        olapCubeRepository.save(cloneCube);


        for (OlapCubeTable cubeTable : cubeTablesList) {
            OlapCubeTable olapCubecTable = new OlapCubeTable();
            MyBeanUtils.copyPropertiesNotBlank(olapCubecTable, cubeTable);
            Long tableId = ss.getSequence();
            //找到table下的列并复制添加数据
            List<OlapCubeTableColumn> findByColumnList = findByColumn.stream().filter(p -> p.getTableId().equals(olapCubecTable.getId())).collect(Collectors.toList());
            for (OlapCubeTableColumn f : findByColumnList) {
                OlapCubeTableColumn olapCubecTableColumn = new OlapCubeTableColumn();
                MyBeanUtils.copyPropertiesNotBlank(olapCubecTableColumn, f);
                olapCubecTableColumn.setTableId(tableId);
                olapCubecTableColumn.setCubeTableColumnId(ss.getSequence());
                olapCubecTableColumn.setCubeId(cloneCube.getCubeId());
                olapCubecTableColumn.setIsNew(true);
                olapCubeTableColumnRepository.save(olapCubecTableColumn);
            }
            olapCubecTable.setCubeTableId(tableId);
            olapCubecTable.setCubeId(cloneCube.getCubeId());
            olapCubecTable.setIsNew(true);
            olapCubeTableRepository.save(olapCubecTable);
        }

        //保存过滤信息
        if (findTableInfo != null) {
            OlapFilter olapFilter = new OlapFilter();
            MyBeanUtils.copyPropertiesNotBlank(olapFilter, findTableInfo);
            olapFilter.setId(ss.getSequence());
            olapFilter.setCubeName(cloneCube.getName());
            olapFilter.setIsNew(true);
            olapFilterRepository.save(olapFilter);

            List<OlapFilterCondidion> filterCondidions = olapFilterCondidionRepository.findByFilterId(findTableInfo.getId());
            for (OlapFilterCondidion filter : filterCondidions) {
                OlapFilterCondidion o = new OlapFilterCondidion();
                MyBeanUtils.copyPropertiesNotBlank(o, filter);
                o.setId(ss.getSequence());
                o.setFilterId(olapFilter.getId());
                o.setIsNew(true);
                olapFilterCondidionRepository.save(o);
            }
        }


        for (OlapCubeTableRelation relationEntity : olapcubeList) {
            OlapCubeTableRelation olapCubeTableRelation = new OlapCubeTableRelation();
            MyBeanUtils.copyPropertiesNotBlank(olapCubeTableRelation, relationEntity);
            olapCubeTableRelation.setId(ss.getSequence());
            olapCubeTableRelation.setCubeId(cloneCube.getCubeId());
            olapCubeTableRelation.setIsNew(true);
            olapCubeTableRelationRepository.save(olapCubeTableRelation);
        }

        if (olapTimingrefresh != null) {
            OlapTimingrefresh olapTiming = new OlapTimingrefresh();
            MyBeanUtils.copyPropertiesNotBlank(olapTiming, olapTimingrefresh);
            olapTiming.setTimingrefreshId(ss.getSequence());
            olapTiming.setCubeName(cloneCube.getName());
            olapTiming.setIsNew(true);
            olapTimingrefreshRepository.save(olapTiming);
        }

        for (OlapDatalaketable olapDatalaketable : datalaketables) {
            OlapDatalaketable datalaketable = new OlapDatalaketable();
            MyBeanUtils.copyPropertiesNotBlank(datalaketable, olapDatalaketable);
            datalaketable.setId(ss.getSequence());
            datalaketable.setCubeName(cloneCubeName);
            datalaketable.setIsNew(true);
            olapDatalaketableRepository.save(datalaketable);
        }
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
                olapCubeTableColumnRepository.save(CubeTableColumn);
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
            olapCubeTableColumnRepository.save(CubeTableColumn);
        }
    }

    //创建定时任务
    public void timingTasks(OlapTimingrefresh task, CubeDescMapper cube, Date date, OaUserVO userVO) {
        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据并修改olap_timingrefresh表数据
        if (StringUtils.isNotBlank(cube.getCubeDescData().getUuid())) {
            OlapTimingrefresh olapTimingrefresh = olapTimingrefreshRepository.findTableInfo(cube.getCubeDescData().getName()).orElse(null);
            if (olapTimingrefresh != null) {
                olapTimingrefresh.setUpdateId(Long.parseLong(userVO.getUserId()));
                olapTimingrefresh.setUpdateName(userVO.getUserAccount());
                olapTimingrefresh.setUpdateTime(date);
                olapTimingrefresh.setIsNew(false);
                olapTimingrefresh.setInterval(task.getInterval());
                olapTimingrefresh.setFrequencytype(task.getFrequencytype());
                olapTimingrefresh.setAutoReload(task.getAutoReload());
                olapTimingrefreshRepository.save(olapTimingrefresh);
            } else {
                task.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
                task.setCreateName(userVO.getUserAccount());//创建人名称
                task.setCreateTime(date);//创建时间
                task.setIsNew(true);
                task.setTimingrefreshId(ConcurrentSequence.getInstance().getSequence());
                task.setCubeName(cube.getCubeDescData().getName());//立方体名称
                olapTimingrefreshRepository.save(task);
            }
        } else {
            task.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
            task.setCreateName(userVO.getUserAccount());//创建人名称
            task.setCreateTime(date);//创建时间
            task.setIsNew(true);
            task.setTimingrefreshId(ConcurrentSequence.getInstance().getSequence());
            task.setCubeName(cube.getCubeDescData().getName());//立方体名称
            olapTimingrefreshRepository.save(task);
        }
    }
}
