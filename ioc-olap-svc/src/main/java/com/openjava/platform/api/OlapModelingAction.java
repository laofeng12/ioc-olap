package com.openjava.platform.api;

import com.alibaba.fastjson.JSON;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.api.kylin.HiveAction;
import com.openjava.platform.api.kylin.JobsAction;
import com.openjava.platform.api.kylin.ModelsAction;
import com.openjava.platform.api.kylin.EncodingAction;
import com.openjava.platform.api.kylin.ProjectAction;
import com.openjava.platform.domain.*;
import com.openjava.platform.mapper.kylin.*;
import com.openjava.platform.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.weaver.ast.Var;
import org.hibernate.criterion.Distinct;
import org.ljdp.component.result.BasicApiResponse;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    @ApiOperation(value = "模型列表")
    @RequestMapping(value = "/cubeList", method = RequestMethod.POST)
    @Security(session = true)
    public ArrayList<CubeMapper> cubeList(Integer limit, Integer offset) {
        return new CubeAction().list(limit, offset);
    }

    @ApiOperation(value = "构建列表")
    @RequestMapping(value = "/jobsList", method = RequestMethod.POST)
    @Security(session = true)
    public JobsMapper[] jobsList(String jobSearchMode, Long limit, Long offset, String projectName, Long timeFilter) {
        return new JobsAction().list(jobSearchMode, limit, offset, projectName, timeFilter);
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
        List<ProjectDescDataMapper> projectList = new ProjectAction().list();
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
                new HiveAction().create(hive);
            }
        } else {
            new ProjectAction().create(body);
            if (tableName.length != 0) {
                HiveMapper hive = new HiveMapper();
                hive.tableNameArr = tableName;
                hive.libraryName = libraryName;
                new HiveAction().create(hive);
            }
        }
    }

    @ApiOperation(value = "第六步——获取Encoding")
    @RequestMapping(value = "/encodingList", method = RequestMethod.POST)
    @Security(session = true)
    public EncodingDataMapper encodingList() {
        return new EncodingAction().encodingDataType();
    }


    @ApiOperation(value = "第六步——获取Encoding2")
    @RequestMapping(value = "/encoding2List", method = RequestMethod.POST)
    @Security(session = true)
    public Map<String, Integer> encoding2List() {
        return new EncodingAction().encodingDataTypeCount();
    }


    @ApiOperation(value = "第七步——完成创建")
    @RequestMapping(value = "/createModeling", method = RequestMethod.POST)
    @Security(session = false)
    public Map<String, Object> createModeling(@RequestBody ModelingMapper body) {
        ModelsMapper models = body.getModels();
        CubeDescMapper cube = body.getCube();


        ModelsMapper modesList = new ModelsAction().create(models);
        CubeDescMapper cubeList = new CubeAction().create(cube);
        Date date = new Date();
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();


        //保存OLAP_CUBE表
        Long cubeId = saveCube(cube, date, userVO);
        //保存OLAP_CUBE_TABLE表
        saveCubeTable(models, cube, date, userVO, cubeId);

        //保存过滤条件
        filter(body.getFilterCondidion(), date, userVO, 123L);


        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ModesList", modesList);
        paramMap.put("CubeList", cubeList);
        return paramMap;
    }

    //保存OLAP_CUBE表
    public Long saveCube(CubeDescMapper cube, Date date, OaUserVO userVO) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();

        SequenceService ss = ConcurrentSequence.getInstance();
        Long cubeId = ss.getSequence();

        OlapCube CubeColumn = new OlapCube();
        CubeColumn.setCubeId(ss.getSequence());
        CubeColumn.setName(cubeDescData.getName());
        CubeColumn.setRemark(cubeDescData.getDescription());
        CubeColumn.setCreateTime(date);
        CubeColumn.setCreateId(Long.parseLong(userVO.getUserId()));
        CubeColumn.setCreateName(userVO.getUserAccount());
        CubeColumn.setIsNew(true);
        olapCubeService.doSave(CubeColumn);
        return cubeId;
    }

    //保存OLAP_CUBE_TABLE表
    public void saveCubeTable(ModelsMapper models, CubeDescMapper cube, Date date, OaUserVO userVO, Long cubeId) {
        ModelsDescDataMapper modelDescData = models.modelDescData;

        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();
        List<OlapCubeTable> cubeTablesList = new ArrayList<>();

        //取到事实表
        String factTable = modelDescData.getFact_table();

        //取到cube维度信息
//        ArrayList<DimensionMapper> dimensions = cube.cubeDescData.getDictionaries();

        for (LookupsMapper lm : modelDescData.getLookups()) {
            String libraryName = lm.getTable().substring(0, lm.getTable().indexOf("."));
            OlapCubeTable cubeTable = new OlapCubeTable();

            cubeTable.setCubeTableId(ss.getSequence());
            cubeTable.setName(lm.getAlias());//表中文名称
            cubeTable.setCubeId(cubeId);//立方体ID
            cubeTable.setTableName(lm.getTable());//表名称
            cubeTable.setTableAlias(lm.getAlias());//表别名
            cubeTable.setIsDict(lm.getTable().equals(factTable) ? 1 : 0);//是否是事实表
            cubeTable.setDatabaseName(libraryName);//数据库名称
            cubeTable.setIsNew(true);
            cubeTablesList.add(cubeTable);


            String libraryNamejoin = lm.getJoinTable().substring(0, lm.getJoinTable().indexOf("."));
            OlapCubeTable cubeTableJoin = new OlapCubeTable();
            cubeTableJoin.setCubeTableId(ss.getSequence());
            cubeTableJoin.setName(lm.getJoinTable());//表中文名称
            cubeTableJoin.setCubeId(cubeId);//立方体ID
            cubeTableJoin.setTableName(lm.getJoinTable());//表名称
            cubeTableJoin.setTableAlias(lm.getJoinTable());//表别名
            cubeTableJoin.setIsDict(lm.getJoinTable().equals(factTable) ? 1 : 0);//是否是事实表
            cubeTableJoin.setDatabaseName(libraryNamejoin);//数据库名称
            cubeTableJoin.setIsNew(true);
            cubeTablesList.add(cubeTableJoin);
        }

        //由于是将主表和子表都放进去了,所以需要过滤一下已确保不会录入重复数据
        cubeTablesList = cubeTablesList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTableName()))), ArrayList::new));
        for (OlapCubeTable oct : cubeTablesList) {
            olapCubeTableService.doSave(oct);
        }


        //保存OLAP_CUBE_TABLE_COLUMN表  olapCubeTableService.doSave(cubeTableJoin)
        saveCubeTableColumn(cube, date, userVO, cubeId, cubeTablesList);

        //保存OLAP_CUBE_TABLE_RELATION表
        saveCubeTableRelation(models, date, userVO, cubeId, cubeTablesList);
    }

    //保存OLAP_CUBE_TABLE_COLUMN表
    public void saveCubeTableColumn(CubeDescMapper cube, Date date, OaUserVO userVO, Long cubeId, List<OlapCubeTable> dmEntity) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        SequenceService ss = ConcurrentSequence.getInstance();


        for (DimensionMapper mm : cubeDescData.getDimensions()) {
            Optional<OlapCubeTable> dmCube = dmEntity.stream()
                    .filter(p -> p.getTableAlias().equals(mm.getTable())).findFirst();//mm只有一个getTable 不知道是别名还是表名
            if (dmCube.isPresent()) {
                OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
                CubeTableColumn.setCubeTableColumnId(ss.getSequence());
                CubeTableColumn.setName(mm.getName());//列中文名称
                CubeTableColumn.setCubeId(cubeId);//立方体ID
                CubeTableColumn.setTableId(dmCube.get().getId());//表ID
                CubeTableColumn.setColumnName(mm.getRealName());//列名称
                CubeTableColumn.setColumnAlias(mm.getName());//列别名
                CubeTableColumn.setIsNew(true);
                //CubeTableColumn.setExpressionType();//表达式类型max、min、sum
                CubeTableColumn.setExpressionFull("{0}.{1}");//完整表达式
                olapCubeTableColumnService.doSave(CubeTableColumn);
            }
        }


        for (MeasureMapper mm : cubeDescData.getMeasures()) {
            String tableName = mm.function.parameter.getValue();
            Optional<OlapCubeTable> dmCube = dmEntity.stream()
                    .filter(p -> p.getTableName().equals(tableName)).findFirst();

            if (dmCube.isPresent()) {
                OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
//                CubeTableColumn.setName(mm.getName());//列中文名称
                CubeTableColumn.setCubeId(cubeId);//立方体ID
                CubeTableColumn.setTableId(dmCube.get().getId());//表ID
//                CubeTableColumn.setColumnName(mm.getRealName());//列名称
                CubeTableColumn.setColumnAlias(mm.function.parameter.getValue());//列别名
                CubeTableColumn.setIsNew(true);

                String expression = mm.function.getExpression() + "({0}.{1}) as {2}";

                CubeTableColumn.setExpressionType(mm.function.getExpression());//表达式类型max、min、sum
                CubeTableColumn.setExpressionFull(expression);//完整表达式
                olapCubeTableColumnService.doSave(CubeTableColumn);
            }
        }
    }


    //保存OLAP_CUBE_TABLE_RELATION表
    public void saveCubeTableRelation(ModelsMapper models, Date date, OaUserVO userVO, Long cubeId, List<OlapCubeTable> dmEntity) {
        ArrayList<LookupsMapper> modelDescData = models.modelDescData.getLookups();
        SequenceService ss = ConcurrentSequence.getInstance();
        for (LookupsMapper lm : modelDescData) {
            OlapCubeTableRelation Relation = new OlapCubeTableRelation();

            String Primary_key = StringUtils.join(lm.join.getPrimary_key(), ",");
            String Foreign_key = StringUtils.join(lm.join.getForeign_key(), ",");
            String Pk_type = StringUtils.join(lm.join.getPk_type(), ",");
            String Fk_type = StringUtils.join(lm.join.getFk_type(), ",");

            Optional<OlapCubeTable> tableId = dmEntity.stream()
                    .filter(p -> p.getTableName().equals(lm.getTable())).findFirst();

            Optional<OlapCubeTable> joinTableId = dmEntity.stream()
                    .filter(p -> p.getTableName().equals(lm.getJoinTable())).findFirst();


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
            olapCubeTableRelationService.doSave(Relation);
        }
    }


    //保存过滤
    public void filter(List<OlapFilterCondidion> filterCondidionList, Date date, OaUserVO userVO, Long cubeId) {
        SequenceService ss = ConcurrentSequence.getInstance();
        Long filterId = ss.getSequence();
        String filterSql = "";//过滤最终形成的sql


        //保存过滤主表
        OlapFilter filter = new OlapFilter();
        filter.setId(filterId);
        filter.setCubeId(cubeId);//立方体名称
        filter.setCreateTime(date);//创建时间
//        filter.setCreateId(Long.parseLong("1"));//创建人id
//        filter.setCreateName("2");//创建人名称
        filter.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
        filter.setCreateName(userVO.getUserAccount());//创建人名称
        filter.setIsNew(true);


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
        olapFilterService.doSave(filter);
    }


//    @ApiOperation(value = "立方体:重命名")
//    @RequestMapping(value = "/desc", method = RequestMethod.POST)
//    @Security(session = true)
//    public CubeDescDataMapper desc(String cubeName) {
//        return new CubeAction().desc(cubeName);
//    }


    @ApiOperation(value = "立方体:查看")
    @RequestMapping(value = "/desc", method = RequestMethod.GET)
    @Security(session = true)
    public CubeDescDataMapper desc(String cubeName) {
        return new CubeAction().desc(cubeName);
    }


    @ApiOperation(value = "立方体:编辑")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @Security(session = true)
    public void update(CubeDescMapper cube) {
        new CubeAction().update(cube);
    }


    @ApiOperation(value = "立方体:构建")
    @RequestMapping(value = "/build", method = RequestMethod.PUT)
    @Security(session = true)
    public void build(String cubeName, Date start, Date end) {
        new CubeAction().build(cubeName, start, end);
    }


//    @ApiOperation(value = "立方体:刷新")
//    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
//    @Security(session = true)
//    public void enable(String cubeName) {
//        new CubeAction().enable(cubeName);
//    }


//    @ApiOperation(value = "立方体:合并")
//    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
//    @Security(session = true)
//    public void enable(String cubeName) {
//        new CubeAction().enable(cubeName);
//    }


    @ApiOperation(value = "立方体:禁用")
    @RequestMapping(value = "/disable", method = RequestMethod.PUT)
    @Security(session = true)
    public void disable(String cubeName) {
        new CubeAction().disable(cubeName);
    }


    @ApiOperation(value = "立方体:启用")
    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
    @Security(session = true)
    public void enable(String cubeName) {
        new CubeAction().enable(cubeName);
    }


//    @ApiOperation(value = "立方体:共享")
//    @RequestMapping(value = "/clone", method = RequestMethod.POST)
//    @Security(session = true)
//    public void clone(String cubeName, String projectName) {
//        new CubeAction().clone(cubeName, projectName);
//    }


    @ApiOperation(value = "立方体:复制")
    @RequestMapping(value = "/clone", method = RequestMethod.POST)
    @Security(session = true)
    public void clone(String cubeName, String projectName) {
        new CubeAction().clone(cubeName, projectName);
    }


//    @ApiOperation(value = "立方体:删除")
//    @RequestMapping(value = "/clone", method = RequestMethod.POST)
//    @Security(session = true)
//    public void clone(String cubeName, String projectName) {
//        new CubeAction().clone(cubeName, projectName);
//    }


    @ApiOperation(value = "构建列表:删除JOB")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Security(session = true)
    public void delete(String jobsId) {
        new JobsAction().delete(jobsId);
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
