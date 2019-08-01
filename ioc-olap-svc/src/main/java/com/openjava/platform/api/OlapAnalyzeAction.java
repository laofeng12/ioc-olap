package com.openjava.platform.api;

import com.alibaba.fastjson.JSON;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.common.Export;
import com.openjava.platform.domain.*;
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
    public AnyDimensionVo query(Long analyzeId, Long cubeId) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        return olapAnalyzeService.query(analyzeId, cubeId, userVO.getUserId());
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

    @ApiOperation(value = "导出olap分析数据", nickname="exportWitholapAnalyze", notes = "报文格式：content-type=application/download")
    @RequestMapping(value="/export",method= RequestMethod.GET)
    //@Security(session=true)
    public void export(HttpServletResponse response) throws Exception {
        String date="{\"totalRows\":1,\"totalColumns\":6,\"results\":[[{\"colspan\":2,\"rowspan\":1,\"value\":\"null\",\"type\":1},{\"colspan\":4,\"rowspan\":1,\"value\":\"Bachelors Degree\",\"type\":1},{\"colspan\":4,\"rowspan\":1,\"value\":\"Graduate Degree\",\"type\":1},{\"colspan\":4,\"rowspan\":1,\"value\":\"High School Degree\",\"type\":1}],[{\"colspan\":2,\"rowspan\":1,\"value\":\"Customer - Gender - Gender\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"F\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"M\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"F\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"M\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"F\",\"type\":1},{\"colspan\":2,\"rowspan\":1,\"value\":\"M\",\"type\":1}],[{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Country\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store State\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Unit Sales\",\"type\":3},{\"colspan\":1,\"rowspan\":1,\"value\":\"Store Cost\",\"type\":3}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"USA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"CA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"USA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"OR\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"USA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"WA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"ESA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"CA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"ESA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"OR\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}],[{\"colspan\":1,\"rowspan\":3,\"value\":\"ESA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"WA\",\"type\":2},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"22.22\",\"type\":4},{\"colspan\":1,\"rowspan\":1,\"value\":\"33.33\",\"type\":4}]],\"cube\":\"\",\"affectedRowCount\":1,\"isException\":true,\"exceptionMessage\":14,\"duration\":2,\"totalScanCount\":2,\"totalScanBytes\":2}";
        AnyDimensionVo anyDimensionVo=JSON.parseObject(date,AnyDimensionVo.class);
        Export.dualAnyDimensionVoDate(anyDimensionVo,response);
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
                meareTreeVo.setAttrs(table);
                TreeVo dimTreeVo=new TreeVo();
                dimTreeVo.setId(table.getId().toString());
                dimTreeVo.setName(table.getName());
                dimTreeVo.setChildren(new ArrayList<TreeNodeVo>());
                dimTreeVo.setAttrs(table);
                ArrayList<OlapCubeTableColumn> cubeTableColumns=olapCubeTableColumnService.getListByTableId(table.getCubeTableId());
                for (OlapCubeTableColumn column : cubeTableColumns){
                    TreeNodeVo leafNode=new TreeNodeVo();
                    leafNode.setId(column.getId().toString());
                    leafNode.setAttrs(column);
                    if(column.getExpressionType()==null || column.getExpressionType()==""){
                        leafNode.setName(column.getName());
                        dimTreeVo.getChildren().add(leafNode);
                    }
                    else{
                        leafNode.setName(column.getName());
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
}
