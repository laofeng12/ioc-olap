package com.openjava.platform.api;

import com.alibaba.fastjson.JSON;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.common.Export;
import com.openjava.platform.domain.*;
import com.openjava.platform.dto.ShareUserDto;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import com.openjava.platform.service.*;
import com.openjava.platform.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "OLAP分析接口")
@RestController
@RequestMapping("/olap/apis/olapAnalyze")
public class OlapAnalyzeAction {

    @Resource
    private OlapCubeService olapCubeService;
    @Resource
    private OlapCubeTableService olapCubeTableService;
    @Resource
    private OlapCubeTableColumnService olapCubeTableColumnService;
    @Resource
    private OlapFolderService olapFolderService;
    @Resource
    private OlapAnalyzeService olapAnalyzeService;
    @Resource
    private CubeAction cubeAction;
    @Resource
    private OlapShareService olapShareService;

    @ApiOperation(value = "获取层级文件夹结构")
    @RequestMapping(value = "/folderWithQuery", method = RequestMethod.GET)
    @Security(session = true)
    public List<TreeVo> folderWithQuery() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        List<TreeVo> trees=new ArrayList<TreeVo>();
        List<OlapFolder> folders = olapFolderService.getListByTypeAndCreateId(Long.parseLong(userVO.getUserId()), "Analyze");
        for (OlapFolder folder : folders){
            TreeVo tree=new TreeVo(folder.getName(),folder.getFolderId().toString(),new ArrayList<TreeNodeVo>(),folder);
            List<OlapAnalyze> olapAnalyzes=olapAnalyzeService.getListWithFolderId(folder.getFolderId());
            for(OlapAnalyze analyze : olapAnalyzes){
                tree.getChildren().add(new TreeNodeVo(analyze.getName(),analyze.getAnalyzeId().toString(),null,analyze));
            }
            trees.add(tree);
        }
        return trees;
    }

    @ApiOperation(value = "获取共享的OLAP分析")
    @RequestMapping(value = "/queryShare", method = RequestMethod.GET)
    @Security(session = true)
    public List<OlapAnalyze> queryShare() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        return olapAnalyzeService.getAllShares(Long.parseLong(userVO.getUserId()));
    }

    @ApiOperation(value = "查询已构建好的OLAP分析数据")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @Security(session = true)
    public AnyDimensionShareVo query(Long analyzeId, Long cubeId) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        AnyDimensionShareVo shareVo=new AnyDimensionShareVo();
        AnyDimensionVo dimensionVo=olapAnalyzeService.query(analyzeId, cubeId, userVO.getUserId());
        MyBeanUtils.copyPropertiesNotBlank(shareVo, dimensionVo);
        List<ShareUserDto> shareList=olapShareService.getList("RealQuery", String.valueOf(analyzeId), Long.valueOf(userVO.getUserId()));
        shareVo.setShareList(shareList);
        return shareVo;
    }

    @ApiOperation(value = "直接查询OLAP分析的数据")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @Security(session = true)
    public AnyDimensionVo query(Long cubeId, @RequestBody List<AnalyzeAxisVo> axises) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        return olapAnalyzeService.query(cubeId, axises, userVO.getUserId());
    }

    @ApiOperation(value = "保存OLAP分析接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Security(session = true)
    public AnalyzeVo save(@RequestBody AnalyzeVo analyzeVo) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        Date date = new Date();
        if (analyzeVo.getIsNew() == null || analyzeVo.getIsNew()) {
            SequenceService ss = ConcurrentSequence.getInstance();
            analyzeVo.setAnalyzeId(ss.getSequence());
            analyzeVo.setCreateTime(date);
            analyzeVo.setCreateId(Long.parseLong(userVO.getUserId()));
            analyzeVo.setCreateName(userVO.getUserAccount());
            analyzeVo.setFlags(0);
            analyzeVo.setIsNew(true);
            AnalyzeVo dbObj = olapAnalyzeService.save(analyzeVo);
        } else {
            OlapAnalyze db = olapAnalyzeService.get(analyzeVo.getId());
            analyzeVo.setCreateId(db.getCreateId());
            analyzeVo.setCreateName(db.getCreateName());
            analyzeVo.setCreateTime(db.getCreateTime());
            analyzeVo.setUpdateId(Long.parseLong(userVO.getUserId()));
            analyzeVo.setUpdateName(userVO.getUserAccount());
            analyzeVo.setUpdateTime(date);
            analyzeVo.setIsNew(false);
            olapAnalyzeService.save(analyzeVo);
        }
        return analyzeVo;
    }

    @ApiOperation(value = "获取指定的OLAP分析接口")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @Security(session = true)
    public AnalyzeVo get(Long id) {
        return olapAnalyzeService.getVo(id);
    }

    @ApiOperation(value = "导出已保存的数据", nickname="exportWitholapAnalyze", notes = "报文格式：content-type=application/download")
    @RequestMapping(value="/exportExist",method= RequestMethod.POST)
    @Security(session=true)
    public void export(Long analyzeId, Long cubeId, HttpServletResponse response) throws Exception  {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        AnyDimensionVo dimensionVo=(AnyDimensionShareVo)olapAnalyzeService.query(analyzeId, cubeId, userVO.getUserId());
        Export.dualAnyDimensionVoDate(dimensionVo,response);
    }

    @ApiOperation(value = "直接导出数据", nickname="exportWitholapAnalyze", notes = "报文格式：content-type=application/download")
    @RequestMapping(value="/export",method= RequestMethod.POST)
    @Security(session=true)
    public void export(Long cubeId, @RequestBody List<AnalyzeAxisVo> axises,HttpServletResponse response) throws Exception {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        AnyDimensionVo dimensionVo=olapAnalyzeService.query(cubeId, axises, userVO.getUserId());
        Export.dualAnyDimensionVoDate(dimensionVo,response);
    }

    @ApiOperation(value = "获取当前登录人立方体、指标、维度数据")
    @RequestMapping(value = "/Cubes", method = RequestMethod.GET)
    @Security(session = true)
    public List<AnalyzeCubeVo> Cubes() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        List<OlapCube> cubes=olapCubeService.getListByUserId(Long.parseLong(userVO.getUserId()));
        List<AnalyzeCubeVo> analyzeCubes=new ArrayList<AnalyzeCubeVo>();
        List<TreeVo> measures,dimensions;
        AnalyzeCubeVo analyzeCube;
        for (OlapCube cube : cubes){
            analyzeCube=new AnalyzeCubeVo();
            MyBeanUtils.copyPropertiesNotBlank(analyzeCube, cube);
            measures=new ArrayList<TreeVo>();
            dimensions=new ArrayList<TreeVo>();
            ArrayList<OlapCubeTable> cubeTables=olapCubeTableService.getListByCubeId(cube.getCubeId());
            for (OlapCubeTable table : cubeTables){
                TreeVo meareTreeVo=new TreeVo();
                meareTreeVo.setId(table.getId().toString());
                meareTreeVo.setName(table.getName());
                meareTreeVo.setChildren(new ArrayList<TreeNodeVo>());
                TreeVo dimTreeVo=new TreeVo();
                dimTreeVo.setId(table.getId().toString());
                dimTreeVo.setName(table.getName());
                dimTreeVo.setChildren(new ArrayList<TreeNodeVo>());
                ArrayList<OlapCubeTableColumn> cubeTableColumns=olapCubeTableColumnService.getListByTableId(table.getCubeTableId());
                for (OlapCubeTableColumn column : cubeTableColumns){
                    TreeNodeVo leafNode=new TreeNodeVo();
                    AnalyzeAxisVo axisVo=new AnalyzeAxisVo();
                    axisVo.setExpressionFull(column.getExpressionFull());
                    axisVo.setExpressionType(column.getExpressionType());
                    axisVo.setColumnAlias(column.getColumnAlias());
                    axisVo.setColumnChName(column.getName());
                    axisVo.setColumnName(column.getColumnName());
                    axisVo.setIsDict(table.getIsDict());
                    axisVo.setTableAlias(table.getTableAlias());
                    axisVo.setTableName(table.getTableName());
                    axisVo.setCubeId(table.getCubeId());
                    axisVo.setName(table.getName());
                    axisVo.setColumnId(column.getCubeTableColumnId());
                    axisVo.setTableId(column.getTableId());
                    leafNode.setId(column.getId().toString());
                    leafNode.setAttrs(axisVo);
                    leafNode.setName(column.getName());
                    if(column.getExpressionType()==null || column.getExpressionType()==""){
                        dimTreeVo.getChildren().add(leafNode);
                    }
                    else{
                        meareTreeVo.getChildren().add(leafNode);
                    }
                }
                if(meareTreeVo.getChildren().size()>0){
                    measures.add(meareTreeVo);
                }
                if(dimTreeVo.getChildren().size()>0){
                    dimensions.add(dimTreeVo);
                }
            }
            analyzeCube.setDimensures(dimensions);
            analyzeCube.setMeasures(measures);
            analyzeCubes.add(analyzeCube);
        }
        return analyzeCubes;
    }

    @ApiOperation(value = "获取维度表某列数据")
    @RequestMapping(value = "/queryDimension", method = RequestMethod.GET)
    @Security(session = true)
    public QueryResultMapper queryDimension(Long tableId, Long columnId,String key,Integer pageIndex,Integer pageSize) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        Integer offeset=(pageIndex-1)*pageSize;
        return olapAnalyzeService.queryDimension(tableId,columnId,Long.parseLong(userVO.getUserId()),key,offeset,pageSize);
    }
}
