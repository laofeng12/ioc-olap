package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.query.OlapCubeDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapCubeService {
	Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable);

	OlapCube findTableInfo(String cubeName, Long createId);

	OlapCube findTableInfo(String cubeName);

	List<OlapCube> getOlapShareByShareUserId(String shareUserId);

	List<OlapCube> queryDataOnly(OlapCubeDBParam params, Pageable pageable);

	OlapCube get(Long id);

	OlapCube doSave(OlapCube m);

	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapCube> findByUserId(Long createId);

	List<OlapCube> findAll();

    ArrayList<OlapCube> getValidListByUserId(Long userId);

	//保存OLAP_CUBE表
	OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO);
}
