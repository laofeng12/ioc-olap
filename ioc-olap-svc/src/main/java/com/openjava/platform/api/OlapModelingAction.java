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
    public List<CubeMapper> cubeList(String cubeName, Integer limit, Integer offset) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String projectName = userVO.getUserId();

        //根据当前用户ID去筛选出属于他的数据
        List<CubeMapper> cubeList = cubeAction.list(cubeName, projectName, limit, offset);
//        //为了及时更新olap分析olap_cube表里的flags状态,所以需要根据查询出来的数据去做对比
//        List<OlapCube> olapCubes = olapCubeService.findByUserId(Long.parseLong(userVO.getUserId()));
//        for (CubeMapper cube : cubeList) {
//            Optional<OlapCube> cubeEntity = olapCubes.stream().filter(p -> p.getName().equals(cube.getName())).findFirst();
//            //如果查到了则对比状态值
//            if (cubeEntity.isPresent() && ((cube.getStatus().equals("READY") && cubeEntity.get().getFlags() == 0) || (cube.getStatus().equals("DISABLED") && cubeEntity.get().getFlags() != 0))) {
//                cubeEntity.get().setIsNew(false);
//                cubeEntity.get().setFlags(0);
//                olapCubeService.doSave(cubeEntity.get());
//            }
//        }
        return cubeList;
    }

    @ApiOperation(value = "构建列表")
    @RequestMapping(value = "/jobsList", method = RequestMethod.GET)
    @Security(session = true)
    public JobsMapper[] jobsList(Long limit, Long offset, String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String projectName = userVO.getUserId();
        return jobsAction.list(limit, offset, projectName, cubeName);
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
        cube.setCubeName(cubeName);

        cube.setProject(userVO.getUserId());
        models.setProject(userVO.getUserId());


        //保存前的判断
        saveVerification(cube, models);

        ModelsNewMapper modelMap = new ModelsNewMapper();
        CubeDescNewMapper cubeMap = new CubeDescNewMapper();
        Map<String, Object> paramMap = new HashMap<String, Object>();


        //拿到所有project
        List<ProjectDescDataMapper> projectDescList = projectAction.list();
        Optional<ProjectDescDataMapper> projectList = projectDescList.stream().filter(p -> p.getName().equals(userVO.getUserId())).findFirst();

        //判断是否有该用户的project
        if (!projectList.isPresent()) {
            ProjectDescDataMapper projectDesc = new ProjectDescDataMapper();
            projectDesc.setName(userVO.getUserId());
            projectDesc.setDescription(userVO.getUserName());
            OverrideKylinPropertiesMapper override = new OverrideKylinPropertiesMapper();
            override.setAuthor(userVO.getUserName());
            projectDesc.setOverride_kylin_properties(override);
            projectAction.create(projectDesc);

            //把选择的hive表放入Kylin
            List<String> tableNameList = new ArrayList<String>();
            for (CubeDatalaketableNewMapper datalaketableNew : body.cubeDatalaketableNew) {
                for (OlapDatalaketable table : datalaketableNew.getTableList()) {
                    String name = datalaketableNew.getOrgName() + "." + table.getTableName();
                    tableNameList.add(name);
                }
            }
            hiveAction.create(tableNameList, cube.getProject());
        }


        if (!StringUtils.isNotBlank(body.getModels().getUuid())) {
            modelMap = modelsAction.create(models);
            if (modelMap == null) {
                throw new APIException(400, "模型信息错误！");
            }
            //写死一组COUNT
            MeasureMapper measire = new MeasureMapper();
            measire.setName("_COUNT_");
            measire.setShowDim(true);
            FunctionMapper function = new FunctionMapper();
            function.setExpression("COUNT");
            function.setReturntype("bigint");
            ParameterMapper parameter = new ParameterMapper();
            parameter.setType("constant");
            parameter.setValue("1");
            function.setParameter(parameter);
            measire.setFunction(function);
            cube.cubeDescData.measures.add(measire);
            cubeMap = cubeAction.create(cube, modelName);
            if (cubeMap == null) {
                throw new APIException(400, "立方体信息错误！");
            }
        } else {
            modelMap = modelsAction.update(models);
            cubeMap = cubeAction.update(cube, modelName);
        }

        paramMap.put("ModesList", modelMap);
        paramMap.put("CubeList", cubeMap);

        //保存OLAP_CUBE表
        OlapCube olapCube = olapCubeService.saveCube(cube, date, userVO);
        //保存OLAP_CUBE_TABLE表
        List<OlapCubeTable> cubeTablesList = olapCubeTableService.saveCubeTable(models, cube, olapCube.getCubeId());

        //保存过滤条件
        List<OlapFilter> filterList = new ArrayList<>();
        if (body.getFilterCondidion().size() != 0) {
            filterList = filter(cube, body.getFilterCondidion(), date, userVO);
        }

        //保存OLAP_CUBE_TABLE_RELATION表
        List<OlapCubeTableRelation> olapcubeList = olapCubeTableRelationService.saveCubeTableRelation(cube, models, olapCube.getCubeId(), cubeTablesList);
        //保存除COLUMN以外的所有表和过滤
        boolean column = saveTable(olapCube, cubeTablesList, olapcubeList, filterList, body.getCubeDatalaketableNew());
        if (column == true) {
            //保存OLAP_CUBE_TABLE_COLUMN表
            olapCubeTableColumnService.saveCubeTableColumn(cube, models.getModelDescData(), olapCube.getCubeId(), cubeTablesList);
            //创建定时任务
            if (body.getTimingreFresh().getAutoReload() != 0 || body.getTimingreFresh().getDataMany() != 0) {
                olapTimingrefreshService.timingTasks(body.getTimingreFresh(), cube, date, userVO);
            }
        }
        return paramMap;
    }


    //保存前的验证
    public void saveVerification(CubeDescMapper cube, ModelsMapper models) throws APIException {
        OlapCube olapCube = olapCubeService.findTableInfo(cube.cubeDescData.getName());
        if (olapCube != null) {
            throw new APIException(400, "该立方体名称已存在！");
        }

        //验证高级列组合是否有默认的_COUNT_
        if (cube.cubeDescData.hbase_mapping.getColumn_family().size() == 0) {
            throw new APIException(400, "高级列组合为空！");
        }
        //验证度量是否有数据measures！
        if (cube.cubeDescData.measures.size() == 0) {
            throw new APIException(400, "度量不可为空！");
        } else {
            String[] companyType = {"SUM", "MIN", "MAX"};
            String[] supportType = {"smallint", "int4", "double", "smallint", "int4", "double", "tinyint", "numeric", "long8", "integer", "real", "float", "decimal(19,4)", "bigint"};
            for (MeasureMapper measure : cube.cubeDescData.getMeasures()) {
                if (Arrays.asList(companyType).contains(measure.getFunction().getExpression()) == true && Arrays.asList(supportType).contains(measure.getFunction().getReturntype()) == false) {
                    throw new APIException(400, "度量计算方式与字段类型不匹配！");
                }
            }
        }
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
                        od.setCubeName(olapCube.getName());
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

    //保存过滤
    public List<OlapFilter> filter(CubeDescMapper cube, List<OlapFilterCondidion> filterCondidionList, Date
            date, OaUserVO userVO) {
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


    @ApiOperation(value = "立方体:查看")
    @RequestMapping(value = "/desc", method = RequestMethod.GET)
    @Security(session = false)
    public Map<String, Object> desc(String cubeName, String models) throws APIException {
        ModelsDescDataMapper model = modelsAction.entity(models);
        if (model == null) {
            throw new APIException(400, "网络错误！");
        }

        List<CubeDescDataMapper> cube = (cubeAction.desc(cubeName));

        List<OlapDatalaketable> table = olapDatalaketableService.getListByCubeName(cubeName);
        //data分组
        Map<String, List<OlapDatalaketable>> data = table.stream().collect(Collectors.groupingBy(OlapDatalaketable::getOrgName));
        //整理用户第一步点击选择的表
        List<CubeDatalaketableNewMapper> datalaketableNewList = new ArrayList<CubeDatalaketableNewMapper>();
        for (Map.Entry<String, List<OlapDatalaketable>> entry : data.entrySet()) {
            CubeDatalaketableNewMapper datalaketableNew = new CubeDatalaketableNewMapper();
            List<OlapDatalaketable> mapValue = entry.getValue();
            if (mapValue.size() != 0) {
                datalaketableNew.setOrgId(mapValue.get(0).getOrgId());
                datalaketableNew.setOrgName(mapValue.get(0).getOrgName());

                datalaketableNew.setTableList((ArrayList<OlapDatalaketable>) entry.getValue());
                datalaketableNewList.add(datalaketableNew);
            }
        }


        ArrayList<OlapCubeTableColumn> column = olapCubeTableColumnService.findByColumn(cubeName);


        //1、移除后端自动添加的_COUNT_   2、将原AVG转换成SUM的再次转换回AVG
        for (CubeDescDataMapper cubeDescData : cube) {
            for (MeasureMapper measure : cubeDescData.measures) {
                //1、移除后端自动添加的_COUNT_
                if (measure.getName().equals("_COUNT_")) {
                    cubeDescData.measures.remove(measure);
                }
                //2、将原AVG转换成SUM的再次转换回AVG
                if (measure.function.getExpression().equals("SUM")) {
                    String name = measure.function.parameter.getValue();

                    String tableName = name.substring(0, name.indexOf("."));
                    String columnName = name.substring(name.indexOf(".") + 1);

                    Optional<OlapCubeTableColumn> cubeColumnEntity = column.stream()
                            .filter(p -> p.getColumnName().equals(tableName) && p.getColumnAlias().equals(columnName)).findFirst();
                    //如果能查到该表的数据并且有原类型的话则替换成该原类型
                    if (cubeColumnEntity.isPresent() && !StringUtils.isNotBlank(cubeColumnEntity.get().getPrimaryType())) {
                        measure.function.setExpression(cubeColumnEntity.get().getPrimaryType());
                    }
                }
            }
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ModesList", model);
        paramMap.put("CubeList", cube);
        paramMap.put("TableList", datalaketableNewList);
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
    public void build(String cubeName, Long start, Long end) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //判断是否有存在该立方体名称
        List<JobsMapper> jobList = Arrays.asList(jobsAction.list(100000L, 0L, userVO.getUserId(), cubeName));
        for (JobsMapper jobs : jobList) {
            if (jobs.getJob_status().equals("RUNNING") || jobs.getJob_status().equals("PENDING")) {
                throw new APIException(400, "该立方体已在构建中！");
            }
        }
        cubeAction.build(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:刷新")
    @RequestMapping(value = "/refresh", method = RequestMethod.PUT)
    @Security(session = true)
    public void refresh(String cubeName, Long start, Long end) throws Exception {
        cubeAction.refresh(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:合并")
    @RequestMapping(value = "/merge", method = RequestMethod.PUT)
    @Security(session = true)
    public void merge(String cubeName, Long start, Long end) throws Exception {
        cubeAction.build(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:禁用")
    @RequestMapping(value = "/disable", method = RequestMethod.PUT)
    @Security(session = true)
    public void disable(String cubeName) throws APIException {
        boolean bl = cubeAction.disable(cubeName);
        if (bl == true) {
            //修改olap分析的cube表状态值为不可用
            OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
            olapCube.setIsNew(false);
            olapCube.setFlags(0);
            olapCubeService.doSave(olapCube);
        } else {
            throw new APIException(400, "禁用失败！");
        }
    }


    @ApiOperation(value = "立方体:启用")
    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
    @Security(session = true)
    public void enable(String cubeName) throws APIException {
        boolean bl = cubeAction.enable(cubeName);
        if (bl == true) {
            //修改olap分析的cube表状态值为不可用
            OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
            olapCube.setIsNew(false);
            olapCube.setFlags(0);
            olapCubeService.doSave(olapCube);
        } else {
            throw new APIException(400, "启用失败！");
        }
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
    public void clone(String cubeName, String cubeNameClone) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        List<CubeMapper> cubeList = cubeAction.list(cubeNameClone, userVO.getUserId(), 15, 0);
        if (cubeList.size() == 0) {
            cubeAction.clone(cubeName, cubeNameClone, userVO.getUserId());
        } else {
            throw new APIException(400, "已存在该立方体名称！");
        }
    }


    @ApiOperation(value = "立方体:删除")
    @RequestMapping(value = "/deleteCube", method = RequestMethod.DELETE)
    @Security(session = true)
    public void deleteCube(String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //判断是否有存在该立方体名称
        List<JobsMapper> jobList = Arrays.asList(jobsAction.list(100000L, 0L, userVO.getUserId(), cubeName));
        if (jobList.size() == 0) {
            cubeAction.delete(cubeName);
        } else {
            throw new APIException(400, "请先删除构建！");
        }
    }


    @ApiOperation(value = "构建列表:删除JOB")
    @RequestMapping(value = "/deleteJob", method = RequestMethod.DELETE)
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
    public List<String> selectColumn(String[] resourceIds) {
        List<String> list = new ArrayList<String>();
        for (String resourceId : resourceIds) {
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
            list.add(json);
        }

        return list;
    }

    @ApiOperation(value = "查看job某一步骤的详细返回数据")
    @RequestMapping(value = "/getJobStepOut", method = RequestMethod.GET)
    @Security(session = true)
    public JobStepOutputMapper getJobStepOut(String jobId,String stepId) {
        return jobsAction.output(jobId,stepId);
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
