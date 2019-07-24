package com.openjava.platform.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.domain.*;
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
    public ArrayList<FolderHierarchicalVo<OlapAnalyze>> folderWithQuery() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        ArrayList<FolderHierarchicalVo<OlapAnalyze>> folderHierarchicalVos = new ArrayList<FolderHierarchicalVo<OlapAnalyze>>();
        List<OlapFolder> folders = olapFolderService.getListByTypeAndCreateId(Long.parseLong(userVO.getUserId()), "DataAnalyze");
        for (OlapFolder folder : folders) {
            FolderHierarchicalVo<OlapAnalyze> folderHierarchicalVo = new FolderHierarchicalVo<OlapAnalyze>();
            MyBeanUtils.copyPropertiesNotBlank(folderHierarchicalVo, folder);
            folderHierarchicalVo.setLeafs(olapAnalyzeService.getListWithFolderId(folder.getFolderId()));
            folderHierarchicalVos.add(folderHierarchicalVo);
        }
        return folderHierarchicalVos;
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
    public AnalyzeVo save(AnalyzeVo analyzeVo) throws APIException {
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
            analyzeVo.setUpdateTime(date);
            analyzeVo.setCreateId(Long.parseLong(userVO.getUserId()));
            analyzeVo.setCreateName(userVO.getUserAccount());
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
}
