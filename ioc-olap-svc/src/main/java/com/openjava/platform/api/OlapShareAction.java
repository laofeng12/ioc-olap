package com.openjava.platform.api;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.domain.OlapRealQuery;
import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.dto.ShareUserDto;
import com.openjava.platform.service.OlapCubeService;
import com.openjava.platform.service.OlapShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.secure.annotation.Security;
import org.ljdp.secure.sso.SsoContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "共享接口")
@RestController
@RequestMapping("/olap/apis/olapShare")
public class OlapShareAction {
    @Resource
    private OlapShareService olapShareService;

    @ApiOperation(value = "保存共享")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Security(session = true)
    public void save(Long[] userIds, String sourceType, Long sourceId, String cubeName) {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        if (StringUtils.isNotBlank(cubeName)) {
            olapShareService.save(userIds, sourceType, sourceId, Long.parseLong(userVO.getUserId()), userVO.getUserName(), cubeName);
        } else {
            olapShareService.save(userIds, sourceType, sourceId, Long.parseLong(userVO.getUserId()), userVO.getUserName());
        }
    }

    @ApiOperation(value = "读取共享")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @Security(session = true)
    public List<ShareUserDto> get(String sourceType, String sourceId, String cubeName) {
        OaUserVO userVO = (OaUserVO) SsoContext.getUser();
        if (StringUtils.isNotBlank(cubeName)) {
            return olapShareService.getList(sourceType, sourceId, Long.parseLong(userVO.getUserId()), cubeName);
        } else {
            return olapShareService.getList(sourceType, sourceId, Long.parseLong(userVO.getUserId()));
        }
    }
}
