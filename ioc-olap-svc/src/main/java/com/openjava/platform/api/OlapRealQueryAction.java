package com.openjava.platform.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.api.kylin.CubeAction;
import com.openjava.platform.common.Export;
import com.openjava.platform.domain.*;
import com.openjava.platform.dto.ShareUserDto;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import com.openjava.platform.service.*;
import com.openjava.platform.vo.QueryResultMapperVo;
import com.openjava.platform.vo.TreeNodeVo;
import com.openjava.platform.vo.TreeVo;
import io.swagger.annotations.*;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * api接口
 * @author xiepc
 *
 */
@Api(tags="即时查询接口")
@RestController
@RequestMapping("/olap/apis/olapRealQuery")
public class OlapRealQueryAction extends BaseAction {
	@Resource
	private OlapRealQueryService olapRealQueryService;
	@Resource
	private OlapCubeService olapCubeService;
	@Resource
	private OlapCubeTableService olapCubeTableService;
	@Resource
	private OlapCubeTableColumnService olapCubeTableColumnService;
	@Resource
	private OlapFolderService olapFolderService;
	@Resource
	private CubeAction cubeAction;
	@Resource
	private OlapShareService olapShareService;

	
	/**
	 * 用主键获取数据
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "根据ID获取", notes = "单个对象查询", nickname="id")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "主标识编码", required = true, dataType = "string", paramType = "path"),
	})
	@ApiResponses({
		@io.swagger.annotations.ApiResponse(code=20020, message="会话失效")
	})
	@Security(session=true)
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public OlapRealQuery get(@PathVariable("id")Long id) {
		OlapRealQuery m = olapRealQueryService.get(id);
		return m;
	}

	/**
	 * 保存
	 */
	@ApiOperation(value = "保存", nickname="save", notes = "报文格式：content-type=application/json")
	@Security(session=true)
	@RequestMapping(method=RequestMethod.POST)
	public OlapRealQuery doSave(@RequestBody OlapRealQuery body) {
		Date date = new Date();
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		if(body.getIsNew() == null || body.getIsNew()) {
			SequenceService ss = ConcurrentSequence.getInstance();
			body.setRealQueryId(ss.getSequence());
			body.setCreateTime(date);
			body.setCreateId(Long.parseLong(userVO.getUserId()));
			body.setCreateName(userVO.getUserAccount());
			body.setFlags(0);
			body.setIsNew(true);
			OlapRealQuery dbObj = olapRealQueryService.doSave(body);
		} else {
			OlapRealQuery db = olapRealQueryService.get(body.getId());
			MyBeanUtils.copyPropertiesNotBlank(db, body);
			db.setUpdateTime(date);
			db.setCreateId(Long.parseLong(userVO.getUserId()));
			db.setCreateName(userVO.getUserAccount());
			db.setIsNew(false);
			olapRealQueryService.doSave(db);
		}

		return body;
	}
	
	@ApiOperation(value = "删除", nickname="delete")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "主键编码", required = true, paramType = "post"),
	})
	@Security(session=true)
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void doDelete(@RequestParam("id")Long id) {
		olapRealQueryService.doDelete(id);
	}
	
	@ApiOperation(value = "批量删除", nickname="remove")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "主键编码用,分隔", required = true, paramType = "post"),
	})
	@Security(session=true)
	@RequestMapping(value="/remove",method=RequestMethod.POST)
	public void doRemove(@RequestParam("ids")String ids) {
		olapRealQueryService.doRemove(ids);
	}

	@ApiOperation(value = "获取自己创建的立方体树形结构列表")
	@Security(session=true)
	@RequestMapping(value="/CubeTree",method=RequestMethod.GET)
	public ArrayList<TreeVo> CubeTree(){
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		ArrayList<OlapCube> cubes=olapCubeService.getListByUserId(Long.parseLong(userVO.getUserId()));
		ArrayList<TreeVo> trees=new ArrayList<TreeVo>();
		for (OlapCube cube : cubes){
			TreeVo tree=new TreeVo();
			tree.setId(cube.getCubeId().toString());
			tree.setName(cube.getName());
			tree.setChildren(new ArrayList<TreeNodeVo>());
			ArrayList<OlapCubeTable> cubeTables=olapCubeTableService.getListByCubeId(cube.getCubeId());
			for (OlapCubeTable table : cubeTables){
				TreeNodeVo nodeVo=tree.getChildren().stream().filter(p->p.getName().equals(table.getTableName())).findFirst().orElse(null);;
				if(nodeVo==null){
					nodeVo=new TreeNodeVo();
					nodeVo.setId(table.getId().toString());
					nodeVo.setName(table.getTableName());
					nodeVo.setChildren(new ArrayList<TreeNodeVo>());
					tree.getChildren().add(nodeVo);
				}
				ArrayList<OlapCubeTableColumn> cubeTableColumns=olapCubeTableColumnService.getListByTableId(table.getCubeTableId());
				for (OlapCubeTableColumn column : cubeTableColumns){
					TreeNodeVo leafNode=nodeVo.getChildren().stream().filter(p->p.getName().equals(column.getColumnName())).findFirst().orElse(null);
					if(leafNode==null){
						leafNode=new TreeNodeVo();
						leafNode.setId(column.getId().toString());
						leafNode.setName(column.getColumnName());
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
	@Security(session=true)
	public QueryResultMapper query(String sql, Integer limit) {
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		//return cubeAction.query(sql,0,limit,userVO.getUserId());
		if(limit==-1){
			limit=Integer.MAX_VALUE;
		}
		return cubeAction.query(sql,0,limit,"learn_kylin");
	}

	@ApiOperation(value = "通过ID查询数据")
	@RequestMapping(value = "/queryById", method = RequestMethod.POST)
	@Security(session=true)
	public QueryResultMapperVo queryById(Long id) {
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		OlapRealQuery m = olapRealQueryService.get(id);
		QueryResultMapper mapper=cubeAction.query(m.getSql(),0,m.getLimit(),"learn_kylin");//获取数据
		List<ShareUserDto> shareList=olapShareService.getList("RealQuery", String.valueOf(id), Long.valueOf(userVO.getUserId()));
		QueryResultMapperVo mapperVo=new QueryResultMapperVo();
		MyBeanUtils.copyPropertiesNotBlank(mapperVo, mapper);//复制mapper属性到VO中
		mapperVo.setShareList(shareList);
		return mapperVo;
	}

	@ApiOperation(value = "获取层级文件夹结构")
	@RequestMapping(value = "/folderWithQuery", method = RequestMethod.GET)
	@Security(session=true)
	public List<TreeVo> folderWithQuery() {
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		List<TreeVo> trees=new ArrayList<TreeVo>();
		List<OlapFolder> folders=olapFolderService.getListByTypeAndCreateId(Long.parseLong(userVO.getUserId()),"RealQuery");
		for (OlapFolder folder : folders){
			TreeVo tree=new TreeVo(folder.getName(),folder.getFolderId().toString(),new ArrayList<TreeNodeVo>(),folder);
			List<OlapRealQuery> olapRealQueries=olapRealQueryService.getListWithFolderId(folder.getFolderId());
			for(OlapRealQuery realQuery : olapRealQueries){
				tree.getChildren().add(new TreeNodeVo(realQuery.getName(),realQuery.getRealQueryId().toString(),null,realQuery));
			}
			trees.add(tree);
		}
		return trees;
	}

	@ApiOperation(value = "获取共享的即时查询")
	@RequestMapping(value = "/queryShare", method = RequestMethod.GET)
	@Security(session=true)
	public List<OlapRealQuery> queryShare() {
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		return olapRealQueryService.getAllShares(Long.parseLong(userVO.getUserId()));
	}

	@ApiOperation(value = "导出即时查询", nickname="export", notes = "报文格式：content-type=application/download")
	@RequestMapping(value="/export",method= RequestMethod.GET)
	//@Security(session=true)
	public void export(String sql,Integer limit, HttpServletResponse response)
	{
		QueryResultMapper mapper=cubeAction.query(sql,0,limit,"learn_kylin");
		Export.dualDate(mapper,response);
	}


}
