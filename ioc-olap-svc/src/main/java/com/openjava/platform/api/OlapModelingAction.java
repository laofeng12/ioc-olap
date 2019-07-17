package com.openjava.platform.api;

import com.alibaba.fastjson.JSON;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.api.kylin.HiveAction;
import com.openjava.platform.api.kylin.JobsAction;
import com.openjava.platform.api.kylin.ModelsAction;
import com.openjava.platform.api.kylin.EncodingAction;
import com.openjava.platform.api.kylin.ProjectAction;
import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapCubeTableColumn;
import com.openjava.platform.mapper.kylin.*;
import com.openjava.platform.service.OlapCubeService;
import com.openjava.platform.service.OlapCubeTableColumnService;
import com.openjava.platform.service.OlapCubeTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.ljdp.component.result.BasicApiResponse;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
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
            @RequestParam(name = "tableName") String tableName,
            @RequestParam(name = "libraryName") String libraryName) {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //拿到所有Poreject
        List<ProjectDescDataMapper> projectList = new ProjectAction().list();
        projectList = (List<ProjectDescDataMapper>) projectList.stream()
                .filter(a -> a.getName().equals(userVO.getOrgname()
                ));

        //如果当前用户在Poreject没有数据,则在Poreject创建一条数据
        if (projectList.size() != 0) {
            new ProjectAction().create(body);
            String[] strArrayTrue = (String[]) projectList.get(0).getTables().toArray(new String[0]);
            String[] TableNameArr = tableName.split(",");
            //对比是否存在,存在的就不要加进去了tables
            String[] TableNameArray = minus(strArrayTrue, TableNameArr);
            if (TableNameArray.length != 0) {
                HiveMapper Hive = new HiveMapper();
                Hive.tableNameArr = TableNameArray;
                Hive.libraryName = libraryName;
                new HiveAction().create(Hive);
            }
        } else {
            String[] TableNameArr = tableName.split(",");
            if (TableNameArr.length != 0) {
                HiveMapper Hive = new HiveMapper();
                Hive.tableNameArr = TableNameArr;
                Hive.libraryName = libraryName;
                new HiveAction().create(Hive);
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
        saveCubeTable(models, date, userVO, cubeId);

        //保存OLAP_CUBE_TABLE_COLUMN表
        saveCubeTableColumn(cube, date, userVO, cubeId);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ModesList", modesList);
        paramMap.put("CubeList", cubeList);
        return paramMap;
    }


    //保存OLAP_CUBE表
    public Long saveCube(CubeDescMapper cube, Date date, OaUserVO userVO) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();

        SequenceService ss = ConcurrentSequence.getInstance();

        OlapCube CubeColumn = new OlapCube();
        CubeColumn.setName(cube.getCubeName());
        CubeColumn.setRemark(cubeDescData.getDescription());
        CubeColumn.setCreateTime(date);
        CubeColumn.setCreateId(Long.parseLong(userVO.getUserId()));
        CubeColumn.setCreateName(userVO.getUserAccount());
        CubeColumn.setIsNew(true);
        olapCubeService.doSave(CubeColumn);
        return CubeColumn.getId();
    }

    //保存OLAP_CUBE_TABLE表
    public void saveCubeTable(ModelsMapper models, Date date, OaUserVO userVO, Long cubeId) {
        ModelsDescDataMapper modelDescData = models.getModelDescData();
        for (LookupsMapper lm : modelDescData.getLookups()) {

            OlapCubeTable cubeTable = new OlapCubeTable();
//            cubeTable.setName("");//表中文名称
            cubeTable.setCubeId(cubeId);//立方体ID
            cubeTable.setTableName(lm.getTable());//表名称
            cubeTable.setTableAlias(lm.getAlias());//表别名
            cubeTable.setJoinType(lm.join.getType());//关联类型 left join 或者 inner join
//            cubeTable.setIsDict("");//是否是事实表
//            cubeTable.setJoinTable("");//关联表名
//            cubeTable.setPkKey("");//主键列名称
//            cubeTable.setFkKey("");//外键列名称
//            cubeTable.setPkDataType("");//主键数据类型
//            cubeTable.setFkDataType("");//外键数据类型
//            cubeTable.setRemark("");//备注
//            cubeTable.setIsDict();//数据库名称
            olapCubeTableService.doSave(cubeTable);
        }
    }

    //保存OLAP_CUBE_TABLE_COLUMN表
    public void saveCubeTableColumn(CubeDescMapper cube, Date date, OaUserVO userVO, Long cubeId) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        for (MeasureMapper mm : cubeDescData.getMeasures()) {
            OlapCubeTableColumn CubeTableColumn = new OlapCubeTableColumn();
//            CubeTableColumn.setName("");//列中文名称
//            CubeTableColumn.setCubeId(cubeId);//立方体ID
//            CubeTableColumn.setTableId();//表ID
//            CubeTableColumn.setColumnName();//列名称
//            CubeTableColumn.setColumnAlias("");//列别名
//            CubeTableColumn.setExpressionType(mm.function.parameter.getType());//表达式类型max、min、sum
//            CubeTableColumn.setExpressionFull("");//完整表达式
//            olapCubeTableColumnService.doSave(CubeTableColumn);
        }
    }

    //TABLE_ID                  源表id
    //JOIN_TABLE_ID             关联表ID
    //JOIN_TYPE                 关联类型
    //PK_KEY                    主键列名称
    //FK_KEY                    外键列名称
    //PK_DATA_TYPE              主键数据类型
    //FK_DATA_TYPE              外键数据类型
    //CUBE_ID                   立方体ID




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
