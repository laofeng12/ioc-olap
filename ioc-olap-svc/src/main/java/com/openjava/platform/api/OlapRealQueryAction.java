package com.openjava.platform.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.domain.OlapRealQuery;
import com.openjava.platform.query.OlapRealQueryDBParam;
import com.openjava.platform.service.OlapRealQueryService;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.common.bean.MyBeanUtils;
import org.ljdp.common.file.ContentType;
import org.ljdp.common.file.POIExcelBuilder;
import org.ljdp.component.result.ApiResponse;
import org.ljdp.component.result.BasicApiResponse;
import org.ljdp.component.result.DataApiResponse;
import org.ljdp.component.sequence.SequenceService;
import org.ljdp.component.sequence.TimeSequence;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.ljdp.ui.bootstrap.TablePage;
import org.ljdp.ui.bootstrap.TablePageImpl;
import org.ljdp.util.DateFormater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;


/**
 * api接口
 * @author xiepc
 *
 */
@Api(tags="即时查询接口")
@RestController
@RequestMapping("/pds/olap/olapRealQuery")
public class OlapRealQueryAction extends BaseAction {
	
	@Resource
	private OlapRealQueryService olapRealQueryService;
	
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

	//TODO 获取树形CUBE结构


	//TODO 获取CUBE查询数据


	//TODO 获取层级文件夹结构


	//TODO 获取共享的查询



	
}
