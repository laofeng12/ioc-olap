package com.openjava.olap.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.*;
import com.openjava.olap.mapper.kylin.CubeDatalaketableNewMapper;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.mapper.kylin.MeasureMapper;
import com.openjava.olap.mapper.kylin.ModelsDescDataMapper;
import com.openjava.olap.query.OlapCubeDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 *
 * @author xiepc
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

	void deleteCubeName(String cubeName);

	void doRemove(String ids);

	List<OlapCube> findByUserId(Long createId);

	List<OlapCube> findAll();

	ArrayList<OlapCube> getValidListByUserId(Long userId);

	//保存OLAP_CUBE表
	OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO, Long dimensionLength, Long dimensionFiledLength, Long measureFiledLength,String graphData);

	boolean saveTable(OlapCube olapCube, List<OlapCubeTable> cubeTablesList, List<OlapCubeTableRelation> olapcubeList,
					  List<CubeDatalaketableNewMapper> cubeDatalaketableNew, CubeDescMapper cube, ModelsDescDataMapper modelDescData,
					  OlapTimingrefresh timingreFresh, Date date, OaUserVO userVO, List<OlapFilterCondidion> condidions, ArrayList<MeasureMapper> countMappers) throws Exception;

	void saveTableClone(OlapCube olapCube, ArrayList<OlapCubeTable> cubeTablesList, ArrayList<OlapCubeTableColumn> findByColumn,
						OlapFilter findTableInfo, ArrayList<OlapCubeTableRelation> olapcubeList, OlapTimingrefresh olapTimingrefresh,
						List<OlapDatalaketable> datalaketables, OaUserVO loginUser, String cloneCubeName);
}
