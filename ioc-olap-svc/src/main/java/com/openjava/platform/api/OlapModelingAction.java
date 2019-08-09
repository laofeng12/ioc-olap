package com.openjava.platform.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.*;
import com.openjava.platform.domain.*;
import com.openjava.platform.mapper.kylin.*;
import com.openjava.platform.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


/**
 * api接口
 *
 * @author zy
 */
@Api(tags = "模型")
@RestController
@RequestMapping("/olap/apis/OlapModeling")
public class OlapModelingAction extends BaseAction {

    @Resource
    private OlapCubeService olapCubeService;

    @Resource
    private OlapCubeTableService olapCubeTableService;

    @Resource
    private OlapCubeTableColumnService olapCubeTableColumnService;

    @Resource
    private OlapCubeTableRelationService olapCubeTableRelationService;

    @Resource
    private OlapFilterService olapFilterService;

    @Resource
    private OlapFilterCondidionService olapFilterCondidionService;

    @Resource
    private OlapTimingrefreshService olapTimingrefreshService;

    @Resource
    private OlapDatalaketableService olapDatalaketableService;

    @Resource
    ModelsAction modelsAction;

    @Resource
    CubeAction cubeAction;

    @Resource
    JobsAction jobsAction;

    @Resource
    ProjectAction projectAction;

    @Resource
    HiveAction hiveAction;

    @Resource
    EncodingAction encodingAction;


    @ApiOperation(value = "模型列表")
    @RequestMapping(value = "/cubeList", method = RequestMethod.GET)
    @Security(session = true)
    public ArrayList<CubeMapper> cubeList(Integer limit, Integer offset) {
        return cubeAction.list(limit, offset);
    }

    @ApiOperation(value = "构建列表")
    @RequestMapping(value = "/jobsList", method = RequestMethod.GET)
    @Security(session = true)
    public JobsMapper[] jobsList(String jobSearchMode, Long limit, Long offset, String projectName, Long timeFilter) {
        return jobsAction.list(jobSearchMode, limit, offset, projectName, timeFilter);
    }

    @ApiOperation(value = "第一步——检测当前用户是否有Poreject")
    @RequestMapping(value = "/ProjectList", method = RequestMethod.POST)
    @Security(session = false)
    public void projectList(
            @RequestBody ProjectDescDataMapper body,
            @RequestParam(name = "tableName") String[] tableName,
            @RequestParam(name = "libraryName") String libraryName) {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //拿到所有Poreject
        List<ProjectDescDataMapper> projectList = projectAction.list();
        projectList = (List<ProjectDescDataMapper>) projectList.stream()
                .filter(a -> a.getName().equals(userVO.getUserId()
                ));
        //如果当前用户在Poreject没有数据,则在Poreject创建一条数据
        if (projectList.size() != 0) {
            String[] strArrayTrue = (String[]) projectList.get(0).getTables().toArray(new String[0]);
            //对比是否存在,存在的就不要加进去了tables
            String[] tableNameArray = minus(strArrayTrue, tableName);
            if (tableNameArray.length != 0) {
                HiveMapper hive = new HiveMapper();
                hive.tableNameArr = tableNameArray;
                hive.libraryName = libraryName;
                hiveAction.create(hive);
            }
        } else {
            projectAction.create(body);
            if (tableName.length != 0) {
                HiveMapper hive = new HiveMapper();
                hive.tableNameArr = tableName;
                hive.libraryName = libraryName;
                hiveAction.create(hive);
            }
        }
    }


    @ApiOperation(value = "第六步——获取Encoding")
    @RequestMapping(value = "/encodingList", method = RequestMethod.GET)
    @Security(session = true)
    public EncodingDataMapper encodingList() {
        return encodingAction.encodingDataType();
    }


    @ApiOperation(value = "第六步——获取Encoding2")
    @RequestMapping(value = "/encoding2List", method = RequestMethod.GET)
    @Security(session = true)
    public Map<String, Integer> encoding2List() {
        return encodingAction.encodingDataTypeCount();
    }


    @ApiOperation(value = "第七步——完成创建")
    @RequestMapping(value = "/createModeling", method = RequestMethod.POST)
    @Security(session = false)
    public Map<String, Object> createModeling(@RequestBody ModelingMapper body) throws APIException {
        ModelsMapper models = body.getModels();
        CubeDescMapper cube = body.getCube();
        Date date = new Date();
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();

        //为models的name给一个唯一值
        SequenceService ss = ConcurrentSequence.getInstance();
        String modelName = String.valueOf(ss.getSequence());
        String cubeName = cube.cubeDescData.getName();
        models.modelDescData.setName(modelName);
        cube.cubeDescData.setModel_name(modelName);
        cube.setCubeName(modelName + "_" + cubeName);
        cube.cubeDescData.setName(modelName + "_" + cubeName);

        ModelsNewMapper modelMap = new ModelsNewMapper();
        CubeDescNewMapper cubeMap = new CubeDescNewMapper();
        Map<String, Object> paramMap = new HashMap<String, Object>();



        ProjectDescDataMapper projectDesc = new ProjectDescDataMapper();
        projectDesc.setName(userVO.getUserId());
        projectDesc.setDescription(userVO.getUserName());

        OverrideKylinPropertiesMapper override = new OverrideKylinPropertiesMapper();
        override.setAuthor(userVO.getUserName());
        projectAction.create(projectDesc);


        if (!StringUtils.isNotBlank(body.getModels().getUuid())) {
            modelMap = modelsAction.create(models);
            cubeMap = cubeAction.create(cube, modelName);
        } else {
            modelMap = modelsAction.update(models);
            cubeMap = cubeAction.update(cube, modelName);
        }

        paramMap.put("ModesList", modelMap);
        paramMap.put("CubeList", cubeMap);

        //保存OLAP_CUBE表
        OlapCube olapCube = saveCube(cube, date, userVO);
        //保存OLAP_CUBE_TABLE表
        List<OlapCubeTable> cubeTablesList = saveCubeTable(models, cube, olapCube.getCubeId());

        //保存过滤条件
        List<OlapFilter> filterList = new ArrayList<>();
        if (body.getFilterCondidion().size() != 0) {
            filterList = filter(cube, body.getFilterCondidion(), date, userVO);
        }

        //保存OLAP_CUBE_TABLE_RELATION表
        List<OlapCubeTableRelation> olapcubeList = saveCubeTableRelation(cube, models, olapCube.getCubeId(), cubeTablesList);
        //保存除COLUMN以外的所有表和过滤
        boolean column = saveTable(olapCube, cubeTablesList, olapcubeList, filterList, body.getCubeDatalaketableNew());
        if (column == true) {
            //保存OLAP_CUBE_TABLE_COLUMN表
            saveCubeTableColumn(cube, models.getModelDescData(), olapCube.getCubeId(), cubeTablesList);
            //创建定时任务
            if (body.getTimingreFresh().getAutoReload() != 0 || body.getTimingreFresh().getDataMany() != 0) {
                timingTasks(body.getTimingreFresh(), cube, date, userVO);
            }
        }
        return paramMap;
    }


    @Transactional(readOnly = false)
    public boolean saveTable(OlapCube olapCube, List<OlapCubeTable> cubeTablesList, List<OlapCubeTableRelation> olapcubeList, List<OlapFilter> filterList, List<CubeDatalaketableNewMapper> cubeDatalaketableNew) {
        try {
            olapCubeService.doSave(olapCube);

            for (OlapCubeTable tableItem : cubeTablesList) {
                olapCubeTableService.doSave(tableItem);
            }

            for (OlapCubeTableRelation relationItem : olapcubeList) {
                olapCubeTableRelationService.doSave(relationItem);
            }

            for (OlapFilter filterItem : filterList) {
                olapFilterService.doSave(filterItem);
            }

            if (cubeDatalaketableNew != null) {
                //保存构建时选择的第一步表
                for (CubeDatalaketableNewMapper cdn : cubeDatalaketableNew) {
                    SequenceService ss = ConcurrentSequence.getInstance();
                    for (OlapDatalaketable od : cdn.tableList) {
                        od.setId(ss.getSequence());
                        od.setIsNew(true);
                        od.setCubeId(olapCube.getId());
                        olapDatalaketableService.doSave(od);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //保存OLAP_CUBE表
    public OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        Long cubeId = ss.getSequence();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            OlapCube olapCube = olapCubeService.findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setUpdateId(Long.parseLong(userVO.getUserId()));
            olapCube.setUpdateName(userVO.getUserAccount());
            olapCube.setUpdateTime(date);
            olapCube.setIsNew(false);
            return olapCube;
        } else {
            OlapCube olapCube = new OlapCube();
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setCubeId(cubeId);
            olapCube.setCreateTime(date);
            olapCube.setCreateId(Long.parseLong(userVO.getUserId()));
            olapCube.setCreateName(userVO.getUserAccount());
            olapCube.setFlags(0L);
            olapCube.setIsNew(true);
            return olapCube;
        }
    }

    //保存OLAP_CUBE_TABLE表
    public List<OlapCubeTable> saveCubeTable(ModelsMapper models, CubeDescMapper cube, Long cubeId) {
        ModelsDescDataMapper modelDescData = models.modelDescData;

        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        List<OlapCubeTable> cubeTablesList = new ArrayList<>();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除table里的数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            olapCubeTableService.deleteCubeId(cubeId);
        }
        //取到事实表
        String factTable = modelDescData.getFact_table();
        String libraryNameFact = factTable.substring(0, factTable.indexOf("."));
        String tableNameFact = factTable.substring(factTable.indexOf(".") + 1);
        OlapCubeTable cubeTableFact = new OlapCubeTable();
        cubeTableFact.setCubeTableId(ss.getSequence());
        cubeTableFact.setName(tableNameFact);//表中文名称
        cubeTableFact.setCubeId(cubeId);//立方体ID
        cubeTableFact.setTableName(tableNameFact);//表名称
        cubeTableFact.setTableAlias(tableNameFact);//表别名
        cubeTableFact.setIsDict(1);//是否是事实表
        cubeTableFact.setDatabaseName(libraryNameFact);//数据库名称
        cubeTableFact.setIsNew(true);
        cubeTablesList.add(cubeTableFact);
//        olapCubeTableService.doSave(cubeTableFact);

        for (LookupsMapper lm : modelDescData.getLookups()) {
            String libraryName = lm.getTable().substring(0, lm.getTable().indexOf("."));
            String tableName = lm.getTable().substring(lm.getTable().indexOf(".") + 1);
            OlapCubeTable cubeTable = new OlapCubeTable();
            cubeTable.setCubeTableId(ss.getSequence());
            cubeTable.setName(lm.getAlias());//表中文名称
            cubeTable.setCubeId(cubeId);//立方体ID
            cubeTable.setTableName(tableName);//表名称
            cubeTable.setTableAlias(lm.getAlias());//表别名
            cubeTable.setIsDict(0);//是否是事实表
            cubeTable.setDatabaseName(libraryName);//数据库名称
            cubeTable.setIsNew(true);
            cubeTablesList.add(cubeTable);
//          olapCubeTableService.doSave(cubeTable);
        }
        return cubeTablesList;
    }

    //保存OLAP_CUBE_TABLE_COLUMN表
    public void saveCubeTableColumn(CubeDescMapper cube, ModelsDescDataMapper modelDescData, Long cubeId, List<OlapCubeTable> dmEntity) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        ArrayList<String> column = new ArrayList<>();
        ArrayList<LookupsMapper> lookups = modelDescData.getLookups();
        SequenceService ss = ConcurrentSequence.getInstance();
        ArrayList<MeasureMapper> measuresList = cubeDescData.getMeasures();


        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除column里的数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            olapCubeTableColumnService.deleteCubeId(cubeId);
        }
        //Cube里dimensions的处理
        for (DimensionMapper mm : cubeDescData.getDimensions()) {
            //dimensions需要用别名去验证改列属于哪个表
            Optional<OlapCubeTable> dmCube = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(mm.getTable())).findFirst();
            if (dmCube.isPresent()) {
                OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
                CubeTableColumn.setCubeTableColumnId(ss.getSequence());
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
                CubeTableColumn.setColumnAlias(mm.getName());//列别名
                CubeTableColumn.setIsNew(true);
                CubeTableColumn.setExpressionFull("{0}.{1} as {2}");//完整表达式
                olapCubeTableColumnService.doSave(CubeTableColumn);

                column.add(mm.getTable() + "." + CubeTableColumn.getColumnName());
            }
        }


        //Cube里measures的处理
        for (MeasureMapper mm : measuresList) {
            SequenceService columnService = ConcurrentSequence.getInstance();

            if (mm.function.getExpression().equals("COUNT")) {
                continue;
            }

            //measures需要用列名前面的表别名去cubetabla里拿到数据
            String tableNameColumn = mm.function.parameter.getValue();
            if (!mm.function.getExpression().equals("COUNT")) {
                tableNameColumn = tableNameColumn.substring(0, tableNameColumn.indexOf("."));
            }
            String tableColumn = tableNameColumn;
            Optional<OlapCubeTable> dmCube = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(tableColumn)).findFirst();

            if (dmCube.isPresent()) {
                OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
                CubeTableColumn.setCubeTableColumnId(columnService.getSequence());
                CubeTableColumn.setCubeId(cubeId);//立方体ID
                CubeTableColumn.setTableId(dmCube.get().getId());//表ID

                String columnTableName = mm.function.parameter.getValue().substring(mm.function.parameter.getValue().indexOf(".") + 1);
                CubeTableColumn.setColumnAlias(columnTableName);//列别名
                CubeTableColumn.setName(columnTableName);//列中文名称
                CubeTableColumn.setColumnName(columnTableName);//真实列名称

                //3、avg这个kylin前端上面不显示，但是在我们的配置页面需要加上，avg这个对应kylin是sum
                String columnType = mm.function.getExpression() == "AVG" ? "SUM" : mm.function.getExpression();

                String expression = columnType + "({0}.{1}) as {2}";
                CubeTableColumn.setColumnType(mm.function.getReturntype());//列类型 HIVE基本数据类型
                CubeTableColumn.setIsNew(true);
                CubeTableColumn.setExpressionType(columnType);//表达式类型max、min、sum
                CubeTableColumn.setExpressionFull(expression);//完整表达式
                olapCubeTableColumnService.doSave(CubeTableColumn);

                column.add(tableColumn + "." + CubeTableColumn.getColumnName());
            }
        }

        //models里lookups的处理
        for (LookupsMapper lk : lookups) {

            for (int i = 0; i < lk.join.getForeign_key().length; i++) {
                String join = lk.join.getForeign_key()[i];
                String columnType = lk.join.getFk_type()[i];
                saveColumn(column, dmEntity, join, columnType, cubeId);
            }

            for (int i = 0; i < lk.join.getPrimary_key().length; i++) {
                String join = lk.join.getPrimary_key()[i];
                String columnType = lk.join.getPk_type()[i];
                saveColumn(column, dmEntity, join, columnType, cubeId);
            }
        }
    }

    //保存OLAP_CUBE_TABLE_RELATION表
    public List<OlapCubeTableRelation> saveCubeTableRelation(CubeDescMapper cube, ModelsMapper models, Long cubeId, List<OlapCubeTable> dmEntity) {
        ArrayList<LookupsMapper> modelDescData = models.modelDescData.getLookups();
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        List<OlapCubeTableRelation> olapcubeList = new ArrayList<>();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据olap_cube的Id删除column里的数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            olapCubeTableRelationService.deleteCubeId(cubeId);
        }

        for (LookupsMapper lm : modelDescData) {
            OlapCubeTableRelation Relation = new OlapCubeTableRelation();

            String Primary_key = "";
            String Foreign_key = "";
            String Pk_type = StringUtils.join(lm.join.getPk_type(), ",");
            String Fk_type = StringUtils.join(lm.join.getFk_type(), ",");

            String tableName = lm.getTable().substring(lm.getTable().indexOf(".") + 1);
            //去除关联表里每一个列名前面的表别名
            for (String pk : lm.join.getPrimary_key()) {
                Primary_key += pk.substring(pk.indexOf(".") + 1) + ",";
            }
            Primary_key = Primary_key.substring(0, Primary_key.length() - 1);

            for (String fk : lm.join.getForeign_key()) {
                Foreign_key += fk.substring(fk.indexOf(".") + 1) + ",";
            }
            Foreign_key = Foreign_key.substring(0, Foreign_key.length() - 1);


            Optional<OlapCubeTable> tableId = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(lm.getAlias())).findFirst();

            Optional<OlapCubeTable> joinTableId = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(lm.getJoinTable())).findFirst();

            Relation.setId(ss.getSequence());
            Relation.setTableId(joinTableId.get().getId()); //源表id
            Relation.setJoinTableId(tableId.get().getId()); //关联表ID
            Relation.setJoinType(lm.join.getType());            //关联类型
            Relation.setPkKey(Primary_key);                     //主键列名称
            Relation.setFkKey(Foreign_key);                     //外键列名称
            Relation.setPkDataType(Pk_type);                    //主键数据类型
            Relation.setFkDataType(Fk_type);                    //外键数据类型
            Relation.setCubeId(cubeId);                         //立方体ID
            Relation.setIsNew(true);
//            olapCubeTableRelationService.doSave(Relation);
            olapcubeList.add(Relation);
        }
        return olapcubeList;
    }

    //保存过滤
    public List<OlapFilter> filter(CubeDescMapper cube, List<OlapFilterCondidion> filterCondidionList, Date date, OaUserVO userVO) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        Long filterId = ss.getSequence();
        String filterSql = "";//过滤最终形成的sql
        List<OlapFilter> filterList = new ArrayList<>();


        //保存过滤主表
        OlapFilter filter = new OlapFilter();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据并修改olap_filter表数据,同时删除olap_filter_condidion的数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            OlapFilter olapFilter = olapFilterService.findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
            filterId = olapFilter.getId();
            filter.setUpdateId(Long.parseLong(userVO.getUserId()));
            filter.setUpdateName(userVO.getUserAccount());
            filter.setUpdateTime(date);
            filter.setIsNew(false);
            olapFilterCondidionService.deleteCubeId(filterId);
        } else {
            filter.setCreateTime(date);//创建时间
            filter.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
            filter.setCreateName(userVO.getUserAccount());//创建人名称
            filter.setIsNew(true);
        }
        filter.setId(filterId);
        filter.setCubeName(cubeDescData.getName());//立方体名称

        //保存过滤条件
        for (OlapFilterCondidion fc : filterCondidionList) {
            OlapFilterCondidion filterCondion = new OlapFilterCondidion();
            filterCondion.setId(ss.getSequence());
            filterCondion.setFilterId(filterId);            //过滤表ID
            filterCondion.setTablename(fc.getTablename());  //表名称
            filterCondion.setField(fc.getField());          //表字段
            filterCondion.setPattern(fc.getPattern());      //过滤方式
            filterCondion.setParameter(fc.getParameter());  //过滤值

            if (fc.getPattern().equals("BETWEEN")) {
                filterCondion.setParameterbe(fc.getParameterbe());  //ETWEEN过滤值

                filterSql += "select * from " + fc.getTablename() + "" +
                        " where " + fc.getField() + " " +
                        "   " + fc.getPattern() + "" +
                        "   '" + fc.getParameter() + "' " +
                        "   and  " +
                        "   '" + fc.getParameterbe() + "' " +
                        ",";

            } else {
                filterSql += "select * from " + fc.getTablename() + "" +
                        " where " + fc.getField() + " " +
                        "   " + fc.getPattern() + " " +
                        "   '" + fc.getParameter() + "' " +
                        ",";
            }
            filterCondion.setIsNew(true);
            olapFilterCondidionService.doSave(filterCondion);
        }
        filterSql = filterSql.substring(0, filterSql.length() - 1);
        filter.setFilterSql(filterSql);    //过滤最终形成的sql
//        olapFilterService.doSave(filter);
        filterList.add(filter);

        return filterList;
    }

    //创建定时任务
    public void timingTasks(OlapTimingrefresh olapTimingrefresh, CubeDescMapper cube, Date date, OaUserVO userVO) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        Long freshId = ss.getSequence();

        //保存过滤主表
        OlapTimingrefresh task = new OlapTimingrefresh();

        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据并修改olap_timingrefresh表数据
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            OlapTimingrefresh olapFilter = olapTimingrefreshService.findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
            freshId = olapFilter.getId();

            task.setUpdateId(Long.parseLong(userVO.getUserId()));
            task.setUpdateName(userVO.getUserAccount());
            task.setUpdateTime(date);
            task.setIsNew(false);
        } else {
            task.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
            task.setCreateName(userVO.getUserAccount());//创建人名称
            task.setCreateTime(date);//创建时间
            task.setIsNew(true);
        }

        task.setId(freshId);
        task.setCubeName(cubeDescData.getName());//立方体名称
        task.setFrequencytype(olapTimingrefresh.getFrequencytype());//频率类型
        task.setInterval(olapTimingrefresh.getInterval());//间隔
        int interval = olapTimingrefresh.getInterval().intValue();


        Date now = new Date();
        //只获取年月日 时分秒自动填充为00 00 00
        LocalDate localDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date executionTime = java.sql.Date.valueOf(localDate);
        Calendar calendar = Calendar.getInstance();
        //拿到当前小时并加入到年月日组成当前时间
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTime(executionTime);
        calendar.add(Calendar.HOUR, hour);


        Date finaDate = calendar.getTime();
        task.setFinalExecutionTime(finaDate);//最后执行时间


        //当前时间加上间隔时间算出 下一次执行时间
        switch (olapTimingrefresh.getFrequencytype().toString()) {
            case "1"://小时
                calendar.add(Calendar.HOUR, interval);
                break;
            case "2"://天数
                calendar.add(Calendar.DAY_OF_MONTH, +interval);
                break;
            default://月
                calendar.add(Calendar.MONTH, +interval);
                break;
        }
        Date nextDate = calendar.getTime();
        task.setNextExecutionTime(nextDate);//下一次执行执行时间
        olapTimingrefreshService.doSave(task);
    }


    public void saveColumn(ArrayList<String> column, List<OlapCubeTable> dmEntity, String join, String columnType, Long cubeId) {
        SequenceService ss = ConcurrentSequence.getInstance();
        String tableName = join.substring(0, join.indexOf("."));
        String columnName = join.substring(join.indexOf(".") + 1);

        boolean isColumn = column.contains(join);

        Optional<OlapCubeTable> dmCube = dmEntity.stream()
                .filter(p -> p.getTableAlias().equals(tableName)).findFirst();
        //判断是否有这个表
        if (isColumn == false) {
            OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
            CubeTableColumn.setCubeTableColumnId(ss.getSequence());
            CubeTableColumn.setCubeId(cubeId);//立方体ID

            CubeTableColumn.setColumnType(columnType);//列类型 HIVE基本数据类型
            CubeTableColumn.setTableId(dmCube.get().getId());//表ID
            CubeTableColumn.setIsNew(true);
            CubeTableColumn.setExpressionFull("{0}.{1} as {2}");//完整表达式

            CubeTableColumn.setName(columnName);//列中文名称
            CubeTableColumn.setColumnName(columnName);//列名称
            CubeTableColumn.setColumnAlias(columnName);//列别名
            olapCubeTableColumnService.doSave(CubeTableColumn);
        }
    }


    @ApiOperation(value = "立方体:查看")
    @RequestMapping(value = "/desc", method = RequestMethod.GET)
    @Security(session = false)
    public Map<String, Object> desc(String cubeName, String models) {
        ModelsDescDataMapper model = modelsAction.entity(models);
        ArrayList<CubeDescDataMapper> cube = cubeAction.desc(cubeName);
//        ArrayList<OlapDatalaketable> table = olapDatalaketableService.getListByCubeName(cubeName);


//        //移除后端自动添加的_COUNT_
//        for (CubeDescDataMapper cubeDescData : cube) {
//            for (MeasureMapper measure : cubeDescData.measures) {
//                if (measure.getName().equals("_COUNT_")) {
//                    cubeDescData.measures.remove(measure);
//                }
//            }
//        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ModesList", model);
        paramMap.put("CubeList", cube);
//        paramMap.put("TableList", table);
        return paramMap;
    }


    @ApiOperation(value = "立方体:编辑")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @Security(session = true)
    public CubeDescNewMapper update(CubeDescMapper cube) throws APIException {
        return cubeAction.update(cube, null);
    }


    @ApiOperation(value = "立方体:构建")
    @RequestMapping(value = "/build", method = RequestMethod.PUT)
    @Security(session = true)
    public void build(String cubeName, Date start, Date end) {
        cubeAction.build(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:刷新")
    @RequestMapping(value = "/refresh", method = RequestMethod.PUT)
    @Security(session = true)
    public void refresh(String cubeName, Date start, Date end) {
        cubeAction.refresh(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:合并")
    @RequestMapping(value = "/merge", method = RequestMethod.PUT)
    @Security(session = true)
    public void merge(String cubeName, Date start, Date end) {
        cubeAction.build(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:禁用")
    @RequestMapping(value = "/disable", method = RequestMethod.PUT)
    @Security(session = true)
    public void disable(String cubeName) {
//        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
//
//        //修改olap分析的cube表状态值为可用
//        olapCubeService.findTableInfo(cubeName,userVO.getUserId());
//        CubeDescDataMapper cubeEntity = cube.get(0);
//
//
//        cubeEntity.setIsNew(false);


        cubeAction.disable(cubeName);
    }


    @ApiOperation(value = "立方体:启用")
    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
    @Security(session = true)
    public void enable(String cubeName) {
        cubeAction.enable(cubeName);
    }


//    @ApiOperation(value = "立方体:共享")
//    @RequestMapping(value = "/clone", method = RequestMethod.POST)
//    @Security(session = true)
//    public void clone(String cubeName, String projectName) {
//        new CubeAction().clone(cubeName, projectName);
//    }


    @ApiOperation(value = "立方体:复制")
    @RequestMapping(value = "/clone", method = RequestMethod.PUT)
    @Security(session = true)
    public void clone(String cubeName, String projectName) {
        cubeAction.clone(cubeName, projectName);
    }


    @ApiOperation(value = "立方体:删除")
    @RequestMapping(value = "/clone", method = RequestMethod.DELETE)
    @Security(session = true)
    public void deleteCube(String cubeName) {
        cubeAction.delete(cubeName);
    }


    @ApiOperation(value = "构建列表:删除JOB")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @Security(session = true)
    public void deleteJob(String jobsId) {
        jobsAction.delete(jobsId);
    }


    @ApiOperation(value = "查看所有目录")
    @RequestMapping(value = "/selectCatalog", method = RequestMethod.GET)
    @Security(session = false)
    public String selectCatalog() {
        String json = "[{\"orgName\": \"DEFAULT\",\"orgId\": 1},{\"orgName\":\"KYLIN\",\"orgId\":2}]";

        return json;
    }


    @ApiOperation(value = "查看所有表")
    @RequestMapping(value = "/selectTable", method = RequestMethod.GET)
    @Security(session = false)
    public String selectTable(String orgId) {
        String json = "";
        if (orgId.equals("1")) {
            json = "[{\"resourceId\":\"0ff420eb-79ad-40bd-bca9-12d8cd05c60a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"952d11b5-69d9-45d1-92af-227489485e3f\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"e286e39e-41d7-44c2-8fa2-41b365123987\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_STREAMING_TABLE\",\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"f386e39e-40d7-44c2-9eb3-41b365632231\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\"},{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365632882\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"DEFAULT\"},{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365522771\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"database\":\"DEFAULT\"}]";
        } else if (orgId.equals("2")) {
            json = "[{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"f86af73f-c96a-4eb8-9de7-3cca85aae998\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"84bbe31a-384f-45c8-8d5d-c1d311095b5a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"050f3ab8-4c36-49d2-a29d-30e246e7c662\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"19e90d39-6cc3-4d20-b08d-1d8771f1ea1d\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"KYLIN\"}]";
        } else {
            json = "[{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"f86af73f-c96a-4eb8-9de7-3cca85aae998\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"84bbe31a-384f-45c8-8d5d-c1d311095b5a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"050f3ab8-4c36-49d2-a29d-30e246e7c662\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"19e90d39-6cc3-4d20-b08d-1d8771f1ea1d\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"KYLIN\"},{\"resourceId\":\"0ff420eb-79ad-40bd-bca9-12d8cd05c60a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"952d11b5-69d9-45d1-92af-227489485e3f\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"e286e39e-41d7-44c2-8fa2-41b365123987\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_STREAMING_TABLE\",\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}},{\"resourceId\":\"f386e39e-40d7-44c2-9eb3-41b365632231\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\"},{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365632882\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"DEFAULT\"},{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365522771\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"database\":\"DEFAULT\"}]";
        }
        return json;
    }

    @ApiOperation(value = "查看列")
    @RequestMapping(value = "/selectColumn", method = RequestMethod.GET)
    @Security(session = false)
    public String selectColumn(String resourceId) {
        String json = "";
        switch (resourceId) {
            case "0ff420eb-79ad-40bd-bca9-12d8cd05c60a":
                json = "{\"resourceId\":\"0ff420eb-79ad-40bd-bca9-12d8cd05c60a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CAL_DT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"CAL_DT\",\"dataType\":\"date\"},{\"id\":\"2\",\"name\":\"YEAR_BEG_DT\",\"dataType\":\"date\"},{\"id\":\"3\",\"name\":\"QTR_BEG_DT\",\"dataType\":\"date\"},{\"id\":\"4\",\"name\":\"MONTH_BEG_DT\",\"dataType\":\"date\"},{\"id\":\"5\",\"name\":\"WEEK_BEG_DT\",\"dataType\":\"date\"},{\"id\":\"6\",\"name\":\"AGE_FOR_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"AGE_FOR_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"8\",\"name\":\"AGE_FOR_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"9\",\"name\":\"AGE_FOR_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"10\",\"name\":\"AGE_FOR_DT_ID\",\"dataType\":\"smallint\"},{\"id\":\"11\",\"name\":\"AGE_FOR_RTL_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"12\",\"name\":\"AGE_FOR_RTL_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"13\",\"name\":\"AGE_FOR_RTL_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"14\",\"name\":\"AGE_FOR_RTL_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"15\",\"name\":\"AGE_FOR_CS_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"16\",\"name\":\"DAY_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"17\",\"name\":\"DAY_OF_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"18\",\"name\":\"DAY_OF_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"19\",\"name\":\"DAY_OF_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"20\",\"name\":\"DAY_OF_WEEK_ID\",\"dataType\":\"integer\"},{\"id\":\"21\",\"name\":\"WEEK_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"22\",\"name\":\"WEEK_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"23\",\"name\":\"MONTH_OF_QTR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"24\",\"name\":\"MONTH_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"25\",\"name\":\"MONTH_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"26\",\"name\":\"QTR_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"27\",\"name\":\"QTR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"28\",\"name\":\"YEAR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"29\",\"name\":\"YEAR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"30\",\"name\":\"QTR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"MONTH_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"32\",\"name\":\"WEEK_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"33\",\"name\":\"CAL_DT_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"34\",\"name\":\"CAL_DT_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"35\",\"name\":\"CAL_DT_SHORT_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"36\",\"name\":\"YTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"37\",\"name\":\"QTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"38\",\"name\":\"MTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"39\",\"name\":\"WTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"40\",\"name\":\"SEASON_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"41\",\"name\":\"DAY_IN_YEAR_COUNT\",\"dataType\":\"smallint\"},{\"id\":\"42\",\"name\":\"DAY_IN_QTR_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"43\",\"name\":\"DAY_IN_MONTH_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"44\",\"name\":\"DAY_IN_WEEK_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"45\",\"name\":\"RTL_YEAR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"46\",\"name\":\"RTL_QTR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"47\",\"name\":\"RTL_MONTH_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"48\",\"name\":\"RTL_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"49\",\"name\":\"CS_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"50\",\"name\":\"CAL_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"51\",\"name\":\"DAY_OF_WEEK\",\"dataType\":\"varchar(256)\"},{\"id\":\"52\",\"name\":\"MONTH_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"53\",\"name\":\"PRD_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"54\",\"name\":\"PRD_FLAG\",\"dataType\":\"varchar(256)\"},{\"id\":\"55\",\"name\":\"PRD_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"56\",\"name\":\"PRD_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"57\",\"name\":\"QTR_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"58\",\"name\":\"QTR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"59\",\"name\":\"QTR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"60\",\"name\":\"RETAIL_WEEK\",\"dataType\":\"varchar(256)\"},{\"id\":\"61\",\"name\":\"RETAIL_YEAR\",\"dataType\":\"varchar(256)\"},{\"id\":\"62\",\"name\":\"RETAIL_START_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"63\",\"name\":\"RETAIL_WK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"64\",\"name\":\"WEEK_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"65\",\"name\":\"WEEK_NUM_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"66\",\"name\":\"WEEK_BEG_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"67\",\"name\":\"WEEK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"68\",\"name\":\"WEEK_IN_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"69\",\"name\":\"WEEK_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"70\",\"name\":\"WEEK_BEG_END_DESC_MDY\",\"dataType\":\"varchar(256)\"},{\"id\":\"71\",\"name\":\"WEEK_BEG_END_DESC_MD\",\"dataType\":\"varchar(256)\"},{\"id\":\"72\",\"name\":\"YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"73\",\"name\":\"YEAR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"74\",\"name\":\"CAL_DT_MNS_1YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"75\",\"name\":\"CAL_DT_MNS_2YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"76\",\"name\":\"CAL_DT_MNS_1QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"77\",\"name\":\"CAL_DT_MNS_2QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"78\",\"name\":\"CAL_DT_MNS_1MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"79\",\"name\":\"CAL_DT_MNS_2MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"80\",\"name\":\"CAL_DT_MNS_1WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"81\",\"name\":\"CAL_DT_MNS_2WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"82\",\"name\":\"CURR_CAL_DT_MNS_1YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"83\",\"name\":\"CURR_CAL_DT_MNS_2YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"84\",\"name\":\"CURR_CAL_DT_MNS_1QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"85\",\"name\":\"CURR_CAL_DT_MNS_2QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"86\",\"name\":\"CURR_CAL_DT_MNS_1MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"87\",\"name\":\"CURR_CAL_DT_MNS_2MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"88\",\"name\":\"CURR_CAL_DT_MNS_1WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"89\",\"name\":\"CURR_CAL_DT_MNS_2WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"90\",\"name\":\"RTL_MONTH_OF_RTL_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"91\",\"name\":\"RTL_QTR_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"92\",\"name\":\"RTL_WEEK_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"93\",\"name\":\"SEASON_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"94\",\"name\":\"YTM_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"95\",\"name\":\"YTQ_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"96\",\"name\":\"YTW_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"97\",\"name\":\"KYLIN_CAL_DT_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"98\",\"name\":\"KYLIN_CAL_DT_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"99\",\"name\":\"KYLIN_CAL_DT_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"100\",\"name\":\"KYLIN_CAL_DT_UPD_USER\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                break;
            case "952d11b5-69d9-45d1-92af-227489485e3f":
                json = "{\"resourceId\":\"952d11b5-69d9-45d1-92af-227489485e3f\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"dataType\":\"integer\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"dataType\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"dataType\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"dataType\":\"varchar(256)\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"dataType\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"14\",\"name\":\"META_CATEG_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"dataType\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"dataType\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_name\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                break;
            case "e286e39e-41d7-44c2-8fa2-41b365123987":
                json = "{\"resourceId\":\"e286e39e-41d7-44c2-8fa2-41b365123987\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_STREAMING_TABLE\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"AMOUNT\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"2\",\"name\":\"CATEGORY\",\"dataType\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"ORDER_TIME\",\"dataType\":\"timestamp\",\"index\":\"T\"},{\"id\":\"4\",\"name\":\"DEVICE\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"QTY\",\"dataType\":\"integer\"},{\"id\":\"6\",\"name\":\"USER_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_AGE\",\"dataType\":\"integer\"},{\"id\":\"8\",\"name\":\"USER_GENDER\",\"dataType\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"CURRENCY\",\"dataType\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"MINUTE_START\",\"dataType\":\"timestamp\",\"index\":\"T\"},{\"id\":\"12\",\"name\":\"HOUR_START\",\"dataType\":\"timestamp\",\"index\":\"T\"},{\"id\":\"13\",\"name\":\"DAY_START\",\"dataType\":\"date\",\"index\":\"T\"},{\"id\":\"14\",\"name\":\"WEEK_START\",\"dataType\":\"date\",\"index\":\"T\"},{\"id\":\"15\",\"name\":\"MONTH_START\",\"dataType\":\"date\",\"index\":\"T\"},{\"id\":\"16\",\"name\":\"QUARTER_START\",\"dataType\":\"date\",\"index\":\"T\"},{\"id\":\"17\",\"name\":\"YEAR_START\",\"dataType\":\"date\",\"index\":\"T\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                break;
            case "ef3cb5ff-03e2-438d-a729-cbdf60ff39cb":
                json = "{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\",\"comment\":\"Category ID, PK\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"dataType\":\"integer\",\"comment\":\"Site ID, PK\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"dataType\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"dataType\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"dataType\":\"varchar(256)\",\"comment\":\"User Defined Field1\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"dataType\":\"varchar(256)\",\"comment\":\"User Defined Field3\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"dataType\":\"varchar(256)\",\"comment\":\"Last Updated Date\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"dataType\":\"varchar(256)\",\"comment\":\"Last Updated User\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"14\",\"name\":\"META_CATEG_name\",\"dataType\":\"varchar(256)\",\"comment\":\"Level1 Category\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_name\",\"dataType\":\"varchar(256)\",\"comment\":\"Level2 Category\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_name\",\"dataType\":\"varchar(256)\",\"comment\":\"Level3 Category\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"dataType\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"dataType\":\"decimal(10,0)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"dataType\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_name\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"}}";
                break;
            case "f86af73f-c96a-4eb8-9de7-3cca85aae998":
                json = "{\"resourceId\":\"f86af73f-c96a-4eb8-9de7-3cca85aae998\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_SALES\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"TRANS_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"PART_DT\",\"dataType\":\"date\",\"comment\":\"Order Date\"},{\"id\":\"3\",\"name\":\"LSTG_FORMAT_NAME\",\"dataType\":\"varchar(256)\",\"comment\":\"Order Transaction Type\"},{\"id\":\"4\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\",\"comment\":\"Category ID\"},{\"id\":\"5\",\"name\":\"LSTG_SITE_ID\",\"dataType\":\"integer\",\"comment\":\"Site ID\"},{\"id\":\"6\",\"name\":\"SLR_SEGMENT_CD\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"PRICE\",\"dataType\":\"decimal(19,4)\",\"comment\":\"Order Price\"},{\"id\":\"8\",\"name\":\"ITEM_COUNT\",\"dataType\":\"bigint\",\"comment\":\"Number of Purchased Goods\"},{\"id\":\"9\",\"name\":\"SELLER_ID\",\"dataType\":\"bigint\",\"comment\":\"Seller ID\"},{\"id\":\"10\",\"name\":\"BUYER_ID\",\"dataType\":\"bigint\",\"comment\":\"Buyer ID\"},{\"id\":\"11\",\"name\":\"OPS_USER_ID\",\"dataType\":\"varchar(256)\",\"comment\":\"System User ID\"},{\"id\":\"12\",\"name\":\"OPS_REGION\",\"dataType\":\"varchar(256)\",\"comment\":\"System User Region\"}],\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"}}";
                break;
            case "f386e39e-40d7-44c2-9eb3-41b365632231":
                json = "{\"resourceId\":\"f386e39e-40d7-44c2-9eb3-41b365632231\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_ACCOUNT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"ACCOUNT_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"ACCOUNT_BUYER_LEVEL\",\"dataType\":\"integer\"},{\"id\":\"3\",\"name\":\"ACCOUNT_SELLER_LEVEL\",\"dataType\":\"integer\"},{\"id\":\"4\",\"name\":\"ACCOUNT_COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"ACCOUNT_CONTACT\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":null,\"database\":\"DEFAULT\",\"exd\":{},\"cardinality\":{}}}";
                break;
            case "84bbe31a-384f-45c8-8d5d-c1d311095b5a":
                json = "{\"resourceId\":\"84bbe31a-384f-45c8-8d5d-c1d311095b5a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_ACCOUNT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"ACCOUNT_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"ACCOUNT_BUYER_LEVEL\",\"dataType\":\"integer\",\"comment\":\"Account Buyer Level\"},{\"id\":\"3\",\"name\":\"ACCOUNT_SELLER_LEVEL\",\"dataType\":\"integer\",\"comment\":\"Account Seller Level\"},{\"id\":\"4\",\"name\":\"ACCOUNT_COUNTRY\",\"dataType\":\"varchar(256)\",\"comment\":\"Account Country\"},{\"id\":\"5\",\"name\":\"ACCOUNT_CONTACT\",\"dataType\":\"varchar(256)\",\"comment\":\"Account Contact Info\"}],\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"}}";
                break;
            case "050f3ab8-4c36-49d2-a29d-30e246e7c662":
                json = "{\"resourceId\":\"050f3ab8-4c36-49d2-a29d-30e246e7c662\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CAL_DT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"CAL_DT\",\"dataType\":\"date\",\"comment\":\"Date, PK\"},{\"id\":\"2\",\"name\":\"YEAR_BEG_DT\",\"dataType\":\"date\",\"comment\":\"YEAR Begin Date\"},{\"id\":\"3\",\"name\":\"QTR_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Quarter Begin Date\"},{\"id\":\"4\",\"name\":\"MONTH_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Month Begin Date\"},{\"id\":\"5\",\"name\":\"WEEK_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Week Begin Date\"},{\"id\":\"6\",\"name\":\"AGE_FOR_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"AGE_FOR_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"8\",\"name\":\"AGE_FOR_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"9\",\"name\":\"AGE_FOR_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"10\",\"name\":\"AGE_FOR_DT_ID\",\"dataType\":\"smallint\"},{\"id\":\"11\",\"name\":\"AGE_FOR_RTL_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"12\",\"name\":\"AGE_FOR_RTL_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"13\",\"name\":\"AGE_FOR_RTL_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"14\",\"name\":\"AGE_FOR_RTL_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"15\",\"name\":\"AGE_FOR_CS_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"16\",\"name\":\"DAY_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"17\",\"name\":\"DAY_OF_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"18\",\"name\":\"DAY_OF_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"19\",\"name\":\"DAY_OF_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"20\",\"name\":\"DAY_OF_WEEK_ID\",\"dataType\":\"integer\"},{\"id\":\"21\",\"name\":\"WEEK_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"22\",\"name\":\"WEEK_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"23\",\"name\":\"MONTH_OF_QTR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"24\",\"name\":\"MONTH_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"25\",\"name\":\"MONTH_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"26\",\"name\":\"QTR_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"27\",\"name\":\"QTR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"28\",\"name\":\"YEAR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"29\",\"name\":\"YEAR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"30\",\"name\":\"QTR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"MONTH_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"32\",\"name\":\"WEEK_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"33\",\"name\":\"CAL_DT_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"34\",\"name\":\"CAL_DT_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"35\",\"name\":\"CAL_DT_SHORT_name\",\"dataType\":\"varchar(256)\"},{\"id\":\"36\",\"name\":\"YTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"37\",\"name\":\"QTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"38\",\"name\":\"MTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"39\",\"name\":\"WTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"40\",\"name\":\"SEASON_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"41\",\"name\":\"DAY_IN_YEAR_COUNT\",\"dataType\":\"smallint\"},{\"id\":\"42\",\"name\":\"DAY_IN_QTR_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"43\",\"name\":\"DAY_IN_MONTH_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"44\",\"name\":\"DAY_IN_WEEK_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"45\",\"name\":\"RTL_YEAR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"46\",\"name\":\"RTL_QTR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"47\",\"name\":\"RTL_MONTH_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"48\",\"name\":\"RTL_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"49\",\"name\":\"CS_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"50\",\"name\":\"CAL_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"51\",\"name\":\"DAY_OF_WEEK\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"52\",\"name\":\"MONTH_ID\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"53\",\"name\":\"PRD_DESC\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"54\",\"name\":\"PRD_FLAG\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"55\",\"name\":\"PRD_ID\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"56\",\"name\":\"PRD_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"57\",\"name\":\"QTR_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"58\",\"name\":\"QTR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"59\",\"name\":\"QTR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"60\",\"name\":\"RETAIL_WEEK\",\"dataType\":\"varchar(256)\"},{\"id\":\"61\",\"name\":\"RETAIL_YEAR\",\"dataType\":\"varchar(256)\"},{\"id\":\"62\",\"name\":\"RETAIL_START_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"63\",\"name\":\"RETAIL_WK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"64\",\"name\":\"WEEK_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"65\",\"name\":\"WEEK_NUM_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"66\",\"name\":\"WEEK_BEG_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"67\",\"name\":\"WEEK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"68\",\"name\":\"WEEK_IN_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"69\",\"name\":\"WEEK_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"70\",\"name\":\"WEEK_BEG_END_DESC_MDY\",\"dataType\":\"varchar(256)\"},{\"id\":\"71\",\"name\":\"WEEK_BEG_END_DESC_MD\",\"dataType\":\"varchar(256)\"},{\"id\":\"72\",\"name\":\"YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"73\",\"name\":\"YEAR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"74\",\"name\":\"CAL_DT_MNS_1YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"75\",\"name\":\"CAL_DT_MNS_2YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"76\",\"name\":\"CAL_DT_MNS_1QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"77\",\"name\":\"CAL_DT_MNS_2QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"78\",\"name\":\"CAL_DT_MNS_1MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"79\",\"name\":\"CAL_DT_MNS_2MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"80\",\"name\":\"CAL_DT_MNS_1WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"81\",\"name\":\"CAL_DT_MNS_2WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"82\",\"name\":\"CURR_CAL_DT_MNS_1YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"83\",\"name\":\"CURR_CAL_DT_MNS_2YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"84\",\"name\":\"CURR_CAL_DT_MNS_1QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"85\",\"name\":\"CURR_CAL_DT_MNS_2QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"86\",\"name\":\"CURR_CAL_DT_MNS_1MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"87\",\"name\":\"CURR_CAL_DT_MNS_2MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"88\",\"name\":\"CURR_CAL_DT_MNS_1WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"89\",\"name\":\"CURR_CAL_DT_MNS_2WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"90\",\"name\":\"RTL_MONTH_OF_RTL_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"91\",\"name\":\"RTL_QTR_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"92\",\"name\":\"RTL_WEEK_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"93\",\"name\":\"SEASON_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"94\",\"name\":\"YTM_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"95\",\"name\":\"YTQ_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"96\",\"name\":\"YTW_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"97\",\"name\":\"KYLIN_CAL_DT_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"98\",\"name\":\"KYLIN_CAL_DT_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"99\",\"name\":\"KYLIN_CAL_DT_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"100\",\"name\":\"KYLIN_CAL_DT_UPD_USER\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"}}";
                break;
            case "19e90d39-6cc3-4d20-b08d-1d8771f1ea1d":
                json = "{\"resourceId\":\"19e90d39-6cc3-4d20-b08d-1d8771f1ea1d\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_COUNTRY\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"2\",\"name\":\"LATITUDE\",\"dataType\":\"double\"},{\"id\":\"3\",\"name\":\"LONGITUDE\",\"dataType\":\"double\"},{\"id\":\"4\",\"name\":\"name\",\"dataType\":\"varchar(256)\"}],\"source_type\":0,\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"}}";
                break;
            case "e286e39e-40d7-44c2-8fa2-41b365632882":
                json = "{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365632882\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_COUNTRY\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"2\",\"name\":\"LATITUDE\",\"dataType\":\"double\"},{\"id\":\"3\",\"name\":\"LONGITUDE\",\"dataType\":\"double\"},{\"id\":\"4\",\"name\":\"name\",\"dataType\":\"varchar(256)\"}]}}";
                break;
            default:
                json = "{\"resourceId\":\"e286e39e-40d7-44c2-8fa2-41b365522771\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"TRANS_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"PART_DT\",\"dataType\":\"date\"},{\"id\":\"3\",\"name\":\"LSTG_FORMAT_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"4\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\"},{\"id\":\"5\",\"name\":\"LSTG_SITE_ID\",\"dataType\":\"integer\"},{\"id\":\"6\",\"name\":\"SLR_SEGMENT_CD\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"PRICE\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"8\",\"name\":\"ITEM_COUNT\",\"dataType\":\"bigint\"},{\"id\":\"9\",\"name\":\"SELLER_ID\",\"dataType\":\"bigint\"},{\"id\":\"10\",\"name\":\"BUYER_ID\",\"dataType\":\"bigint\"},{\"id\":\"11\",\"name\":\"OPS_USER_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"OPS_REGION\",\"dataType\":\"varchar(256)\"}]}}";
                break;
        }
        return json;
    }


    //求两个数组的差集
    public static String[] minus(String[] arr1, String[] arr2) {
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> history = new LinkedList<String>();
        String[] longerArr = arr1;
        String[] shorterArr = arr2;
        //找出较长的数组来减较短的数组
        if (arr1.length > arr2.length) {
            longerArr = arr2;
            shorterArr = arr1;
        }
        for (String str : longerArr) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        for (String str : shorterArr) {
            if (list.contains(str)) {
                history.add(str);
                list.remove(str);
            } else {
                if (!history.contains(str)) {
                    list.add(str);
                }
            }
        }
        String[] result = {};
        return list.toArray(result);
    }
}
