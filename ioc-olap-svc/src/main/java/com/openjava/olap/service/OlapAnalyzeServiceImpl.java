package com.openjava.olap.service;

import com.openjava.olap.common.StringUtils;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.*;
import com.openjava.olap.dto.TableInnerDto;
import com.openjava.olap.mapper.kylin.QueryResultMapper;
import com.openjava.olap.query.OlapAnalyzeDBParam;
import com.openjava.olap.repository.*;
import com.openjava.olap.vo.AnalyzeAxisVo;
import com.openjava.olap.vo.AnalyzeVo;
import com.openjava.olap.vo.AnyDimensionCellVo;
import com.openjava.olap.vo.AnyDimensionVo;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapAnalyzeServiceImpl implements OlapAnalyzeService {

    @Resource
    private OlapAnalyzeRepository olapAnalyzeRepository;

    @Resource
    private OlapCubeTableRelationRepository olapCubeTableRelationRepository;

    @Resource
    private OlapAnalyzeAxisRepository olapAnalyzeAxisRepository;

    @Resource
    private OlapAnalyzeAxisFilterRepository olapAnalyzeAxisFilterRepository;

    @Resource
    private OlapCubeTableRepository olapCubeTableRepository;

    @Resource
    private OlapCubeTableColumnRepository olapCubeTableColumnRepository;

    @Resource
    private OlapCubeRepository olapCubeRepository;

    /**
     * 麒麟访问hive数据库的库名
     **/
    @Value("${olap.kylin.databaseName:olap}")
    private String KYLIN_DATABASE_NAME;

    @Autowired
    private CubeHttpClient cubeHttpClient;

    public Page<OlapAnalyze> query(OlapAnalyzeDBParam params, Pageable pageable) {
        Page<OlapAnalyze> pageresult = olapAnalyzeRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapAnalyze> queryDataOnly(OlapAnalyzeDBParam params, Pageable pageable) {
        return olapAnalyzeRepository.queryDataOnly(params, pageable);
    }

    public OlapAnalyze get(Long id) throws APIException {
        Optional<OlapAnalyze> o = olapAnalyzeRepository.findById(id);
        if (o.isPresent()) {
            OlapAnalyze m = o.get();
            return m;
        }
        throw new APIException(400, "找不到记录OlapAnalyze：" + id);
    }

    public OlapAnalyze doSave(OlapAnalyze m) {
        return olapAnalyzeRepository.save(m);
    }

    public void doDelete(Long id) {
        olapAnalyzeRepository.deleteById(id);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapAnalyzeRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public List<OlapAnalyze> getListWithFolderId(Long folderId) {
        return olapAnalyzeRepository.findByFolderId(folderId);
    }

    @Override
    public List<OlapAnalyze> getAllShares(Long userId) {
        return olapAnalyzeRepository.getAllShares(userId);
    }

    private void writeJoinTable(OlapCubeTable dict, List<OlapCubeTableRelation> relations, ArrayList<TableInnerDto> joinTables, List<OlapCubeTable> cubeTables, Long tableId) throws APIException {
        if (tableId.equals(dict.getId())) {
            return;
        }

        OlapCubeTableRelation relation = relations.stream().filter((p) -> p.getJoinTableId().equals(tableId)).findFirst().orElse(null);

        if (relation == null) {
            throw new APIException(10002, "结构错误！");
        }
        if (!relation.getTableId().equals(dict.getCubeTableId())) {
            writeJoinTable(dict, relations, joinTables, cubeTables, relation.getTableId());
        }
        if (joinTables.stream().filter((p) -> p.getJoinTableId().equals(tableId)).count() == 0) {
            OlapCubeTable joinTable = cubeTables.stream().filter((p) -> p.getCubeTableId().equals(tableId)).findFirst().get();
            OlapCubeTable sourceTable = cubeTables.stream().filter((p) -> p.getCubeTableId().equals(relation.getTableId())).findFirst().get();
            TableInnerDto innerDto = new TableInnerDto();
            innerDto.setJoinTable(joinTable.getTableName());
            innerDto.setJoinTableAlias(joinTable.getTableAlias());
            innerDto.setJoinTableId(tableId);
            innerDto.setJoinType(relation.getJoinType());
            innerDto.setSourceTableAlias(sourceTable.getTableAlias());
            innerDto.setJoinFkKeys(relation.getFkKey().split(","));
            innerDto.setJoinPkKeys(relation.getPkKey().split(","));
            innerDto.setDatabaseName(joinTable.getDatabaseName());
            joinTables.add(innerDto);
        }
    }

    private String builderSql(ArrayList<String> selectColumns, ArrayList<TableInnerDto> joinTables, ArrayList<String> filters, ArrayList<String> groups, OlapCubeTable dict) {
        List<String> joinTableSqls = joinTables.stream().map((p) -> {
            String joinOn = "", fk, pk;
            for (Integer i = 0; i < p.getJoinPkKeys().length; i++) {
                pk = p.getJoinPkKeys()[i];
                fk = p.getJoinFkKeys()[i];
                if (!StringUtils.isNullOrEmpty(pk)) {
                    joinOn += p.getSourceTableAlias() + "." + fk + "=" + p.getJoinTableAlias() + "." + pk;
                    if (i < p.getJoinPkKeys().length - 1) {
                        joinOn += " and ";
                    }
                }
            }
            return p.getJoinType() + " join " + KYLIN_DATABASE_NAME + "." + p.getJoinTable() + " as " + p.getJoinTableAlias() + " on " + joinOn;
        }).collect(Collectors.toList());
        String where = filters.size() > 0 ? String.join(" and ", filters) : "1=1";
        String sql = MessageFormat.format("select {0} from {1} as {2} {3} where {4} group by {5} order by {5}", String.join(",", selectColumns),
                KYLIN_DATABASE_NAME + "." + dict.getTableName(), dict.getTableAlias(), String.join(" ", joinTableSqls), where, String.join(",", groups));
        return sql;
    }

    @Override
    @Transactional(readOnly = false)
    public AnalyzeVo save(AnalyzeVo analyzeVo) throws APIException {
        String sql = getSql(analyzeVo.getCubeId(), analyzeVo.getOlapAnalyzeAxes());
        OlapAnalyze analyze = new OlapAnalyze();
        analyze.setSql(sql);
        analyze.setAnalyzeId(analyzeVo.getAnalyzeId());
        analyze.setCreateId(analyzeVo.getCreateId());
        analyze.setCreateName(analyzeVo.getCreateName());
        analyze.setCreateTime(analyzeVo.getCreateTime());
        analyze.setCubeId(analyzeVo.getCubeId());
        analyze.setFlags(analyzeVo.getFlags());
        analyze.setFolderId(analyzeVo.getFolderId());
        analyze.setIsNew(analyzeVo.getIsNew());
        analyze.setName(analyzeVo.getName());
        analyze.setUpdateId(analyzeVo.getUpdateId());
        analyze.setUpdateName(analyzeVo.getUpdateName());
        analyze.setUpdateTime(analyzeVo.getUpdateTime());
        analyze.setIsSummation(analyzeVo.getIsSummation());
        olapAnalyzeRepository.save(analyze);
        if (analyze.getIsNew() != null && !analyze.getIsNew()) {
            olapAnalyzeAxisRepository.deleteByAnalyzeId(analyze.getAnalyzeId());
            olapAnalyzeAxisFilterRepository.deleteByAnalyzeId(analyze.getAnalyzeId());
        }
        for (AnalyzeAxisVo axis : analyzeVo.getOlapAnalyzeAxes()) {
            OlapAnalyzeAxis analyzeAxis = new OlapAnalyzeAxis();
            analyzeAxis.setAnalyzeAxisId(ConcurrentSequence.getInstance().getSequence());
            analyzeAxis.setAnalyzeId(analyze.getAnalyzeId());
            analyzeAxis.setColumnId(axis.getColumnId());
            analyzeAxis.setType(axis.getType());
            analyzeAxis.setIsNew(true);
            analyzeAxis.setTableId(axis.getTableId());
            olapAnalyzeAxisRepository.save(analyzeAxis);
            if (axis.getType() == 4) {
                OlapAnalyzeAxisFilter analyzeAxisFilter = new OlapAnalyzeAxisFilter();
                analyzeAxisFilter.setAnalyzeAxisFilterId(ConcurrentSequence.getInstance().getSequence());
                analyzeAxisFilter.setAxisId(analyzeAxis.getAnalyzeAxisId());
                analyzeAxisFilter.setAnalyzeId(analyze.getAnalyzeId());
                analyzeAxisFilter.setIsInclude(axis.getIsInclude());
                analyzeAxisFilter.setSelectValues(axis.getSelectValues());
                analyzeAxisFilter.setIsNew(true);
                olapAnalyzeAxisFilterRepository.save(analyzeAxisFilter);
            }
        }

        return analyzeVo;
    }

    private String getSql(Long cubeId, List<AnalyzeAxisVo> axisVos) throws APIException {
        ArrayList<String> selectColumns = new ArrayList<String>();
        ArrayList<TableInnerDto> joinTables = new ArrayList<TableInnerDto>();
        ArrayList<String> filters = new ArrayList<String>();
        ArrayList<String> groups = new ArrayList<String>();
        String group, column, filter;
        List<OlapCubeTableRelation> relations = olapCubeTableRelationRepository.findByCubeId(cubeId);
        List<OlapCubeTable> cubeTables = olapCubeTableRepository.findByCubeId(cubeId);
        OlapCubeTable dict = cubeTables.stream().filter(p -> p.getIsDict() == 1).findFirst().get();
        axisVos.sort(Comparator.comparing(AnalyzeAxisVo::getType));
        for (AnalyzeAxisVo axis : axisVos) {
            // 连接查询构建
            writeJoinTable(dict, relations, joinTables, cubeTables, axis.getTableId());
            //这里去掉列名带空格的情况，替换为下划线
            if (axis.getColumnName()!=null){
                axis.setColumnName(axis.getColumnName().replaceAll("\\s+","_"));
            }
            if (axis.getColumnChName()!=null){
                axis.setColumnChName(axis.getColumnChName().replaceAll("\\s+","_"));
            }
            // 列名构建
            if (axis.getType() != 4) {
                if (axis.getExpressionType() != null) {
                    //这里主要是为了防止SQL语法错误的问题
                    // ... as ... as后面的变量名不能是数字、关键字
                    column = MessageFormat.format(axis.getExpressionFull(), axis.getTableAlias(), axis.getColumnName(), axis.getExpressionType() + "_" + axis.getColumnChName() + "_");
                } else {

                    column = MessageFormat.format(axis.getExpressionFull(), axis.getTableAlias(), axis.getColumnName(), axis.getColumnChName());
                }
                if (!selectColumns.contains(column)) {
                    selectColumns.add(column);
                }

                // 分组语句中构建
                if (axis.getType() == 1 || axis.getType() == 2) {
                    group = axis.getTableAlias() + "." + axis.getColumnName();
                    if (!groups.contains(group)) {
                        groups.add(group);
                    }
                }
            } else {
                // 过滤条件构建
                if (axis.getSelectValues() != null && !axis.getSelectValues().equals("")) {
                    List<String> values = Arrays.stream(axis.getSelectValues().split(",")).map((p) -> {
                        return "'" + p + "'";
                    }).collect(Collectors.toList());
                    filter = "{0}.{1} {2} ({3})";
                    if (axis.getIsInclude() == 1) {
                        filter = MessageFormat.format(filter, axis.getTableAlias(), axis.getColumnName(), "in", String.join(",", values));
                    } else {
                        filter = MessageFormat.format(filter, axis.getTableAlias(), axis.getColumnName(), "not in", String.join(",", values));
                    }
                    filters.add(filter);
                }
            }
        }

        return builderSql(selectColumns, joinTables, filters, groups, dict);
    }

    @Override
    public AnalyzeVo getVo(Long id) throws APIException {
        AnalyzeVo analyzeVo = new AnalyzeVo();
        //获取analyze
        OlapAnalyze olapAnalyze = get(id);
        //复制属性到VO中
        MyBeanUtils.copyPropertiesNotBlank(analyzeVo, olapAnalyze);
        //查询子表数据
        List<OlapAnalyzeAxis> analyzeAxes = olapAnalyzeAxisRepository.findByAnalyzeId(id);
        List<OlapCubeTableColumn> cubeTableColumns = olapCubeTableColumnRepository.findByCubeId(analyzeVo.getCubeId());
        List<OlapCubeTable> cubeTables = olapCubeTableRepository.findByCubeId(analyzeVo.getCubeId());
        analyzeVo.setOlapAnalyzeAxes(new ArrayList<AnalyzeAxisVo>());
        for (OlapAnalyzeAxis axis : analyzeAxes) {
            OlapCubeTable cubeTable = cubeTables.stream().filter(p -> p.getCubeTableId().equals(axis.getTableId())).findFirst().get();
            OlapCubeTableColumn cubeTableColumn = cubeTableColumns.stream().filter(p -> p.getCubeTableColumnId().equals(axis.getColumnId())).findFirst().get();
            AnalyzeAxisVo axisVo = new AnalyzeAxisVo();
            MyBeanUtils.copyPropertiesNotBlank(axisVo, axis);
            axisVo.setName(cubeTable.getName());
            axisVo.setCubeId(cubeTable.getCubeId());
            axisVo.setTableName(cubeTable.getTableName());
            axisVo.setTableAlias(cubeTable.getTableAlias());
            axisVo.setIsDict(cubeTable.getIsDict());
            axisVo.setColumnName(cubeTableColumn.getColumnName());
            axisVo.setColumnChName(cubeTableColumn.getName());
            axisVo.setColumnAlias(cubeTableColumn.getColumnAlias());
            axisVo.setExpressionType(cubeTableColumn.getExpressionType());
            axisVo.setExpressionFull(cubeTableColumn.getExpressionFull());
            if (axis.getType() == 4) {
                OlapAnalyzeAxisFilter axisFilter = olapAnalyzeAxisFilterRepository.findByAxisId(axis.getAnalyzeAxisId());
                axisVo.setIsInclude(axisFilter.getIsInclude());
                axisVo.setSelectValues(axisFilter.getSelectValues());
            }
            analyzeVo.getOlapAnalyzeAxes().add(axisVo);
        }

        return analyzeVo;
    }

    private AnyDimensionCellVo getCell(ArrayList<AnyDimensionCellVo> rowCells, String id, Integer begin) {
        for (Integer i = begin; i < rowCells.size(); i++) {
            if (rowCells.get(i).getId().equals(id)) {
                return rowCells.get(i);
            }
        }
        return null;
    }

    @Override
    public AnyDimensionVo query(Long cubeId, List<AnalyzeAxisVo> axises, String userId) throws APIException {
        return query(cubeId, axises, userId, null, 0, 0);
    }

    private AnyDimensionVo query(Long cubeId, List<AnalyzeAxisVo> axises, String userId, String sql, Integer pageIndex, Integer pageSize) throws APIException {
        OlapCube cube = olapCubeRepository.findById(cubeId).orElse(null);
        if (cube == null) {
            throw new APIException(400, "模型已被删除！");
        }
        if (cube.getFlags() == 0) {
            throw new APIException(400, "模型未被启用！");
        }

        AnyDimensionVo anyDimensionVo = new AnyDimensionVo();
        List<ArrayList<AnyDimensionCellVo>> results = new ArrayList<ArrayList<AnyDimensionCellVo>>();
        if (sql == null || sql.equals("")) {
            sql = getSql(cubeId, axises);
        }
        QueryResultMapper resultMapper;
        try {
            resultMapper = cubeHttpClient.query(sql, 0, Integer.MAX_VALUE, cube.getCreateId().toString());
        } catch (Exception ex) {
            throw new APIException(400, "查询失败！");
        }
        MyBeanUtils.copyPropertiesNotBlank(anyDimensionVo, resultMapper);
        List<AnalyzeAxisVo> yAxises = axises.stream().filter(p -> p.getType().equals(2)).collect(Collectors.toList());
        List<AnalyzeAxisVo> xAxises = axises.stream().filter(p -> p.getType().equals(1)).collect(Collectors.toList());
        List<AnalyzeAxisVo> measureAxises = axises.stream().filter(p -> p.getType().equals(3)).collect(Collectors.toList());
        HashSet<String> axisXDatas = new HashSet<String>();
        TreeSet<String> axisYDatas = new TreeSet<String>();
        List<String> xTemps, yTemps, dTemps;
        String axisXData, axisYData;
        ArrayList<AnyDimensionCellVo> rowCells;
        AnyDimensionCellVo cell;
        Integer axisYCount = yAxises.size(), dataIndex = axisYCount, axisXCount = xAxises.size(), begin = axisYCount + 1, end = Integer.MAX_VALUE;
        if (isPaging(pageIndex, pageSize)) {
            begin += (pageIndex - 1) * pageSize;
            end = pageIndex * pageSize + axisYCount + 1;
        }
        List<Double> rowSummarys = new ArrayList<>();
        List<Double> columnSummarys = new ArrayList<>();
        //定义y轴头部
        for (Integer i = 0; i < yAxises.size(); i++) {
            rowCells = new ArrayList<AnyDimensionCellVo>();
            cell = new AnyDimensionCellVo("", axisXCount, 1, yAxises.get(i).getColumnChName(), 2);
            rowCells.add(cell);
            results.add(rowCells);
        }
        //定义x轴头部
        rowCells = new ArrayList<AnyDimensionCellVo>();
        for (Integer i = 0; i < xAxises.size(); i++) {
            cell = new AnyDimensionCellVo("", 1, 1, xAxises.get(i).getColumnChName(), 1);
            rowCells.add(cell);
        }
        results.add(rowCells);
        //写入具体维度、度量数据
        for (ArrayList<String> row : resultMapper.results) {
            xTemps = row.subList(0, axisXCount);
            yTemps = row.subList(axisXCount, axisXCount + axisYCount);
            dTemps = row.subList(axisXCount + axisYCount, row.size());

            axisXData = String.join("-", xTemps);
            axisYData = String.join("-", yTemps);
            //写入X轴
            if (!axisXDatas.contains(axisXData)) {
                axisXDatas.add(axisXData);
                dataIndex++;
                if (dataIndex >= begin && dataIndex < end) {
                    rowCells = new ArrayList<AnyDimensionCellVo>();
                    writeXData(results, measureAxises, axisYDatas, rowCells, xTemps, rowSummarys);
                }
            }
            if (dataIndex < begin || dataIndex >= end) {
                continue;
            }
            if (!axisYDatas.contains(axisYData)) {
                axisYDatas.add(axisYData);
                //写入Y轴
                writeYData(results, measureAxises, yTemps);

                // 写入度量轴
                writeMData(results, measureAxises, axisYCount);

                // 写入数据轴
                writeNewData(results, measureAxises, axisYCount, axisXCount, begin, axisYDatas, axisYData, rowCells, dTemps, dataIndex, rowSummarys, columnSummarys);
            } else {
                // 写入数据轴
                writeExistData(measureAxises, axisXCount, axisYDatas, axisYData, rowCells, dTemps, rowSummarys, columnSummarys);
            }
        }
        anyDimensionVo.setTotalRows(dataIndex - 1 - axisYCount);
        // 行列汇总
        rowAndColumnSummary(anyDimensionVo, results, axisYCount, axisXCount, rowSummarys, columnSummarys);
        return anyDimensionVo;
    }

    private void writeExistData(List<AnalyzeAxisVo> measureAxises, Integer axisXCount, TreeSet<String> axisYDatas, String axisYData, ArrayList<AnyDimensionCellVo> rowCells, List<String> dTemps, List<Double> rowSummarys, List<Double> columnSummarys) throws APIException {
        AnyDimensionCellVo cell;
        Integer beginIndex = GetSetIndex(axisYDatas, axisYData) * measureAxises.size() + axisXCount;
        for (String dTemp : dTemps) {
            cell = new AnyDimensionCellVo(axisYData, 1, 1, String.format("%.2f", Double.parseDouble(dTemp)), 4);
            rowCells.set(beginIndex, cell);
            columnSummarys.set(beginIndex - axisXCount, columnSummarys.get(beginIndex - axisXCount) + Double.parseDouble(dTemp));
            rowSummarys.set(rowSummarys.size() - 1, rowSummarys.get(rowSummarys.size() - 1) + Double.parseDouble(dTemp));
            beginIndex++;
        }
    }

    private Integer GetSetIndex(TreeSet<String> treeSet, String searchVal) throws APIException {
        Integer searchIndex = 0;
        for (Iterator iter = treeSet.iterator(); iter.hasNext(); ) {
            if (iter.next().equals(searchVal)) {
                return searchIndex;
            }
            searchIndex++;
        }
        throw new APIException(400, "没有找到数据元素！");
    }

    private void writeNewData(List<ArrayList<AnyDimensionCellVo>> results, List<AnalyzeAxisVo> measureAxises, Integer axisYCount, Integer axisXCount, Integer begin, TreeSet<String> axisYDatas, String axisYData, ArrayList<AnyDimensionCellVo> rowCells, List<String> dTemps, Integer dataIndex, List<Double> rowSummarys, List<Double> columnSummarys) throws APIException {
        AnyDimensionCellVo cell;
        Integer beginIndex = GetSetIndex(axisYDatas, axisYData) * measureAxises.size() + axisXCount;
        for (String dTemp : dTemps) {
            for (Integer i = begin; i < dataIndex; i++) {
                Integer row = dataIndex - i + axisYCount;
                if (results.get(row).size() > beginIndex) {
                    results.get(row).add(beginIndex, null);
                } else {
                    results.get(row).add(null);
                }
            }
            cell = new AnyDimensionCellVo(axisYData, 1, 1, String.format("%.2f", Double.parseDouble(dTemp)), 4);
            if (rowCells.size() > beginIndex) {
                rowCells.add(beginIndex, cell);
                columnSummarys.add(beginIndex - axisXCount, Double.parseDouble(dTemp));
                rowSummarys.set(rowSummarys.size() - 1, rowSummarys.get(rowSummarys.size() - 1) + Double.parseDouble(dTemp));
            } else {
                rowCells.add(cell);
                columnSummarys.add(Double.parseDouble(dTemp));
                rowSummarys.set(rowSummarys.size() - 1, rowSummarys.get(rowSummarys.size() - 1) + Double.parseDouble(dTemp));
            }
            beginIndex++;
        }
    }

    private void writeMData(List<ArrayList<AnyDimensionCellVo>> results, List<AnalyzeAxisVo> measureAxises, Integer axisYCount) {
        AnyDimensionCellVo cell;
        for (AnalyzeAxisVo measure : measureAxises) {
            cell = new AnyDimensionCellVo("", 1, 1, measure.getColumnChName(), 3);
            results.get(axisYCount).add(cell);
        }
    }

    private void writeYData(List<ArrayList<AnyDimensionCellVo>> results, List<AnalyzeAxisVo> measureAxises, List<String> yTemps) {
        String cellId;
        AnyDimensionCellVo cell;
        for (Integer i = 0; i < yTemps.size(); i++) {
            cellId = String.join("-", yTemps.subList(0, i + 1));
            cell = getCell(results.get(i), cellId, 1);
            if (cell == null) {
                cell = new AnyDimensionCellVo(cellId, measureAxises.size(), 1, yTemps.get(i), 2);
                results.get(i).add(cell);
                results.get(i).sort(Comparator.comparing(AnyDimensionCellVo::getId));
            } else {
                cell.setColspan(cell.getColspan() + measureAxises.size());
            }
        }
    }

    private void writeXData(List<ArrayList<AnyDimensionCellVo>> results, List<AnalyzeAxisVo> measureAxises, TreeSet<String> axisYDatas, ArrayList<AnyDimensionCellVo> rowCells, List<String> xTemps, List<Double> rowSummarys) {
        String cellId;
        for (Integer i = 0; i < xTemps.size(); i++) {
            cellId = String.join("-", xTemps.subList(0, i + 1));
            rowCells.add(new AnyDimensionCellVo(cellId, 1, 1, xTemps.get(i), 1));
        }
        for (Integer i = 0; i < axisYDatas.size() * measureAxises.size(); i++) {
            rowCells.add(null);
        }
        results.add(rowCells);
        rowSummarys.add(0.0);
    }

    private void rowAndColumnSummary(AnyDimensionVo anyDimensionVo, List<ArrayList<AnyDimensionCellVo>> results, Integer axisYCount, Integer axisXCount, List<Double> rowSummarys, List<Double> columnSummarys) {
        ArrayList<AnyDimensionCellVo> rowCells;
        Integer compareIndex = 0;
        rowCells = new ArrayList<AnyDimensionCellVo>();
        rowCells.add(new AnyDimensionCellVo("", axisXCount, 1, "汇总", 5));
        for (Double columnSummary : columnSummarys) {
            rowCells.add(new AnyDimensionCellVo("", 1, 1, String.format("%.2f", columnSummary), 5));
        }
        results.add(rowCells);
        rowSummarys.add(columnSummarys.stream().collect(Collectors.summingDouble(Double::doubleValue)));
        results.get(0).add(new AnyDimensionCellVo("", 1, axisYCount + 1, "汇总", 5));
        //合并X轴
        for (Integer i = axisYCount + 1; i < results.size(); i++) {
            results.get(i).add(new AnyDimensionCellVo("", 1, 1, String.format("%.2f", rowSummarys.get(i - axisYCount - 1)), 5));
            for (Integer j = i + 1; j < results.size(); j++) {
                if (results.get(i).get(0).getId().equals(results.get(j).get(0).getId())) {
                    for (Integer k = 0; k < axisXCount; k++) {
                        if (results.get(i).get(k).getId().equals(results.get(j).get(compareIndex).getId())) {
                            results.get(i).get(k).setRowspan(results.get(i).get(k).getRowspan() + 1);
                            results.get(j).remove(0);
                        } else {
                            compareIndex++;
                        }
                    }
                    compareIndex = 0;
                } else {
                    i = j;
                }
                results.get(j).add(new AnyDimensionCellVo("", 1, 1, String.format("%.2f", rowSummarys.get(j - axisYCount - 1)), 5));
            }
        }
        anyDimensionVo.setResults(results);
    }

    @Override
    public AnyDimensionVo query(Long analyzeId, Long cubeId) throws APIException {
        AnalyzeVo analyzeVo = getVo(analyzeId);
        return query(analyzeId, analyzeVo.getOlapAnalyzeAxes(), analyzeVo.getCreateId().toString(), analyzeVo.getSql(), 0, 0);
    }

    @Override
    public QueryResultMapper queryDimension(Long tableId, Long columnId, Long userId, String key, Integer offeset, Integer limit) throws APIException {
        if (limit == -1) {
            limit = Integer.MAX_VALUE;
        }
        OlapCubeTable cubeTable = olapCubeTableRepository.findById(tableId).get();
        OlapCubeTableColumn cubeTableColumn = olapCubeTableColumnRepository.findById(columnId).get();
        String sql = MessageFormat.format("select {0} from {1} where 1=1", cubeTableColumn.getColumnName(), cubeTable.getTableName());
        if (key != null && !key.equals("")) {
            String columnType = cubeTableColumn.getColumnType();
            if (columnType != null && !columnType.equals("")) {
                if (isStringType(columnType)) {
                    sql += MessageFormat.format(" and {0} like ''%{1}%''", cubeTableColumn.getColumnName(), key);
                } else {
                    sql += MessageFormat.format(" and cast({0} as varchar(20)) like ''%{1}%''", cubeTableColumn.getColumnName(), key);
                }
            }
        }
        sql += " group by " + cubeTableColumn.getColumnName();
        String countSql = MessageFormat.format("select count(*) from ({0})M", sql);
        QueryResultMapper countMapper = cubeHttpClient.query(countSql, 0, 100, userId.toString());
        QueryResultMapper mapper = cubeHttpClient.query(sql, offeset, limit, userId.toString());
        Integer count = Integer.parseInt(countMapper.results.get(0).get(0));
        mapper.setTotalRecord(count);
        return mapper;
    }

    @Override
    public AnyDimensionVo queryPaging(Integer pageIndex, Integer pageSize, Long cubeId, List<AnalyzeAxisVo> axises, String userId) throws APIException {
        return query(cubeId, axises, userId, null, pageIndex, pageSize);
    }

    @Override
    public AnyDimensionVo queryPaging(Integer pageIndex, Integer pageSize, Long analyzeId, Long cubeId) throws APIException {
        AnalyzeVo analyzeVo = getVo(analyzeId);
        return query(cubeId, analyzeVo.getOlapAnalyzeAxes(), analyzeVo.getCreateId().toString(), analyzeVo.getSql(), pageIndex, pageSize);
    }

    private boolean isStringType(String columnType) {
        switch (columnType.toUpperCase()) {
            case "DOUBLE":
            case "FlOAT":
            case "BIGINT":
            case "INT":
            case "SMALLINT":
            case "TINYINT": {
                return false;
            }
            default: {
                return true;
            }
        }
    }

    private boolean isPaging(Integer pageIndex, Integer pageSize) {
        if (pageIndex > 0 && pageSize > 0) {
            return true;
        }
        return false;
    }
}
