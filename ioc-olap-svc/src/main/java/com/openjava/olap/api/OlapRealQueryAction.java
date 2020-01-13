package com.openjava.olap.api;

import com.openjava.admin.component.IocAuthorizationToken;
import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.common.*;
import com.openjava.olap.common.kylin.CubeHttpClient;
import com.openjava.olap.domain.*;
import com.openjava.olap.dto.ShareUserDto;
import com.openjava.olap.mapper.CustomApiMapper;
import com.openjava.olap.mapper.kylin.QueryResultMapper;
import com.openjava.olap.service.*;
import com.openjava.olap.vo.QueryResultMapperVo;
import com.openjava.olap.vo.TreeNodeVo;
import com.openjava.olap.vo.TreeVo;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.exception.APIException;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.plugin.sys.vo.UserVO;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * api接口
 *
 * @author xiepc
 */
@Api(tags = "即时查询接口")
@RestController
@RequestMapping("/olap/apis/olapRealQuery")
public class OlapRealQueryAction extends BaseAction {
    @Resource
    private OlapRealQueryService olapRealQueryService;
    @Resource
    private OlapCubeService olapCubeService;
    @Resource
    private com.openjava.olap.service.OlapCubeTableService olapCubeTableService;
    @Resource
    private OlapCubeTableColumnService olapCubeTableColumnService;
    @Resource
    private OlapFolderService olapFolderService;
    @Autowired
    private CubeHttpClient cubeHttpClient;
    @Resource
    private OlapShareService olapShareService;
    @Resource
    private IocAuthorizationToken iocAuthorizationToken;
    @Autowired
    private GateWayHttpClient gateWayHttpClient;

    /**麒麟查找hive的数据库名**/
    @Value("${olap.kylin.databaseName:olap}")
    private String databaseName;

    @Resource
    private AuditComponentProxy auditComponentProxy;


    /**
     * 用主键获取数据
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID获取", notes = "单个对象查询", nickname = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主标识编码", required = true, dataType = "string", paramType = "path"),
    })
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20020, message = "会话失效")
    })
    @Security(session = true, allowResources = {"OlapRealQuery"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OlapRealQuery get(@PathVariable("id") Long id) throws APIException {
        OlapRealQuery m = olapRealQueryService.get(id);
        return m;
    }

    /**
     * 保存
     */
    @ApiOperation(value = "保存", nickname = "save", notes = "报文格式：content-type=application/json")
    @Security(session = true, allowResources = {"OlapRealQuery"})
    @RequestMapping(method = RequestMethod.POST)
    public OlapRealQuery doSave(@RequestBody OlapRealQuery body) throws APIException {
        Date date = new Date();
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        if (body.getCubeName() ==null || "".equals(body.getCubeName())){
            throw new APIException(400,"模型名称不能为空");
        }
        if (body.getIsNew() == null || body.getIsNew()) {
            SequenceService ss = ConcurrentSequence.getInstance();
            body.setRealQueryId(ss.getSequence());
            body.setCreateTime(date);
            body.setCreateId(Long.parseLong(userVO.getUserId()));
            body.setCreateName(userVO.getUserAccount());
            body.setFlags(0);
            body.setIsNew(true);
            OlapRealQuery dbObj = olapRealQueryService.doSave(body);
            //新建查询-保存结果
            AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY,AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVE_QUERY,
                AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVE_QUERY_PERSIST_RESULT, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                new ArrayList<Object>(){
                    {
                        add(body);
                    }
                },new ArrayList<Object>(){{add(dbObj);}});
            this.auditComponentProxy.saveAudit(param);
        } else {
            OlapRealQuery db = olapRealQueryService.get(body.getId());
            MyBeanUtils.copyPropertiesNotBlank(db, body);
            db.setUpdateTime(date);
            db.setCreateId(Long.parseLong(userVO.getUserId()));
            db.setCreateName(userVO.getUserAccount());
            db.setIsNew(false);
            olapRealQueryService.doSave(db);
            //已保存结果-编辑
            AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
                AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY,AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVED_RESULT,
                AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_EDIT, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
                new ArrayList<Object>(){
                    {
                        add(body);
                    }
                },new ArrayList<Object>(){{add(db);}});
            this.auditComponentProxy.saveAudit(param);
        }

        return body;
    }

    @ApiOperation(value = "删除", nickname = "delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键编码", required = true, paramType = "post"),
    })
    @Security(session = true, allowResources = {"OlapRealQuery"})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void doDelete(@RequestParam("id") Long id) {
        olapRealQueryService.doDelete(id);
    }

    @ApiOperation(value = "批量删除", nickname = "remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "主键编码用,分隔", required = true, paramType = "post"),
    })
    @Security(session = true, allowResources = {"OlapRealQuery"})
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public void doRemove(@RequestParam("ids") String ids) {
        olapRealQueryService.doRemove(ids);
    }

    @ApiOperation(value = "获取自己创建的立方体树形结构列表")
    @Security(session = true, allowResources = {"OlapRealQuery"})
    @RequestMapping(value = "/CubeTree", method = RequestMethod.GET)
    public ArrayList<TreeVo> CubeTree() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        ArrayList<OlapCube> cubes = olapCubeService.getValidListByUserId(Long.parseLong(userVO.getUserId()));
        ArrayList<TreeVo> trees = new ArrayList<TreeVo>();
        for (OlapCube cube : cubes) {
            TreeVo tree = new TreeVo();
            tree.setId(cube.getCubeId().toString());
            tree.setName(cube.getName());
            tree.setChildren(new ArrayList<TreeNodeVo>());
            ArrayList<OlapCubeTable> cubeTables = olapCubeTableService.getListByCubeId(cube.getCubeId());
            for (OlapCubeTable table : cubeTables) {
                TreeNodeVo nodeVo = tree.getChildren().stream().filter(p -> p.getName().equals(table.getTableName())).findFirst().orElse(null);

                if (nodeVo == null) {
                    nodeVo = new TreeNodeVo();
                    nodeVo.setId(table.getId().toString());
                    nodeVo.setName(table.getTableName());//真实表名，两个同时返回，不需要替换
                    nodeVo.setVirtualTableName(table.getVirtualTableName());//虚拟表名
                    nodeVo.setChildren(new ArrayList<TreeNodeVo>());
                    tree.getChildren().add(nodeVo);
                }
                ArrayList<OlapCubeTableColumn> cubeTableColumns = olapCubeTableColumnService.getListByTableId(table.getCubeTableId());
                for (OlapCubeTableColumn column : cubeTableColumns) {
                    TreeNodeVo leafNode = nodeVo.getChildren().stream().filter(p -> p.getName().equals(column.getColumnName())).findFirst().orElse(null);
                    if (leafNode == null) {
                        leafNode = new TreeNodeVo();
                        leafNode.setId(column.getId().toString());
                        if (column.getExpressionType() != null && !column.getExpressionType().equals("")) {
                            leafNode.setName(column.getColumnName() + "->" + column.getExpressionType());
                        } else {
                            leafNode.setName(column.getColumnName());
                        }

                        nodeVo.getChildren().add(leafNode);
                    }
                }
            }
            trees.add(tree);
        }
        return trees;
    }

    @ApiOperation(value = "查询数据")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public QueryResultMapper query(
        @ApiParam(value = "查询SQL语句",name = "sql",required = true)@RequestParam String sql,
        @ApiParam(value = "限制行数",name = "limit")@RequestParam(required = false) Integer limit,
        @ApiParam(value = "模型名称",name = "cubeName",required = true)@RequestParam String cubeName) throws Exception {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        OlapCube record = this.olapCubeService.findTableInfo(cubeName);
        if (record == null){
            throw new APIException(400,"查询不到关联的模型");
        }
        if (limit == null || limit == -1) {
            limit = Integer.MAX_VALUE;
        }
        // 统一给查询表名或join表名加上前缀数据库名
        // 主要加库名
        sql = StringEscapeUtils.unescapeHtml(sql);
        String formatSql = formatSql(sql,true);
        //已保存结果-查询数据
        //新建查询-查询，两个一级标题联合了
        AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
            AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY,AuditLogEnum.LOG_PRIMARY_TITLE_UNITE_SAVE_QUERY_SAVED_RESULT,
            AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_QUERY_DATA, AuditLogEnum.AuditLogEvent.LOG_EVENT_QUERY,
            new ArrayList<Object>(){
                {
                    add(formatSql);
                }
            },new ArrayList<Object>(){{}});
        this.auditComponentProxy.saveAudit(param);
        try {
            return cubeHttpClient.query(formatSql, 0, limit, record.getCreateId().toString());
        } catch (Exception ex) {
            throw new APIException(400, "查询失败！");
        }
    }

    @ApiOperation(value = "通过ID查询数据")
    @RequestMapping(value = "/queryById", method = RequestMethod.POST)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public QueryResultMapperVo queryById(Long id) throws APIException {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        OlapRealQuery m = olapRealQueryService.get(id);
        if (m.getSql() != null){
            m.setSql(formatSql(m.getSql(),true));
        }
        OlapCube cube = this.olapCubeService.findTableInfo(m.getCubeName());
        if (cube == null){
            throw new APIException(400,"查询不到该记录对应的模型，请检查该模型是否有效");
        }
        QueryResultMapper mapper;
        try {
            mapper = cubeHttpClient.query(m.getSql(), 0, m.getLimit(), cube.getCreateId().toString());//获取数据
        } catch (Exception ex) {
            throw new APIException(400, "查询失败！");
        }
        List<ShareUserDto> shareList = olapShareService.getList("RealQuery", String.valueOf(id), Long.valueOf(userVO.getUserId()));
        QueryResultMapperVo mapperVo = new QueryResultMapperVo();
        MyBeanUtils.copyPropertiesNotBlank(mapperVo, mapper);//复制mapper属性到VO中
        mapperVo.setShareList(shareList);
        return mapperVo;
    }

    @ApiOperation(value = "获取层级文件夹结构")
    @RequestMapping(value = "/folderWithQuery", method = RequestMethod.GET)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public List<TreeVo> folderWithQuery() throws Exception{
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        List<TreeVo> trees = new ArrayList<TreeVo>();
        List<OlapFolder> folders = olapFolderService.getListByTypeAndCreateId(Long.parseLong(userVO.getUserId()), "RealQuery");
        for (OlapFolder folder : folders) {
            TreeVo tree = new TreeVo(folder.getName(), folder.getFolderId().toString(), new ArrayList<TreeNodeVo>(), folder);
            List<OlapRealQuery> olapRealQueries = olapRealQueryService.getListWithFolderId(folder.getFolderId());
            for (OlapRealQuery realQuery : olapRealQueries) {
                if (realQuery.getSql() != null){
                    //用户看到的select语句，不需要显示库名
                    realQuery.setSql(formatSql(realQuery.getSql(),false));
                }
                tree.getChildren().add(new TreeNodeVo(realQuery.getName(), realQuery.getRealQueryId().toString(), null, realQuery));
            }
            trees.add(tree);
        }
        return trees;
    }

    @ApiOperation(value = "获取共享的即时查询")
    @RequestMapping(value = "/queryShare", method = RequestMethod.GET)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public List<OlapRealQuery> queryShare() {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        return olapRealQueryService.getAllShares(Long.parseLong(userVO.getUserId()));
    }

    @ApiOperation(value = "导出即时查询", nickname = "export", notes = "报文格式：content-type=application/download")
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public void export(@RequestParam String sql, Integer limit, @RequestParam String cubeName, HttpServletResponse response) throws Exception {
        try {
            OlapCube cube = this.olapCubeService.findTableInfo(cubeName);
            if (cube == null){
                throw new APIException(400,"查询不到该记录对应的模型，请检查该模型是否有效");
            }
            String formatSql = formatSql(sql,true);
            QueryResultMapper mapper = cubeHttpClient.query(formatSql, 0, limit, cube.getCreateId().toString());
            Export.dualDate(mapper, response);
        } catch (Exception ex) {
            throw new APIException(400, "导出失败！");
        }
        //已保存结果-导出结果
        // 新建查询-导出结果 共用
        AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),(UserVO)SsoContext.getUser(), AuditLogEnum.LOG_SERVICE_NAME,
            AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY,AuditLogEnum.LOG_PRIMARY_TITLE_UNITE_SAVE_QUERY_SAVED_RESULT,
            AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_EXPORT_RESULT, AuditLogEnum.AuditLogEvent.LOG_EVENT_EXPORT,
            new ArrayList<Object>(){
                {
                    add(sql);
                    add(limit);
                    add(cubeName);
                }
            },new ArrayList<Object>(){{}});
        this.auditComponentProxy.saveAudit(param);
    }

    @ApiOperation(value = "发布接口", nickname = "publish", notes = "报文格式：content-type=application/download")
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public void publish(@RequestBody CustomApiMapper body) throws Exception {
        if (StringUtils.isBlank(body.getApiMethod()) || StringUtils.isBlank(body.getApiName())
                || StringUtils.isBlank(body.getApiPaths()) || StringUtils.isBlank(body.getEnctype())) {
            throw new APIException(400, "必填参数为空！");
        }
        body.setModuleType(gateWayHttpClient.REALQUERY_MODULE_TYPE);
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String token = iocAuthorizationToken.generateAesToken(userVO);
        gateWayHttpClient.registerApi(body, token);
        // 即席查询-已保存结果-发布接口
        AuditLogParam param = new AuditLogParam(SsoContext.getRequestId(),userVO, AuditLogEnum.LOG_SERVICE_NAME,
            AuditLogEnum.LOG_MODULES_REAL_TIME_QUERY,AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_PRIMARY_SAVED_RESULT,
            AuditLogEnum.LOG_REAL_TIME_QUERY_TITLE_LEVEL_SECONDARY_SAVED_RESULT_PUBLISH, AuditLogEnum.AuditLogEvent.LOG_EVENT_MANAGE,
            new ArrayList<Object>(){
                {
                    add(body);
                }
            },new ArrayList<Object>(){{}});
        this.auditComponentProxy.saveAudit(param);
    }

    @ApiOperation(value = "查看发布接口")
    @RequestMapping(value = "/publish/{realQueryId}", method = RequestMethod.GET)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public CustomApiMapper publish(@PathVariable Long realQueryId) throws Exception {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String token = iocAuthorizationToken.generateAesToken(userVO);
        CustomApiMapper mapper = gateWayHttpClient.get(realQueryId, gateWayHttpClient.REALQUERY_MODULE_TYPE, token);
        OlapRealQuery realQuery = olapRealQueryService.get(realQueryId);
        if (mapper == null || mapper.getCustomApiId() == null) {
            mapper = new CustomApiMapper();
            mapper.setApiMethod("GET");
            mapper.setApiName(realQuery.getName());
            mapper.setApiPaths("/olap/apis/olapRealQuery/query/" + realQueryId.toString());
            mapper.setCustomApiId(realQueryId);
            mapper.setEnctype("application/json");
            mapper.setApiProtocols("Http");
        }
        mapper.setModuleType(gateWayHttpClient.REALQUERY_MODULE_TYPE);
        mapper.setModuleTypeName("即席查询");
        return mapper;
    }

    @ApiOperation(value = "删除发布接口")
    @RequestMapping(value = "/publish/{realQueryId}", method = RequestMethod.DELETE)
    @Security(session = true, allowResources = {"OlapRealQuery"})
    public void deletePublish(@PathVariable Long realQueryId) throws Exception {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        String token = iocAuthorizationToken.generateAesToken(userVO);
        gateWayHttpClient.delete(realQueryId, gateWayHttpClient.REALQUERY_MODULE_TYPE, token);
    }

    @ApiOperation(value = "查询数据-对外")
    @RequestMapping(value = "/query/{realQueryId}", method = RequestMethod.GET)
    public QueryResultMapper query(@PathVariable Long realQueryId) throws APIException {
        OlapRealQuery realQuery = olapRealQueryService.get(realQueryId);
        try {
            return cubeHttpClient.query(realQuery.getSql(), 0, realQuery.getLimit(), realQuery.getCreateId().toString());
        } catch (Exception ex) {
            throw new APIException(400, "查询失败！");
        }
    }


    /**
     * 动态加上或移除olap库名
     * @param sql
     * @param databaseNameVisible true:加上库名，false:去掉库名
     * @return
     * @throws APIException
     */
    private String formatSql(String sql,boolean databaseNameVisible)throws APIException{
        if (sql == null || "".equalsIgnoreCase(sql)){
            throw new APIException("sql不能为空");
        }
        String[] str = sql.split("\\s+");//按照空格分隔
        StringBuilder sb = new StringBuilder();
        if (databaseNameVisible) {
            for (int i = 0; i < str.length; i++) {
                if (str[i].equalsIgnoreCase("from")
                    || str[i].equalsIgnoreCase("join")) {
                    sb.append(str[i]).append(" ").append(this.databaseName).append(".");
                } else sb.append(str[i]).append(" ");
            }
        }else {
            sql = sql.replaceAll(this.databaseName+".","");
            return sql;
        }
        return sb.toString();
    }

}
