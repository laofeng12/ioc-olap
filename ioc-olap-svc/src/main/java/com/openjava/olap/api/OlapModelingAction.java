package com.openjava.olap.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.common.TableNameTransposition;
import com.openjava.olap.common.kylin.*;
import com.openjava.olap.domain.*;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.service.*;
import com.openjava.olap.vo.CubeListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * api接口
 *
 * @author zy
 */
@Api(tags = "模型")
@RestController
@RequestMapping("/olap/apis/OlapModeling")
@Slf4j
public class OlapModelingAction extends BaseAction {

    @Resource
    private OlapCubeService olapCubeService;

    @Resource
    private com.openjava.olap.service.OlapCubeTableService olapCubeTableService;

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
    private OlapCubeBuildService olapCubeBuildService;

    @Autowired
    ModelHttpClient modelHttpClient;
    @Autowired
    CubeHttpClient cubeHttpClient;
    @Autowired
    JobHttpClient jobsHttpClient;
    @Autowired
    ProjectHttpClient projectHttpClient;
    @Autowired
    HiveHttpClient hiveHttpClient;
    @Autowired
    TableHttpClient tableHttpClient;

    private static Object lock = new Object();


    @ApiOperation(value = "模型列表")
    @RequestMapping(value = "/cubeList", method = RequestMethod.GET)
    @Security(session = true)
    public CubeListVo cubeList(String cubeName, Integer limit, Integer offset, int dateType) throws APIException {
        // 先查询麒麟的模型列表，后读取数据库的模型状态字段，之所以保留查询麒麟列表，是因为要返回date_range_end上一次构建时间
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String projectName = userVO.getUserId();
        List<CubeMapper> cubeList = new ArrayList<>();
        boolean isNext = false;
        Integer total = limit + offset;

        //0共享模型、1自建
        if (dateType == 0) {
            //查询出分享数据的前十五条
            List<OlapCube> shareList = olapCubeService.getOlapShareByShareUserId(userVO.getUserId());
            if (shareList.size() == 0 || offset > shareList.size()) {
                return new CubeListVo(cubeList, false);
            } else {
                if (shareList.size() > total) {
                    isNext = true;
                    shareList = shareList.subList(offset, total);
                } else {
                    shareList = shareList.subList(offset, shareList.size());
                }
            }

            //循环在麒麟找到分享的数据并加入数据里
            for (OlapCube o : shareList) {
                List<CubeMapper> cubeListMa = cubeHttpClient.list(o.getName(), String.valueOf(o.getCreateId()), limit, offset);
                Optional<CubeMapper> cubeEntity = cubeListMa.stream().filter(p -> p.getName().equals(o.getName())).findFirst();
                if (cubeEntity.isPresent()) {
                    cubeList.add(cubeEntity.get());
                }
            }
        } else {
            cubeList = cubeHttpClient.list(cubeName, projectName, limit + 1, offset);
            if (cubeList.size() > limit) {
                isNext = true;
                cubeList = cubeList.subList(0, limit);
            }
        }
        this.olapCubeService.resetCubeStatus(cubeList);
        return new CubeListVo(cubeList, isNext);
    }

    @ApiOperation(value = "构建列表")
    @RequestMapping(value = "/jobsList", method = RequestMethod.GET)
    @Security(session = true)
    public List<JobsMapper> jobsList(Long limit, Long offset, String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String projectName = userVO.getUserId();
        ProjectDescDataMapper projectDescDataMapper = projectHttpClient.get(projectName);
        if (projectDescDataMapper != null) {
            JobsMapper[] jobs = jobsHttpClient.list(limit, offset, projectName, cubeName);
            List<JobsMapper> jobsList = Arrays.asList(jobs);
            return jobsList;
        }
        return new ArrayList<>();
    }

    @ApiOperation(value = "第六步——获取Encoding")
    @RequestMapping(value = "/encodingList", method = RequestMethod.GET)
    @Security(session = true)
    public EncodingDataMapper encodingList() throws APIException {
        return tableHttpClient.encodingDataType();
    }


    @ApiOperation(value = "第六步——获取Encoding2")
    @RequestMapping(value = "/encoding2List", method = RequestMethod.GET)
    @Security(session = true)
    public HashMap encoding2List() throws APIException {
        return tableHttpClient.encodingDataTypeCount();
    }


    @ApiOperation(value = "第七步——完成创建")
    @RequestMapping(value = "/createModeling", method = RequestMethod.POST)
    @Security(session = true)
    public Map<String, Object> createModeling(@RequestBody ModelingMapper body) throws Exception {
        List<TableNameRelationMapper> relations = body.getRelations();
        if (relations != null && !relations.isEmpty()){
            body.setRelations(null);
            String json = JSON.toJSONString(body);
            for (TableNameRelationMapper mapper : relations){//循环每个表，全局替换
                if (mapper.getTableName()!=null && mapper.getVirtualTableName()!=null){
                    json = TableNameTransposition.replaceAll(json,mapper.getVirtualTableName(),mapper.getTableName());
                }
            }
            log.info("请求模型消息体替换后:{}",json);
            body = JSON.parseObject(json,ModelingMapper.class);
        }
        ModelsMapper models = body.getModels();
        CubeDescMapper cube = body.getCube();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String cubeName = cube.cubeDescData.getName();
        String modelName = "";
        Date date = new Date();

        ModelsMapper modelMap = new ModelsMapper();
        CubeDescNewMapper cubeMap = new CubeDescNewMapper();
        ModelsDescDataMapper oldModel = null;
        CubeDescDataMapper oldCube = saveVerification(cube, models);

        //处理逻辑如下：
        //1、反推models里选择的维度字段
        //2、cube的measures放入models的measure
        ArrayList<MeasureMapper> countMappers = modelsLogic(models, cube);

        //处理逻辑如下：
        //1、将用户创建的模型里选择过的表全部拉到DataSource里
        hiveCreate(userVO, body.cubeDatalaketableNew);

        //设置完整的过滤SQL
        models.getModelDescData().setFilter_condition(makeFilterSql(body.getFilterCondidion()));

        //构建需要保存的数据
        OlapCube olapCube = olapCubeService.saveCube(cube, date, userVO, body.getDimensionLength(), body.getDimensionFiledLength(), body.getMeasureFiledLength(),body.getGraphData());
        List<OlapCubeTable> cubeTablesList = olapCubeTableService.saveCubeTable(models, cube, olapCube.getCubeId(), body.cubeDatalaketableNew);
        List<OlapCubeTableRelation> olapcubeList = olapCubeTableRelationService.saveCubeTableRelation(cube, models, olapCube.getCubeId(), cubeTablesList);

        synchronized (lock) {
            //处理逻辑如下：
            //1、通过uuid去判断是否为新增或编辑,然后进行相应操作.
            if (StringUtils.isBlank(body.getCube().getCubeDescData().getUuid())) {
                modelName = String.valueOf(ConcurrentSequence.getInstance().getSequence());
                models.modelDescData.setName(modelName);
                models.modelDescData.setVersion(null);
                models.setProject(userVO.getUserId());
                modelMap = modelHttpClient.create(models);
                try {
                    cube.cubeDescData.setModel_name(modelName);
                    cube.cubeDescData.setVersion(null);
                    cube.cubeDescData.setStorage_type("2");
                    cube.setCubeName(cubeName);
                    cube.setProject(userVO.getUserId());
                    Thread.sleep(500);
                    cubeMap = cubeHttpClient.create(cube);
                } catch (Exception ex) {
                    modelHttpClient.delete(modelName);
                    throw ex;
                }
            } else {
                modelName = models.getModelDescData().getName();
                oldModel = modelHttpClient.entity(models.getModelDescData().getName());
                models.getModelDescData().setVersion(oldModel.getVersion());
                models.getModelDescData().setLast_modified(oldModel.getLast_modified());
                modelMap = modelHttpClient.update(models);
                try {
                    cube.project = userVO.getUserId();
                    cube.cubeName = cube.cubeDescData.getName();
                    cube.cubeDescData.setModel_name(models.modelDescData.getName());
                    cube.cubeDescData.setStorage_type("2");
                    Thread.sleep(500);
                    cubeMap = cubeHttpClient.update(cube);
                } catch (Exception ex) {
                    restoreModel(modelMap, oldModel);
                    throw ex;
                }
            }
        }

        try {
            //保存所有数据
            olapCubeService.saveTable(olapCube, cubeTablesList, olapcubeList, body.getCubeDatalaketableNew(), cube, models.getModelDescData(), body.getTimingreFresh(),
                    date, userVO, body.getFilterCondidion(), countMappers,relations);
        } catch (Exception ex) {
            if (StringUtils.isBlank(body.getCube().getCubeDescData().getUuid())) {
                modelHttpClient.delete(modelName);
                cubeHttpClient.delete(cubeName);
            } else {
                restoreModel(modelMap, oldModel);
                restoreCube(cubeMap, oldCube);
            }
            throw ex;
        }

        paramMap.put("ModesList", modelMap);
        paramMap.put("CubeList", cubeMap);
        return paramMap;
    }

    private void restoreCube(CubeDescNewMapper cubeMap, CubeDescDataMapper oldCube) throws APIException {
        CubeDescDataMapper newCube = cubeHttpClient.desc(cubeMap.cubeName);
        CubeDescMapper cubeDescMapper = new CubeDescMapper();
        cubeDescMapper.setCubeName(oldCube.getName());
        cubeDescMapper.setProject(cubeMap.project);
        cubeDescMapper.setCubeDescData(oldCube);
        cubeDescMapper.setUuid(newCube.getUuid());
        oldCube.setLast_modified(newCube.getLast_modified());
        cubeHttpClient.update(cubeDescMapper);
    }

    private void restoreModel(ModelsMapper modelMap, ModelsDescDataMapper oldModel) throws APIException {
        modelMap.modelDescData.setPartition_desc(oldModel.getPartition_desc());
        modelMap.modelDescData.setLookups(oldModel.getLookups());
        modelMap.modelDescData.setFact_table(oldModel.getFact_table());
        modelMap.modelDescData.setMetrics(oldModel.getMetrics());
        modelMap.modelDescData.setDimensions(oldModel.getDimensions());
        modelMap.modelDescData.setFilter_condition(oldModel.getFilter_condition());
        modelHttpClient.update(modelMap);
    }

    private String makeFilterSql(List<OlapFilterCondidion> filterCondidion) {
        String filter = "";
        if (filterCondidion != null && filterCondidion.size() > 0) {
            ArrayList<String> conditions = new ArrayList<String>();
            for (OlapFilterCondidion condidion : filterCondidion) {
                conditions.add(MessageFormat.format("{0}{1}''{2}''", condidion.getField(), condidion.getPattern(), condidion.getParameter()));
            }
            filter = String.join(" AND ", conditions);
        }
        return filter;
    }


    //处理逻辑如下：
    //1、反推models里选择的维度、度量字段
    //2、cube的measures放入models的measure
    public ArrayList<MeasureMapper> modelsLogic(ModelsMapper models, CubeDescMapper cube) {
        ArrayList<DimensionsMapper> dimensionsList = new ArrayList<>();
        for (DimensionMapper dimensionMapper : cube.cubeDescData.getDimensions()) {
            DimensionsMapper dimensionsEntity = dimensionsList.stream().filter(p -> p.getTable().equalsIgnoreCase(dimensionMapper.getTable())).
                    findFirst().orElse(null);
            if (dimensionsEntity == null) {
                dimensionsEntity = new DimensionsMapper();
                dimensionsEntity.setTable(dimensionMapper.getTable());
                dimensionsEntity.setColumns(new ArrayList<String>());
                dimensionsList.add(dimensionsEntity);
            }
            if (dimensionMapper.getDerived() != null && dimensionMapper.getDerived().size() > 0) {
                dimensionsEntity.getColumns().addAll(dimensionMapper.getDerived());
            } else {
                dimensionsEntity.getColumns().add(dimensionMapper.getColumn());
            }
        }

        String dictTable = models.modelDescData.getFact_table().substring(models.modelDescData.getFact_table().indexOf(".") + 1);
        ArrayList<MeasureMapper> countMappers = new ArrayList<>();
        //循环拿到cube的measures放入models的measure
        ArrayList<String> metricsList = new ArrayList();
        cube.getCubeDescData().getMeasures().forEach(p -> {
            String measureTable = p.function.parameter.getValue().substring(0, p.function.parameter.getValue().indexOf("."));
            String measureColumn = p.function.parameter.getValue().substring(p.function.parameter.getValue().indexOf(".") + 1);
            if (dictTable.equalsIgnoreCase(measureTable)) {
                if (!metricsList.contains(p.function.parameter.getValue())) {
                    metricsList.add(p.function.parameter.getValue());
                }
            } else {
                Optional<DimensionsMapper> oDimensions = dimensionsList.stream().filter(c -> c.getTable().equals(measureTable)).findFirst();
                if (oDimensions.isPresent()) {
                    if (!oDimensions.get().getColumns().contains(measureColumn)) {
                        oDimensions.get().getColumns().add(measureColumn);
                    }
                } else {
                    DimensionsMapper dimensionsEntity = new DimensionsMapper();
                    dimensionsEntity.setTable(measureTable);
                    dimensionsEntity.setColumns(new ArrayList<String>());
                    dimensionsEntity.getColumns().add(measureColumn);
                    dimensionsList.add(dimensionsEntity);
                }
            }

            if (p.function.getExpression().equals("AVG")) {
                p.function.setExpression("SUM");
                p.function.setRequestExpression("AVG");
            } else if (p.function.getExpression().equals("COUNT")) {
                p.function.setRequestExpression(p.function.getExpression());
                for (HbaseColumnFamilyMapper familyMapper : cube.cubeDescData.hbase_mapping.column_family) {
                    if (familyMapper.columns.get(0).measure_refs.contains(p.name)) {
                        familyMapper.columns.get(0).measure_refs.remove(p.name);
                        break;
                    }
                }
                countMappers.add(p);
            } else if (p.function.getExpression().equals("COUNT_DISTINCT")) {
                String disCountColumn = p.function.getParameter().getValue();
                if (cube.getCubeDescData().getDictionaries() == null) {
                    cube.getCubeDescData().setDictionaries(new ArrayList<CubeDictionaryMapper>());
                }
                if (cube.getCubeDescData().getDictionaries().stream().filter(q -> q.getColumn().equals(disCountColumn)).count() == 0) {
                    cube.getCubeDescData().getDictionaries().add(new CubeDictionaryMapper(disCountColumn, CubeDictionaryMapper.GlobalDictionaryBuilder));
                }
                p.function.setRequestExpression(p.function.getExpression());
            } else {
                p.function.setRequestExpression(p.function.getExpression());
            }
        });

        //移除COUNT
        Integer countMapperSize = countMappers.size();
        for (Integer i = 0; i < countMapperSize; i++) {
            cube.getCubeDescData().getMeasures().remove(countMappers.get(i));
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

        models.getModelDescData().setDimensions(dimensionsList);
        models.getModelDescData().setMetrics(metricsList);
        return countMappers;
    }

    //处理逻辑如下：
    //1、将用户创建的模型里选择过的表全部拉到DataSource里
    public void hiveCreate(OaUserVO userVO, List<CubeDatalaketableNewMapper> cubeDatalaketableNew) throws APIException {
        ProjectDescDataMapper project = projectHttpClient.get(userVO.getUserId());
        //1、检测当前项目是否有该表,如果没有则加入进去
        List<String> tableNameList = new ArrayList<String>();
        if (cubeDatalaketableNew != null) {
            for (CubeDatalaketableNewMapper datalaketableNew : cubeDatalaketableNew) {
                for (OlapDatalaketable table : datalaketableNew.getTableList()) {
//                    String name = datalaketableNew.getOrgName() + "." + table.getTable_name();
                    String name = "olap" + "." + table.getTable_name();//TODO 这里前缀olap是固定的hive数据库名，需加到配置文件里
                    tableNameList.add(name);
                }
            }
        }

        //1、判断是否有该用户的project,如果没有则创建
        if (project == null) {
            ProjectDescDataMapper projectDesc = new ProjectDescDataMapper();
            projectDesc.setName(userVO.getUserId());
            projectDesc.setDescription(userVO.getUserName());
            OverrideKylinPropertiesMapper override = new OverrideKylinPropertiesMapper();
            override.setAuthor(userVO.getUserName());
            projectDesc.setOverride_kylin_properties(override);
            projectHttpClient.create(projectDesc);
            hiveHttpClient.create(tableNameList, userVO.getUserId());
        } else {
            String[] strArrayTrue = project.getTables().toArray(new String[0]);
            String[] tableName = tableNameList.stream().toArray(String[]::new);
            //对比是否存在,存在的就不要加进去了tables,拿差集
            String[] tableNameArray = minus(strArrayTrue, tableName);
            if (tableNameArray.length != 0) {
                List<String> list = Arrays.asList(tableNameArray);
                hiveHttpClient.create(list, userVO.getUserId());
            }
        }
    }

    //处理逻辑如下：
    //1、保存前各个数据模块校验
    //2、默认给度量加上_COUNT_ (麒麟的这个类型是必带的,所有决定后台写死)
    public CubeDescDataMapper saveVerification(CubeDescMapper cube, ModelsMapper models) throws APIException {
        CubeDescDataMapper cubeDescDataMapper = cubeHttpClient.desc(cube.cubeDescData.getName());
        if (StringUtils.isBlank(cube.getCubeDescData().getUuid()) && cubeDescDataMapper != null) {
            throw new APIException(400, "该立方体名称已存在！");
        }
        //编辑验证
        if (StringUtils.isNotBlank(cube.getCubeDescData().getUuid())) {
            if (cubeDescDataMapper == null || !cube.getCubeDescData().getUuid().equals(cubeDescDataMapper.getUuid())) {
                throw new APIException(400, "不能修改立方体名称！");
            }
            List<CubeHbaseMapper> cubeHbaseMappers = cubeHttpClient.hbase(cube.getCubeDescData().getName());
            if (cubeHbaseMappers.size() > 0) {
                throw new APIException(400, "请先删除已经构建的块！");
            }
            cube.getCubeDescData().setVersion(cubeDescDataMapper.getVersion());
            cube.getCubeDescData().setLast_modified(cubeDescDataMapper.getLast_modified());
        }

        for (LookupsMapper lookupsMapper : models.modelDescData.getLookups()) {
            for (String fk : lookupsMapper.join.getForeign_key()) {
                if (cube.getCubeDescData().getDimensions().stream().filter(p -> p.getId().equalsIgnoreCase(fk) && p.getDerived() == null).count() == 0) {
                    throw new APIException(400, "外键列【" + fk + "】必须在维度中存在且为非衍生模式！");
                }
            }
            for (String pk : lookupsMapper.join.getPrimary_key()) {
                if (cube.getCubeDescData().getDimensions().stream().filter(p -> p.getId().equalsIgnoreCase(pk)).count() == 0) {
                    throw new APIException(400, "主键列【" + pk + "】必须在维度中存在！");
                }
            }
        }

        Long rowKeyCount = cube.getCubeDescData().getDimensions().stream().filter(p -> p.getDerived() == null).count();
        if (rowKeyCount != cube.getCubeDescData().getRowkey().getRowkey_columns().size()) {
            throw new APIException(400, "rowkey个数不等于【" + rowKeyCount + "】！");
        }

        for (RowkeyColumnMapper rowKey : cube.getCubeDescData().getRowkey().getRowkey_columns()) {
            if (rowKey.getColumn() == null || rowKey.getColumn().equals("") || rowKey.getColumn().indexOf(".") < 0 || rowKey.getEncoding() == null
                    || rowKey.getEncoding().equals("")) {
                throw new APIException(400, "rowKey数据格式有问题！");
            }

            boolean isSetting = false;
            for (AggregationGroupMapper groupMapper : cube.getCubeDescData().getAggregation_groups()) {
                if (groupMapper.includes.contains(rowKey.column) == true) {
                    isSetting = true;
                    break;
                }
            }

            if (!isSetting) {
                throw new APIException(400, "维度【" + rowKey.column + "】未包含在维度中！");
            }
        }

        if (cube.cubeDescData.measures.size() == 0) {
            throw new APIException(400, "度量不可为空！");
        } else if (cube.getCubeDescData().getDimensions().size() == 0) {
            throw new APIException(400, "维度不可为空！");
        } else {
            String[] companyType = {"SUM", "AVG"};
            String[] supportType = {"smallint", "int4", "double", "smallint", "int4", "double", "tinyint", "numeric", "long8", "integer", "real", "float", "decimal(19,4)", "bigint"};
            for (MeasureMapper measure : cube.cubeDescData.getMeasures()) {
                if (Arrays.asList(companyType).contains(measure.getFunction().getExpression()) == true && Arrays.asList(supportType).contains(measure.getFunction().getReturntype()) == false) {
                    throw new APIException(400, "度量计算方式与字段类型不匹配！");
                }

                if (measure.getFunction().getExpression().equals("COUNT_DISTINCT")) {
                    RowkeyColumnMapper mapper = cube.getCubeDescData().getRowkey().getRowkey_columns().stream().filter(p -> p.column.equals(measure.getFunction()
                            .getParameter().getValue())).findFirst().orElse(null);
                    if (mapper == null) {
                        throw new APIException(400, "计算方式为【COUNT_DISTINCT】的度量计算列在设置维度中必须存在且为普通模式！");
                    }
                    if (mapper.getEncoding().equals("dict") || Integer.parseInt(mapper.getLengths()) <= 0) {
                        throw new APIException(400, "计算方式为【COUNT_DISTINCT】的度量计算列在对应rowkey中不允许为【dict】编码方式且需指定长度！");
                    }
                }
            }
            for (DimensionMapper dimensionMapper : cube.getCubeDescData().getDimensions()) {
                if (StringUtils.isBlank(dimensionMapper.column_type)) {
                    throw new APIException(400, "维度列类型不能为空！");
                }
            }
        }

        return cubeDescDataMapper;
    }


    @ApiOperation(value = "立方体:查看")
    @RequestMapping(value = "/desc", method = RequestMethod.GET)
    @Security(session = true)
    public Map<String, Object> desc(String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        CubeDescDataMapper cube = cubeHttpClient.desc(cubeName);
        ModelsDescDataMapper model = modelHttpClient.entity(cube.getModel_name());
        List<OlapDatalaketable> table = olapDatalaketableService.getListByCubeName(cubeName);
        List<TableNameRelationMapper> relations = TableNameTransposition.extract(table);
        OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
        if (olapCube == null) {
            throw new APIException("立方体数据缺失！");
        }
        //事实表
        String factTable = model.getFact_table().substring(model.getFact_table().indexOf(".") + 1);
        //第一步
        //整理用户第一步点击选择的表并保存为前端需要的格式
        //data分组
        Map<String, List<OlapDatalaketable>> data = table.stream().collect(Collectors.groupingBy(OlapDatalaketable::getOrgName));
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


        //第二步
        //(Moldes)需要将前端在这保存的第二步的坐标返回出去
        //根据当前用户查找存储在olapCube表里的数据并组装坐标、主外键字段和字段类型
        List<OlapCubeTable> cubetable = olapCubeTableService.findByTable(cubeName);
        for (LookupsMapper l : model.lookups) {
            Optional<OlapCubeTable> cubeEntity = cubetable.stream().filter(p -> p.getTableAlias().equalsIgnoreCase(l.getAlias())).findFirst();
            //查出所有列 然后保存在数组里
            ArrayList<OlapCubeTableColumn> column = olapCubeTableColumnService.findByCubeTableId(cubeEntity.get().getCubeTableId());
            List<String> pkList = new ArrayList<>();
            List<String> fkList = new ArrayList<>();
            String[] joinPk = l.join.getPrimary_key();
            String[] joinFk = l.join.getPrimary_key();
            for (int i = 0; i < joinPk.length; i++) {
                String joinPkSub = joinPk[i].substring(joinPk[i].indexOf(".") + 1);
                Optional<OlapCubeTableColumn> EntityPk = column.stream().filter(p -> p.getColumnName().equalsIgnoreCase(joinPkSub)).findFirst();
                pkList.add(EntityPk.get().getColumnType());

                String joinFkSub = joinFk[i].substring(joinFk[i].indexOf(".") + 1);
                Optional<OlapCubeTableColumn> EntityFk = column.stream().filter(p -> p.getColumnName().equalsIgnoreCase(joinFkSub)).findFirst();
                fkList.add(EntityFk.get().getColumnType());
            }
            l.join.setPk_type(pkList);
            l.join.setFk_type(fkList);

            l.setSAxis(cubeEntity.get().getSAxis());
            l.setYAxis(cubeEntity.get().getYAxis());
            l.setJoinSAxis(cubeEntity.get().getJoinSAxis());
            l.setJoinYAxis(cubeEntity.get().getJoinYAxis());
            l.setJoinAlias(cubeEntity.get().getJoinAlias());
            l.setJoinId(cubeEntity.get().getJoinId());
            l.setJoinTable(cubeEntity.get().getJoinTable());
            l.setId(cubeEntity.get().getTableId());
        }
        Optional<OlapCubeTable> fact = cubetable.stream().filter(p -> p.getTableName().equals(factTable)).findFirst();
        model.setSAxis(fact.get().getSAxis());
        model.setYAxis(fact.get().getYAxis());
        model.setJoinSAxis(fact.get().getJoinSAxis());
        model.setJoinYAxis(fact.get().getJoinYAxis());


        //第四步
        //1、移除后端自动添加的_COUNT_   2、将原AVG转换成SUM的再次转换回AVG
        ArrayList<OlapCubeTableColumn> column = olapCubeTableColumnService.findByColumn(cubeName);
        ArrayList<MeasureMapper> measuresList = cube.getMeasures();
        MeasureMapper me = null;
        for (MeasureMapper measure : measuresList) {
            //1、移除后端自动添加的_COUNT_
            if (measure.getName().equals("_COUNT_")) {
                me = measure;
            }
            Optional<OlapCubeTableColumn> cubeColumnEntity = column.stream()
                    .filter(p -> p.getColumnAlias().equals(measure.getName()) && p.getExpressionType() != null).findFirst();
            if (cubeColumnEntity.isPresent()) {
                measure.function.setExpression(cubeColumnEntity.get().getExpressionType());
            }
        }
        if (me != null) {
            cube.measures.remove(me);
        }

        List<OlapCubeTableColumn> countColumns = column.stream().filter(p -> p.getExpressionType() != null && p.getExpressionType().equalsIgnoreCase("COUNT")).
                collect(Collectors.toList());
        for (OlapCubeTableColumn countColumn : countColumns) {
            MeasureMapper mapper = new MeasureMapper();
            mapper.setName(countColumn.getName());
            mapper.setFunction(new FunctionMapper());
            mapper.getFunction().setRequestExpression(countColumn.getExpressionType());
            mapper.getFunction().setExpression(countColumn.getExpressionType());
            mapper.getFunction().setParameter(new ParameterMapper());
            mapper.getFunction().setReturntype("bigint");
            OlapCubeTable countTable = cubetable.stream().filter(p -> p.getCubeTableId().equals(countColumn.getTableId())).findFirst().orElse(null);
            mapper.getFunction().getParameter().setValue(countTable.getTableAlias() + "." + countColumn.getColumnName());
            mapper.getFunction().getParameter().setType("column");
            cube.hbase_mapping.column_family.get(0).getColumns().get(0).measure_refs.add(countColumn.getName());
            measuresList.add(mapper);
        }

        //处理cube里dimensions的数据
        for (DimensionMapper dimension : cube.getDimensions()) {
            //拿到列,这里的衍生模式是为数组(暂时没发现他为什么要用数组.),我是只取第一个(也只有一个)
            String columu = (dimension.getColumn() != null) == true ? dimension.getColumn() : dimension.getDerived().get(0);
            //查出表信息
            Optional<OlapCubeTable> cubeEntity = cubetable.stream().filter(p -> p.getTableAlias().equalsIgnoreCase(dimension.getTable())).findFirst();
            //列信息
            Optional<OlapCubeTableColumn> columnEntity = olapCubeTableColumnService.findByCubeTableId(cubeEntity.get().getCubeTableId())
                    .stream().filter(p -> p.getColumnAlias().equalsIgnoreCase(columu)).findFirst();
            //赋值列信息
            if (columnEntity.isPresent()) {
                dimension.setColumn_type(columnEntity.get().getColumnType());
                dimension.setId(columnEntity.get().getLibraryTable());
            }
        }

        //处理刷新
        OlapTimingrefresh timingrefresh = olapTimingrefreshService.findTableInfo(cubeName);
        //处理过滤
        OlapFilter olapFilter = olapFilterService.findTableInfo(cubeName);
        List<OlapFilterCondidion> olapFilterCondidions = new ArrayList<>();
        if (olapFilter != null) {
            olapFilterCondidions = olapFilterCondidionService.findByFilterId(olapFilter.getId());
        }


        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ModesList", model);
        paramMap.put("CubeList", cube);
        paramMap.put("TableList", datalaketableNewList);
        paramMap.put("dimensionLength", olapCube.getDimensionLength());
        paramMap.put("dimensionFiledLength", olapCube.getDimensionFiledLength());
        paramMap.put("measureFiledLength", olapCube.getMeasureFiledLength());
        paramMap.put("timingreFresh", timingrefresh);
        paramMap.put("filterCondidion", olapFilterCondidions);
        paramMap.put("graphData", olapCube.getGraphData());
        if (relations != null && !relations.isEmpty()){
            String json = JSON.toJSONString(paramMap);
            for (TableNameRelationMapper mapper : relations){//循环每个表，全局替换
                if (mapper.getVirtualTableName()!= null && mapper.getTableName()!=null) {
                    json = TableNameTransposition.replaceAll(json, mapper.getTableName(), mapper.getVirtualTableName());
                }
            }
            paramMap = JSON.parseObject(json);
        }
        return paramMap;
    }


    @ApiOperation(value = "立方体:构建")
    @RequestMapping(value = "/build", method = RequestMethod.PUT)
    @Security(session = true)
    public void build(String cubeName, Long start, Long end,@RequestBody OlapTimingrefresh timingrefresh) throws Exception {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        timingrefresh.setUpdateId(Long.parseLong(userVO.getUserId()));
        timingrefresh.setUpdateName(userVO.getUserAccount());
        timingrefresh.setUpdateTime(new Date());
        olapCubeBuildService.preBuild(cubeName,start,end,1);
    }


    @ApiOperation(value = "立方体:刷新")
    @RequestMapping(value = "/refresh", method = RequestMethod.PUT)
    @Security(session = true)
    public void refresh(String cubeName, Long startTime, Long endTime) throws Exception {
        cubeHttpClient.refresh(cubeName, startTime, endTime);
    }


    @ApiOperation(value = "立方体:合并")
    @RequestMapping(value = "/merge", method = RequestMethod.PUT)
    @Security(session = true)
    public void merge(String cubeName, Long start, Long end) throws Exception {
        cubeHttpClient.build(cubeName, start, end);
    }


    @ApiOperation(value = "立方体:禁用")
    @RequestMapping(value = "/disable", method = RequestMethod.PUT)
    @Security(session = true)
    public void disable(String cubeName) throws APIException {
        cubeHttpClient.disable(cubeName);
        //修改olap分析的cube表状态值为不可用
        OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
        olapCube.setIsNew(false);
        olapCube.setFlags(0);
        olapCubeService.doSave(olapCube);
    }


    @ApiOperation(value = "立方体:启用")
    @RequestMapping(value = "/enable", method = RequestMethod.PUT)
    @Security(session = true)
    public void enable(String cubeName) throws APIException {
        cubeHttpClient.enable(cubeName);
        //修改olap分析的cube表状态值为不可用
        OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
        olapCube.setIsNew(false);
        olapCube.setFlags(1);
        olapCubeService.doSave(olapCube);
    }

    @ApiOperation(value = "立方体:复制")
    @RequestMapping(value = "/clone", method = RequestMethod.PUT)
    @Security(session = true)
    public void clone(String cubeName, String cubeNameClone) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        Date date = new Date();

        CubeDescDataMapper cube = cubeHttpClient.desc(cubeNameClone);
        if (cube != null) {
            throw new APIException(400, "已存在该立方体名称！");
        }
        //拿到OLAP_CUBE表数据
        OlapCube olapCubeEntity = olapCubeService.findTableInfo(cubeName);
        if (olapCubeEntity == null) {
            throw new APIException("立方体数据缺失！");
        }
        //进行克隆
        cubeHttpClient.clone(cubeName, cubeNameClone, userVO.getUserId());
        //拿到OLAP_CUBE_TABLE表数据
        ArrayList<OlapCubeTable> cubeTablesList = olapCubeTableService.findByTable(cubeName);
        //拿到列数据
        ArrayList<OlapCubeTableColumn> findByColumn = olapCubeTableColumnService.findByColumn(cubeName);
        //拿到过滤条件
        OlapFilter findTableInfo = olapFilterService.findTableInfo(cubeName);
        //拿到OLAP_CUBE_TABLE_RELATION表数据
        ArrayList<OlapCubeTableRelation> olapcubeList = olapCubeTableRelationService.findByCubeName(cubeName);
        //拿到定时任务
        OlapTimingrefresh olapTimingrefresh = olapTimingrefreshService.findTableInfo(cubeName);
        //拿到第一步用户选择表
        List<OlapDatalaketable> datalaketables = olapDatalaketableService.getListByCubeName(cubeName);

        //放到事物里进行保存
        olapCubeService.saveTableClone(olapCubeEntity, cubeTablesList, findByColumn, findTableInfo, olapcubeList, olapTimingrefresh, datalaketables, userVO, cubeNameClone);
    }

    @ApiOperation(value = "立方体:删除")
    @RequestMapping(value = "/deleteCube", method = RequestMethod.DELETE)
    @Security(session = true)
    public void deleteCube(String cubeName) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        //判断该立方体是否还存在job数据,如果存在则不能删除.
        List<JobsMapper> jobList = Arrays.asList(jobsHttpClient.list(100000L, 0L, userVO.getUserId(), cubeName));
        for (JobsMapper jobs : jobList) {
            if (jobs.getName().equals(cubeName)) {
                throw new APIException(400, "请先删除构建数据！");
            }
        }
        //删除立方体
        cubeHttpClient.delete(cubeName);
        //删除与该立方体相关数据
        OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
        if (olapCube != null) {
            olapCubeService.deleteCubeName(cubeName);
            olapCubeTableService.deleteCubeId(olapCube.getId());
            olapCubeTableColumnService.deleteCubeId(olapCube.getId());
            olapCubeTableRelationService.deleteCubeId(olapCube.getId());
            //删除过滤表
            OlapFilter olapFilter = olapFilterService.findTableInfo(cubeName);
            if (olapFilter != null) {
                olapFilterCondidionService.deleteFilterId(olapFilter.getId());
                olapFilterService.deleteCubeName(cubeName);
            }
            olapTimingrefreshService.deleteCubeName(cubeName);
            olapDatalaketableService.deleteCubeName(cubeName);
        }
    }


    @ApiOperation(value = "构建列表:停止")
    @RequestMapping(value = "/cancelJob", method = RequestMethod.PUT)
    @Security(session = true)
    public void cancelJob(String jobsId) throws APIException {
        jobsHttpClient.cancel(jobsId);
        jobsHttpClient.cancel(jobsId);
    }

    @ApiOperation(value = "构建列表:暂停")
    @RequestMapping(value = "/pauseJob", method = RequestMethod.PUT)
    @Security(session = true)
    public void pauseJob(String cubeName, String jobsId) throws APIException {
        jobsHttpClient.pause(jobsId);
    }

    @ApiOperation(value = "构建列表:运行")
    @RequestMapping(value = "/resumeJob", method = RequestMethod.PUT)
    @Security(session = true)
    public void resumeJob(String jobsId) throws APIException {
        jobsHttpClient.resume(jobsId);
    }


    @ApiOperation(value = "构建列表:删除JOB")
    @RequestMapping(value = "/deleteJob", method = RequestMethod.DELETE)
    @Security(session = true)
    public void deleteJob(String jobsId) throws APIException {
//        jobsAction.cancel(jobsId);
        jobsHttpClient.delete(jobsId);
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
        String json = "[{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"orgId\":\"2\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"f86af73f-c96a-4eb8-9de7-3cca85aae998\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"source_type\":0,\"orgId\":\"2\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"84bbe31a-384f-45c8-8d5d-c1d311095b5a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"orgId\":\"2\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"050f3ab8-4c36-49d2-a29d-30e246e7c662\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"orgId\":\"2\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"KYLIN\"},{\"resourceId\":\"19e90d39-6cc3-4d20-b08d-1d8771f1ea1d\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"orgId\":\"2\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"KYLIN\"},{\"resourceId\":\"c65b5c62-6d14-4456-bc37-2f7bff28fcca\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CATEGORY_GROUPINGS\",\"source_type\":0,\"orgId\":\"1\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"DEFAULT\"},{\"resourceId\":\"3fa28668-b02c-49cf-aa53-008e06c49cb7\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_SALES\",\"source_type\":0,\"orgId\":\"1\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"DEFAULT\"},{\"resourceId\":\"2abeafff-b218-46de-b06c-abd10365d273\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_ACCOUNT\",\"source_type\":0,\"orgId\":\"1\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"DEFAULT\"},{\"resourceId\":\"a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_CAL_DT\",\"source_type\":0,\"orgId\":\"1\",\"table_type\":\"MANAGED_TABLE\",\"database\":\"DEFAULT\"},{\"resourceId\":\"34ac809f-7728-423b-81af-29f931c2da39\",\"last_modified\":0,\"orgId\":\"1\",\"version\":\"2.3.1.0\",\"resourceTableName\":\"KYLIN_COUNTRY\",\"database\":\"DEFAULT\"}]";

        List<DemoTable> list = JSONObject.parseArray(json, DemoTable.class);
        list = list.stream().filter(p -> p.getOrgId().equals(orgId)).collect(Collectors.toList());
        json = JSON.toJSONString(list);
        return json;
    }

    @ApiOperation(value = "查看列")
    @RequestMapping(value = "/selectColumn", method = RequestMethod.GET)
    @Security(session = false)
    public List<String> selectColumn(String[] resourceIds) {

//        String json = "[{\"resourceId\":\"84bbe31a-384f-45c8-8d5d-c1d311095b5a\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_ACCOUNT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"ACCOUNT_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"ACCOUNT_BUYER_LEVEL\",\"datatype\":\"integer\"},{\"id\":\"3\",\"name\":\"ACCOUNT_SELLER_LEVEL\",\"datatype\":\"integer\"},{\"id\":\"4\",\"name\":\"ACCOUNT_COUNTRY\",\"datatype\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"ACCOUNT_CONTACT\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}},{\"resourceId\":\"050f3ab8-4c36-49d2-a29d-30e246e7c662\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CAL_DT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"CAL_DT\",\"datatype\":\"date\",\"comment\":\"Date, PK\"},{\"id\":\"2\",\"name\":\"YEAR_BEG_DT\",\"datatype\":\"date\",\"comment\":\"YEAR Begin Date\"},{\"id\":\"3\",\"name\":\"QTR_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Quarter Begin Date\"},{\"id\":\"4\",\"name\":\"MONTH_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Month Begin Date\"},{\"id\":\"5\",\"name\":\"WEEK_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Week Begin Date\"},{\"id\":\"6\",\"name\":\"AGE_FOR_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"7\",\"name\":\"AGE_FOR_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"8\",\"name\":\"AGE_FOR_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"9\",\"name\":\"AGE_FOR_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"10\",\"name\":\"AGE_FOR_DT_ID\",\"datatype\":\"smallint\"},{\"id\":\"11\",\"name\":\"AGE_FOR_RTL_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"12\",\"name\":\"AGE_FOR_RTL_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"13\",\"name\":\"AGE_FOR_RTL_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"14\",\"name\":\"AGE_FOR_RTL_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"15\",\"name\":\"AGE_FOR_CS_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"16\",\"name\":\"DAY_OF_CAL_ID\",\"datatype\":\"integer\"},{\"id\":\"17\",\"name\":\"DAY_OF_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"18\",\"name\":\"DAY_OF_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"19\",\"name\":\"DAY_OF_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"20\",\"name\":\"DAY_OF_WEEK_ID\",\"datatype\":\"integer\"},{\"id\":\"21\",\"name\":\"WEEK_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"22\",\"name\":\"WEEK_OF_CAL_ID\",\"datatype\":\"integer\"},{\"id\":\"23\",\"name\":\"MONTH_OF_QTR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"24\",\"name\":\"MONTH_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"25\",\"name\":\"MONTH_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"26\",\"name\":\"QTR_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"27\",\"name\":\"QTR_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"28\",\"name\":\"YEAR_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"29\",\"name\":\"YEAR_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"30\",\"name\":\"QTR_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"MONTH_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"32\",\"name\":\"WEEK_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"33\",\"name\":\"CAL_DT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"34\",\"name\":\"CAL_DT_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"35\",\"name\":\"CAL_DT_SHORT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"36\",\"name\":\"YTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"37\",\"name\":\"QTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"38\",\"name\":\"MTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"39\",\"name\":\"WTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"40\",\"name\":\"SEASON_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"41\",\"name\":\"DAY_IN_YEAR_COUNT\",\"datatype\":\"smallint\"},{\"id\":\"42\",\"name\":\"DAY_IN_QTR_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"43\",\"name\":\"DAY_IN_MONTH_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"44\",\"name\":\"DAY_IN_WEEK_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"45\",\"name\":\"RTL_YEAR_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"46\",\"name\":\"RTL_QTR_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"47\",\"name\":\"RTL_MONTH_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"48\",\"name\":\"RTL_WEEK_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"49\",\"name\":\"CS_WEEK_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"50\",\"name\":\"CAL_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"51\",\"name\":\"DAY_OF_WEEK\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"52\",\"name\":\"MONTH_ID\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"53\",\"name\":\"PRD_DESC\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"54\",\"name\":\"PRD_FLAG\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"55\",\"name\":\"PRD_ID\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"56\",\"name\":\"PRD_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"57\",\"name\":\"QTR_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"58\",\"name\":\"QTR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"59\",\"name\":\"QTR_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"60\",\"name\":\"RETAIL_WEEK\",\"datatype\":\"varchar(256)\"},{\"id\":\"61\",\"name\":\"RETAIL_YEAR\",\"datatype\":\"varchar(256)\"},{\"id\":\"62\",\"name\":\"RETAIL_START_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"63\",\"name\":\"RETAIL_WK_END_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"64\",\"name\":\"WEEK_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"65\",\"name\":\"WEEK_NUM_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"66\",\"name\":\"WEEK_BEG_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"67\",\"name\":\"WEEK_END_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"68\",\"name\":\"WEEK_IN_YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"69\",\"name\":\"WEEK_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"70\",\"name\":\"WEEK_BEG_END_DESC_MDY\",\"datatype\":\"varchar(256)\"},{\"id\":\"71\",\"name\":\"WEEK_BEG_END_DESC_MD\",\"datatype\":\"varchar(256)\"},{\"id\":\"72\",\"name\":\"YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"73\",\"name\":\"YEAR_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"74\",\"name\":\"CAL_DT_MNS_1YEAR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"75\",\"name\":\"CAL_DT_MNS_2YEAR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"76\",\"name\":\"CAL_DT_MNS_1QTR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"77\",\"name\":\"CAL_DT_MNS_2QTR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"78\",\"name\":\"CAL_DT_MNS_1MONTH_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"79\",\"name\":\"CAL_DT_MNS_2MONTH_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"80\",\"name\":\"CAL_DT_MNS_1WEEK_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"81\",\"name\":\"CAL_DT_MNS_2WEEK_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"82\",\"name\":\"CURR_CAL_DT_MNS_1YEAR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"83\",\"name\":\"CURR_CAL_DT_MNS_2YEAR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"84\",\"name\":\"CURR_CAL_DT_MNS_1QTR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"85\",\"name\":\"CURR_CAL_DT_MNS_2QTR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"86\",\"name\":\"CURR_CAL_DT_MNS_1MONTH_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"87\",\"name\":\"CURR_CAL_DT_MNS_2MONTH_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"88\",\"name\":\"CURR_CAL_DT_MNS_1WEEK_YN_IND\",\"datatype\":\"tinyint\"},{\"id\":\"89\",\"name\":\"CURR_CAL_DT_MNS_2WEEK_YN_IND\",\"datatype\":\"tinyint\"},{\"id\":\"90\",\"name\":\"RTL_MONTH_OF_RTL_YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"91\",\"name\":\"RTL_QTR_OF_RTL_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"92\",\"name\":\"RTL_WEEK_OF_RTL_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"93\",\"name\":\"SEASON_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"94\",\"name\":\"YTM_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"95\",\"name\":\"YTQ_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"96\",\"name\":\"YTW_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"97\",\"name\":\"KYLIN_CAL_DT_CRE_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"98\",\"name\":\"KYLIN_CAL_DT_CRE_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"99\",\"name\":\"KYLIN_CAL_DT_UPD_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"100\",\"name\":\"KYLIN_CAL_DT_UPD_USER\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}},{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"datatype\":\"integer\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"datatype\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"datatype\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"datatype\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"datatype\":\"varchar(256)\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"datatype\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"14\",\"name\":\"META_CATEG_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"datatype\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"datatype\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"datatype\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_NAME\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}},{\"resourceId\":\"19e90d39-6cc3-4d20-b08d-1d8771f1ea1d\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_COUNTRY\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"COUNTRY\",\"datatype\":\"varchar(256)\"},{\"id\":\"2\",\"name\":\"LATITUDE\",\"datatype\":\"double\"},{\"id\":\"3\",\"name\":\"LONGITUDE\",\"datatype\":\"double\"},{\"id\":\"4\",\"name\":\"NAME\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}},{\"resourceId\":\"f86af73f-c96a-4eb8-9de7-3cca85aae998\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_SALES\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"TRANS_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"PART_DT\",\"datatype\":\"date\"},{\"id\":\"3\",\"name\":\"LSTG_FORMAT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"4\",\"name\":\"LEAF_CATEG_ID\",\"datatype\":\"bigint\"},{\"id\":\"5\",\"name\":\"LSTG_SITE_ID\",\"datatype\":\"integer\"},{\"id\":\"6\",\"name\":\"SLR_SEGMENT_CD\",\"datatype\":\"smallint\"},{\"id\":\"7\",\"name\":\"PRICE\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"8\",\"name\":\"ITEM_COUNT\",\"datatype\":\"bigint\"},{\"id\":\"9\",\"name\":\"SELLER_ID\",\"datatype\":\"bigint\"},{\"id\":\"10\",\"name\":\"BUYER_ID\",\"datatype\":\"bigint\"},{\"id\":\"11\",\"name\":\"OPS_USER_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"OPS_REGION\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}},{\"resourceId\":\"2abeafff-b218-46de-b06c-abd10365d273\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_ACCOUNT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"ACCOUNT_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"ACCOUNT_BUYER_LEVEL\",\"datatype\":\"integer\"},{\"id\":\"3\",\"name\":\"ACCOUNT_SELLER_LEVEL\",\"datatype\":\"integer\"},{\"id\":\"4\",\"name\":\"ACCOUNT_COUNTRY\",\"datatype\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"ACCOUNT_CONTACT\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}},{\"resourceId\":\"a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CAL_DT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"CAL_DT\",\"datatype\":\"date\",\"comment\":\"Date, PK\"},{\"id\":\"2\",\"name\":\"YEAR_BEG_DT\",\"datatype\":\"date\",\"comment\":\"YEAR Begin Date\"},{\"id\":\"3\",\"name\":\"QTR_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Quarter Begin Date\"},{\"id\":\"4\",\"name\":\"MONTH_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Month Begin Date\"},{\"id\":\"5\",\"name\":\"WEEK_BEG_DT\",\"datatype\":\"date\",\"comment\":\"Week Begin Date\"},{\"id\":\"6\",\"name\":\"AGE_FOR_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"7\",\"name\":\"AGE_FOR_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"8\",\"name\":\"AGE_FOR_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"9\",\"name\":\"AGE_FOR_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"10\",\"name\":\"AGE_FOR_DT_ID\",\"datatype\":\"smallint\"},{\"id\":\"11\",\"name\":\"AGE_FOR_RTL_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"12\",\"name\":\"AGE_FOR_RTL_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"13\",\"name\":\"AGE_FOR_RTL_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"14\",\"name\":\"AGE_FOR_RTL_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"15\",\"name\":\"AGE_FOR_CS_WEEK_ID\",\"datatype\":\"smallint\"},{\"id\":\"16\",\"name\":\"DAY_OF_CAL_ID\",\"datatype\":\"integer\"},{\"id\":\"17\",\"name\":\"DAY_OF_YEAR_ID\",\"datatype\":\"smallint\"},{\"id\":\"18\",\"name\":\"DAY_OF_QTR_ID\",\"datatype\":\"smallint\"},{\"id\":\"19\",\"name\":\"DAY_OF_MONTH_ID\",\"datatype\":\"smallint\"},{\"id\":\"20\",\"name\":\"DAY_OF_WEEK_ID\",\"datatype\":\"integer\"},{\"id\":\"21\",\"name\":\"WEEK_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"22\",\"name\":\"WEEK_OF_CAL_ID\",\"datatype\":\"integer\"},{\"id\":\"23\",\"name\":\"MONTH_OF_QTR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"24\",\"name\":\"MONTH_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"25\",\"name\":\"MONTH_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"26\",\"name\":\"QTR_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"27\",\"name\":\"QTR_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"28\",\"name\":\"YEAR_OF_CAL_ID\",\"datatype\":\"smallint\"},{\"id\":\"29\",\"name\":\"YEAR_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"30\",\"name\":\"QTR_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"MONTH_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"32\",\"name\":\"WEEK_END_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"33\",\"name\":\"CAL_DT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"34\",\"name\":\"CAL_DT_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"35\",\"name\":\"CAL_DT_SHORT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"36\",\"name\":\"YTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"37\",\"name\":\"QTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"38\",\"name\":\"MTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"39\",\"name\":\"WTD_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"40\",\"name\":\"SEASON_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"41\",\"name\":\"DAY_IN_YEAR_COUNT\",\"datatype\":\"smallint\"},{\"id\":\"42\",\"name\":\"DAY_IN_QTR_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"43\",\"name\":\"DAY_IN_MONTH_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"44\",\"name\":\"DAY_IN_WEEK_COUNT\",\"datatype\":\"tinyint\"},{\"id\":\"45\",\"name\":\"RTL_YEAR_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"46\",\"name\":\"RTL_QTR_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"47\",\"name\":\"RTL_MONTH_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"48\",\"name\":\"RTL_WEEK_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"49\",\"name\":\"CS_WEEK_BEG_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"50\",\"name\":\"CAL_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"51\",\"name\":\"DAY_OF_WEEK\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"52\",\"name\":\"MONTH_ID\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"53\",\"name\":\"PRD_DESC\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"54\",\"name\":\"PRD_FLAG\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"55\",\"name\":\"PRD_ID\",\"datatype\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"56\",\"name\":\"PRD_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"57\",\"name\":\"QTR_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"58\",\"name\":\"QTR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"59\",\"name\":\"QTR_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"60\",\"name\":\"RETAIL_WEEK\",\"datatype\":\"varchar(256)\"},{\"id\":\"61\",\"name\":\"RETAIL_YEAR\",\"datatype\":\"varchar(256)\"},{\"id\":\"62\",\"name\":\"RETAIL_START_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"63\",\"name\":\"RETAIL_WK_END_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"64\",\"name\":\"WEEK_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"65\",\"name\":\"WEEK_NUM_DESC\",\"datatype\":\"varchar(256)\"},{\"id\":\"66\",\"name\":\"WEEK_BEG_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"67\",\"name\":\"WEEK_END_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"68\",\"name\":\"WEEK_IN_YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"69\",\"name\":\"WEEK_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"70\",\"name\":\"WEEK_BEG_END_DESC_MDY\",\"datatype\":\"varchar(256)\"},{\"id\":\"71\",\"name\":\"WEEK_BEG_END_DESC_MD\",\"datatype\":\"varchar(256)\"},{\"id\":\"72\",\"name\":\"YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"73\",\"name\":\"YEAR_IND\",\"datatype\":\"varchar(256)\"},{\"id\":\"74\",\"name\":\"CAL_DT_MNS_1YEAR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"75\",\"name\":\"CAL_DT_MNS_2YEAR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"76\",\"name\":\"CAL_DT_MNS_1QTR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"77\",\"name\":\"CAL_DT_MNS_2QTR_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"78\",\"name\":\"CAL_DT_MNS_1MONTH_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"79\",\"name\":\"CAL_DT_MNS_2MONTH_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"80\",\"name\":\"CAL_DT_MNS_1WEEK_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"81\",\"name\":\"CAL_DT_MNS_2WEEK_DT\",\"datatype\":\"varchar(256)\"},{\"id\":\"82\",\"name\":\"CURR_CAL_DT_MNS_1YEAR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"83\",\"name\":\"CURR_CAL_DT_MNS_2YEAR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"84\",\"name\":\"CURR_CAL_DT_MNS_1QTR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"85\",\"name\":\"CURR_CAL_DT_MNS_2QTR_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"86\",\"name\":\"CURR_CAL_DT_MNS_1MONTH_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"87\",\"name\":\"CURR_CAL_DT_MNS_2MONTH_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"88\",\"name\":\"CURR_CAL_DT_MNS_1WEEK_YN_IND\",\"datatype\":\"tinyint\"},{\"id\":\"89\",\"name\":\"CURR_CAL_DT_MNS_2WEEK_YN_IND\",\"datatype\":\"tinyint\"},{\"id\":\"90\",\"name\":\"RTL_MONTH_OF_RTL_YEAR_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"91\",\"name\":\"RTL_QTR_OF_RTL_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"92\",\"name\":\"RTL_WEEK_OF_RTL_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"93\",\"name\":\"SEASON_OF_YEAR_ID\",\"datatype\":\"tinyint\"},{\"id\":\"94\",\"name\":\"YTM_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"95\",\"name\":\"YTQ_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"96\",\"name\":\"YTW_YN_ID\",\"datatype\":\"tinyint\"},{\"id\":\"97\",\"name\":\"KYLIN_CAL_DT_CRE_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"98\",\"name\":\"KYLIN_CAL_DT_CRE_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"99\",\"name\":\"KYLIN_CAL_DT_UPD_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"100\",\"name\":\"KYLIN_CAL_DT_UPD_USER\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}},{\"resourceId\":\"c65b5c62-6d14-4456-bc37-2f7bff28fcca\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"datatype\":\"integer\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"datatype\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"datatype\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"datatype\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"datatype\":\"varchar(256)\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"datatype\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"datatype\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"datatype\":\"varchar(256)\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"14\",\"name\":\"META_CATEG_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"datatype\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"datatype\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"datatype\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_NAME\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}},{\"resourceId\":\"34ac809f-7728-423b-81af-29f931c2da39\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_COUNTRY\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"COUNTRY\",\"datatype\":\"varchar(256)\"},{\"id\":\"2\",\"name\":\"LATITUDE\",\"datatype\":\"double\"},{\"id\":\"3\",\"name\":\"LONGITUDE\",\"datatype\":\"double\"},{\"id\":\"4\",\"name\":\"NAME\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}},{\"resourceId\":\"3fa28668-b02c-49cf-aa53-008e06c49cb7\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_SALES\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"TRANS_ID\",\"datatype\":\"bigint\"},{\"id\":\"2\",\"name\":\"PART_DT\",\"datatype\":\"date\"},{\"id\":\"3\",\"name\":\"LSTG_FORMAT_NAME\",\"datatype\":\"varchar(256)\"},{\"id\":\"4\",\"name\":\"LEAF_CATEG_ID\",\"datatype\":\"bigint\"},{\"id\":\"5\",\"name\":\"LSTG_SITE_ID\",\"datatype\":\"integer\"},{\"id\":\"6\",\"name\":\"SLR_SEGMENT_CD\",\"datatype\":\"smallint\"},{\"id\":\"7\",\"name\":\"PRICE\",\"datatype\":\"decimal(19,4)\"},{\"id\":\"8\",\"name\":\"ITEM_COUNT\",\"datatype\":\"bigint\"},{\"id\":\"9\",\"name\":\"SELLER_ID\",\"datatype\":\"bigint\"},{\"id\":\"10\",\"name\":\"BUYER_ID\",\"datatype\":\"bigint\"},{\"id\":\"11\",\"name\":\"OPS_USER_ID\",\"datatype\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"OPS_REGION\",\"datatype\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}]";
//        List<DemoColumn> list = JSONObject.parseArray(json, DemoColumn.class);
//        List<String> listColumn = new ArrayList<String>();
//        for (String resour : resourceIds) {
//            Optional<DemoColumn> dr = list.stream().filter(p -> p.getResourceId().equals(resour)).findFirst();
//            String json1 = JSON.toJSONString(dr.get());
//            listColumn.add(json1);
//        }
//        return listColumn;

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
                    json = "{\"resourceId\":\"ef3cb5ff-03e2-438d-a729-cbdf60ff39cb\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"dataType\":\"integer\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"dataType\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"dataType\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"dataType\":\"varchar(256)\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"dataType\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"14\",\"name\":\"META_CATEG_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"dataType\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"dataType\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_NAME\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"KYLIN\"}}";
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
                case "c65b5c62-6d14-4456-bc37-2f7bff28fcca":
                    json = "{\"resourceId\":\"c65b5c62-6d14-4456-bc37-2f7bff28fcca\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CATEGORY_GROUPINGS\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"LEAF_CATEG_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"3\",\"name\":\"SITE_ID\",\"dataType\":\"integer\"},{\"id\":\"4\",\"name\":\"CATEG_BUSN_MGR\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"CATEG_BUSN_UNIT\",\"dataType\":\"varchar(256)\"},{\"id\":\"6\",\"name\":\"REGN_CATEG\",\"dataType\":\"varchar(256)\"},{\"id\":\"7\",\"name\":\"USER_DEFINED_FIELD1\",\"dataType\":\"varchar(256)\"},{\"id\":\"8\",\"name\":\"USER_DEFINED_FIELD3\",\"dataType\":\"varchar(256)\"},{\"id\":\"9\",\"name\":\"KYLIN_GROUPINGS_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"10\",\"name\":\"KYLIN_GROUPINGS_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"11\",\"name\":\"KYLIN_GROUPINGS_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"KYLIN_GROUPINGS_UPD_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"13\",\"name\":\"META_CATEG_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"14\",\"name\":\"META_CATEG_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"15\",\"name\":\"CATEG_LVL2_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"16\",\"name\":\"CATEG_LVL3_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"17\",\"name\":\"CATEG_LVL4_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"18\",\"name\":\"CATEG_LVL5_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"19\",\"name\":\"CATEG_LVL6_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"20\",\"name\":\"CATEG_LVL7_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"21\",\"name\":\"CATEG_LVL2_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"22\",\"name\":\"CATEG_LVL3_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"23\",\"name\":\"CATEG_LVL4_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"24\",\"name\":\"CATEG_LVL5_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"25\",\"name\":\"CATEG_LVL6_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"26\",\"name\":\"CATEG_LVL7_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"27\",\"name\":\"CATEG_FLAGS\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"28\",\"name\":\"ADULT_CATEG_YN\",\"dataType\":\"varchar(256)\"},{\"id\":\"29\",\"name\":\"DOMAIN_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"30\",\"name\":\"USER_DEFINED_FIELD5\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"VCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"32\",\"name\":\"GCS_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"33\",\"name\":\"MOVE_TO\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"34\",\"name\":\"SAP_CATEGORY_ID\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"35\",\"name\":\"SRC_ID\",\"dataType\":\"tinyint\"},{\"id\":\"36\",\"name\":\"BSNS_VRTCL_NAME\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                    break;
                case "3fa28668-b02c-49cf-aa53-008e06c49cb7":
                    json = "{\"resourceId\":\"3fa28668-b02c-49cf-aa53-008e06c49cb7\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_SALES\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"TRANS_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"PART_DT\",\"dataType\":\"date\"},{\"id\":\"3\",\"name\":\"LSTG_FORMAT_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"4\",\"name\":\"LEAF_CATEG_ID\",\"dataType\":\"bigint\"},{\"id\":\"5\",\"name\":\"LSTG_SITE_ID\",\"dataType\":\"integer\"},{\"id\":\"6\",\"name\":\"SLR_SEGMENT_CD\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"PRICE\",\"dataType\":\"decimal(19,4)\"},{\"id\":\"8\",\"name\":\"ITEM_COUNT\",\"dataType\":\"bigint\"},{\"id\":\"9\",\"name\":\"SELLER_ID\",\"dataType\":\"bigint\"},{\"id\":\"10\",\"name\":\"BUYER_ID\",\"dataType\":\"bigint\"},{\"id\":\"11\",\"name\":\"OPS_USER_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"12\",\"name\":\"OPS_REGION\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                    break;
                case "2abeafff-b218-46de-b06c-abd10365d273":
                    json = "{\"resourceId\":\"2abeafff-b218-46de-b06c-abd10365d273\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_ACCOUNT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"ACCOUNT_ID\",\"dataType\":\"bigint\"},{\"id\":\"2\",\"name\":\"ACCOUNT_BUYER_LEVEL\",\"dataType\":\"integer\"},{\"id\":\"3\",\"name\":\"ACCOUNT_SELLER_LEVEL\",\"dataType\":\"integer\"},{\"id\":\"4\",\"name\":\"ACCOUNT_COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"5\",\"name\":\"ACCOUNT_CONTACT\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                    break;
                case "a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972":
                    json = "{\"resourceId\":\"a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_CAL_DT\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"CAL_DT\",\"dataType\":\"date\",\"comment\":\"Date, PK\"},{\"id\":\"2\",\"name\":\"YEAR_BEG_DT\",\"dataType\":\"date\",\"comment\":\"YEAR Begin Date\"},{\"id\":\"3\",\"name\":\"QTR_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Quarter Begin Date\"},{\"id\":\"4\",\"name\":\"MONTH_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Month Begin Date\"},{\"id\":\"5\",\"name\":\"WEEK_BEG_DT\",\"dataType\":\"date\",\"comment\":\"Week Begin Date\"},{\"id\":\"6\",\"name\":\"AGE_FOR_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"7\",\"name\":\"AGE_FOR_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"8\",\"name\":\"AGE_FOR_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"9\",\"name\":\"AGE_FOR_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"10\",\"name\":\"AGE_FOR_DT_ID\",\"dataType\":\"smallint\"},{\"id\":\"11\",\"name\":\"AGE_FOR_RTL_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"12\",\"name\":\"AGE_FOR_RTL_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"13\",\"name\":\"AGE_FOR_RTL_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"14\",\"name\":\"AGE_FOR_RTL_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"15\",\"name\":\"AGE_FOR_CS_WEEK_ID\",\"dataType\":\"smallint\"},{\"id\":\"16\",\"name\":\"DAY_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"17\",\"name\":\"DAY_OF_YEAR_ID\",\"dataType\":\"smallint\"},{\"id\":\"18\",\"name\":\"DAY_OF_QTR_ID\",\"dataType\":\"smallint\"},{\"id\":\"19\",\"name\":\"DAY_OF_MONTH_ID\",\"dataType\":\"smallint\"},{\"id\":\"20\",\"name\":\"DAY_OF_WEEK_ID\",\"dataType\":\"integer\"},{\"id\":\"21\",\"name\":\"WEEK_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"22\",\"name\":\"WEEK_OF_CAL_ID\",\"dataType\":\"integer\"},{\"id\":\"23\",\"name\":\"MONTH_OF_QTR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"24\",\"name\":\"MONTH_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"25\",\"name\":\"MONTH_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"26\",\"name\":\"QTR_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"27\",\"name\":\"QTR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"28\",\"name\":\"YEAR_OF_CAL_ID\",\"dataType\":\"smallint\"},{\"id\":\"29\",\"name\":\"YEAR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"30\",\"name\":\"QTR_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"31\",\"name\":\"MONTH_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"32\",\"name\":\"WEEK_END_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"33\",\"name\":\"CAL_DT_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"34\",\"name\":\"CAL_DT_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"35\",\"name\":\"CAL_DT_SHORT_NAME\",\"dataType\":\"varchar(256)\"},{\"id\":\"36\",\"name\":\"YTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"37\",\"name\":\"QTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"38\",\"name\":\"MTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"39\",\"name\":\"WTD_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"40\",\"name\":\"SEASON_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"41\",\"name\":\"DAY_IN_YEAR_COUNT\",\"dataType\":\"smallint\"},{\"id\":\"42\",\"name\":\"DAY_IN_QTR_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"43\",\"name\":\"DAY_IN_MONTH_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"44\",\"name\":\"DAY_IN_WEEK_COUNT\",\"dataType\":\"tinyint\"},{\"id\":\"45\",\"name\":\"RTL_YEAR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"46\",\"name\":\"RTL_QTR_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"47\",\"name\":\"RTL_MONTH_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"48\",\"name\":\"RTL_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"49\",\"name\":\"CS_WEEK_BEG_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"50\",\"name\":\"CAL_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"51\",\"name\":\"DAY_OF_WEEK\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"52\",\"name\":\"MONTH_ID\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"53\",\"name\":\"PRD_DESC\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"54\",\"name\":\"PRD_FLAG\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"55\",\"name\":\"PRD_ID\",\"dataType\":\"varchar(256)\",\"comment\":\"\"},{\"id\":\"56\",\"name\":\"PRD_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"57\",\"name\":\"QTR_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"58\",\"name\":\"QTR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"59\",\"name\":\"QTR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"60\",\"name\":\"RETAIL_WEEK\",\"dataType\":\"varchar(256)\"},{\"id\":\"61\",\"name\":\"RETAIL_YEAR\",\"dataType\":\"varchar(256)\"},{\"id\":\"62\",\"name\":\"RETAIL_START_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"63\",\"name\":\"RETAIL_WK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"64\",\"name\":\"WEEK_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"65\",\"name\":\"WEEK_NUM_DESC\",\"dataType\":\"varchar(256)\"},{\"id\":\"66\",\"name\":\"WEEK_BEG_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"67\",\"name\":\"WEEK_END_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"68\",\"name\":\"WEEK_IN_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"69\",\"name\":\"WEEK_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"70\",\"name\":\"WEEK_BEG_END_DESC_MDY\",\"dataType\":\"varchar(256)\"},{\"id\":\"71\",\"name\":\"WEEK_BEG_END_DESC_MD\",\"dataType\":\"varchar(256)\"},{\"id\":\"72\",\"name\":\"YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"73\",\"name\":\"YEAR_IND\",\"dataType\":\"varchar(256)\"},{\"id\":\"74\",\"name\":\"CAL_DT_MNS_1YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"75\",\"name\":\"CAL_DT_MNS_2YEAR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"76\",\"name\":\"CAL_DT_MNS_1QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"77\",\"name\":\"CAL_DT_MNS_2QTR_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"78\",\"name\":\"CAL_DT_MNS_1MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"79\",\"name\":\"CAL_DT_MNS_2MONTH_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"80\",\"name\":\"CAL_DT_MNS_1WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"81\",\"name\":\"CAL_DT_MNS_2WEEK_DT\",\"dataType\":\"varchar(256)\"},{\"id\":\"82\",\"name\":\"CURR_CAL_DT_MNS_1YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"83\",\"name\":\"CURR_CAL_DT_MNS_2YEAR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"84\",\"name\":\"CURR_CAL_DT_MNS_1QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"85\",\"name\":\"CURR_CAL_DT_MNS_2QTR_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"86\",\"name\":\"CURR_CAL_DT_MNS_1MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"87\",\"name\":\"CURR_CAL_DT_MNS_2MONTH_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"88\",\"name\":\"CURR_CAL_DT_MNS_1WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"89\",\"name\":\"CURR_CAL_DT_MNS_2WEEK_YN_IND\",\"dataType\":\"tinyint\"},{\"id\":\"90\",\"name\":\"RTL_MONTH_OF_RTL_YEAR_ID\",\"dataType\":\"varchar(256)\"},{\"id\":\"91\",\"name\":\"RTL_QTR_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"92\",\"name\":\"RTL_WEEK_OF_RTL_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"93\",\"name\":\"SEASON_OF_YEAR_ID\",\"dataType\":\"tinyint\"},{\"id\":\"94\",\"name\":\"YTM_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"95\",\"name\":\"YTQ_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"96\",\"name\":\"YTW_YN_ID\",\"dataType\":\"tinyint\"},{\"id\":\"97\",\"name\":\"KYLIN_CAL_DT_CRE_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"98\",\"name\":\"KYLIN_CAL_DT_CRE_USER\",\"dataType\":\"varchar(256)\"},{\"id\":\"99\",\"name\":\"KYLIN_CAL_DT_UPD_DATE\",\"dataType\":\"varchar(256)\"},{\"id\":\"100\",\"name\":\"KYLIN_CAL_DT_UPD_USER\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
                    break;
                case "34ac809f-7728-423b-81af-29f931c2da39":
                    json = "{\"resourceId\":\"34ac809f-7728-423b-81af-29f931c2da39\",\"last_modified\":0,\"version\":\"2.3.1.0\",\"name\":\"KYLIN_COUNTRY\",\"data\":{\"columns\":[{\"id\":\"1\",\"name\":\"COUNTRY\",\"dataType\":\"varchar(256)\"},{\"id\":\"2\",\"name\":\"LATITUDE\",\"dataType\":\"double\"},{\"id\":\"3\",\"name\":\"LONGITUDE\",\"dataType\":\"double\"},{\"id\":\"4\",\"name\":\"NAME\",\"dataType\":\"varchar(256)\"}],\"source_type\":1,\"table_type\":null,\"database\":\"DEFAULT\"}}";
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
    public JobStepOutputMapper getJobStepOut(String jobId, String stepId) throws APIException {
        return jobsHttpClient.output(jobId, stepId);
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

    @ApiOperation(value = "查看立方体定时构建配置")
    @RequestMapping(value = "/getTimingrefresh", method = RequestMethod.GET)
    @Security(session = true)
    public OlapTimingrefresh getTimingrefresh(String cubeName) throws APIException {
        return olapTimingrefreshService.findTableInfo(cubeName);
    }

}
