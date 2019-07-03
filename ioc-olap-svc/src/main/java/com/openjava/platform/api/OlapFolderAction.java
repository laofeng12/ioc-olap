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
import com.openjava.platform.domain.OlapFolder;
import com.openjava.platform.query.OlapFolderDBParam;
import com.openjava.platform.service.OlapFolderService;
import jxl.write.DateTime;
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
@Api(tags="文件夹接口")
@RestController
@RequestMapping("/olap/apis/olapFolder")
public class OlapFolderAction extends BaseAction{
	
	@Resource
	private OlapFolderService olapFolderService;

	/**
	 * 保存
	 */
	@ApiOperation(value = "保存", nickname="save", notes = "报文格式：content-type=application/json")
	@Security(session=true)
	@RequestMapping(method=RequestMethod.POST)
	public OlapFolder doSave(@RequestBody OlapFolder body) {
		Date date = new Date();
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		if(body.getIsNew() == null || body.getIsNew()) {
			SequenceService ss = ConcurrentSequence.getInstance();
			body.setFolderId(ss.getSequence());
			body.setIsNew(true);
			body.setCreateTime(date);
			body.setCreateId(Long.parseLong(userVO.getUserId()));
			body.setCreateName(userVO.getUserAccount());
			body.setFlags(0);
			OlapFolder dbObj = olapFolderService.doSave(body);
		} else {
			OlapFolder db = olapFolderService.get(body.getId());
			MyBeanUtils.copyPropertiesNotBlank(db, body);
			db.setIsNew(false);
			db.setUpdateTime(date);
			db.setCreateId(Long.parseLong(userVO.getUserId()));
			db.setCreateName(userVO.getUserAccount());
			olapFolderService.doSave(db);
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
		olapFolderService.doDelete(id);
	}
	
	@ApiOperation(value = "批量删除", nickname="remove")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "ids", value = "主键编码用,分隔", required = true, paramType = "post"),
	})
	@Security(session=true)
	@RequestMapping(value="/remove",method=RequestMethod.POST)
	public void doRemove(@RequestParam("ids")String ids) {
		olapFolderService.doRemove(ids);
	}

	@ApiOperation(value = "获取个人的文件夹列表", nickname="listWhthPerson")
	@Security(session=true)
	@RequestMapping(value="/listWhthPerson",method=RequestMethod.GET)
	public List<OlapFolder> listWhthPerson() {
		OaUserVO userVO = (OaUserVO) SsoContext.getUser();
		return olapFolderService.getListByCreateId(Long.parseLong(userVO.getUserId()));
	}

	@ApiOperation(value = "根据ID获取单个文件夹信息", nickname="folder")
	@Security(session=true)
	@RequestMapping(value="/folder",method=RequestMethod.GET)
	public  OlapFolder folder(Long id){
		return olapFolderService.get(id);
	}
}
