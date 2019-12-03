package com.openjava.olap.service;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.*;
import com.openjava.olap.mapper.kylin.*;
import com.openjava.olap.query.OlapCubeDBParam;
import com.openjava.olap.vo.ShareCubeVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件夹表业务层接口
 *
 * @author xiepc
 */
public interface OlapCubeService {
	Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable);

	OlapCube findTableInfo(String cubeName, Long createId);

	OlapCube findTableInfo(String cubeName);

	/**
	 * 只返回必要的属性，然后再去查询麒麟接口
	 * @param shareUserId
	 * @return
	 */
	List<ShareCubeVo> getOlapShareByShareUserId(String shareUserId);

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
	OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO, Long dimensionLength, Long dimensionFiledLength, Long measureFiledLength, String graphData);

	boolean saveTable(OlapCube olapCube, List<OlapCubeTable> cubeTablesList, List<OlapCubeTableRelation> olapcubeList,
					  List<CubeDatalaketableNewMapper> cubeDatalaketableNew, CubeDescMapper cube, ModelsDescDataMapper modelDescData,
					  OlapTimingrefresh timingreFresh, Date date, OaUserVO userVO, List<OlapFilterCondidion> condidions, ArrayList<MeasureMapper> countMappers, List<TableNameRelationMapper> list) throws Exception;

	void saveTableClone(OlapCube olapCube, ArrayList<OlapCubeTable> cubeTablesList, ArrayList<OlapCubeTableColumn> findByColumn,
						OlapFilter findTableInfo, ArrayList<OlapCubeTableRelation> olapcubeList, OlapTimingrefresh olapTimingrefresh,
						List<OlapDatalaketable> datalaketables, OaUserVO loginUser, String cloneCubeName);

	/**
	 * <p>根据模型状态查询模型列表,再按照模型名称查询关联的表</p>
	 * <p>最后按照模型名称分组</p>
	 * @param flags
	 * @return Map,key:cubeName,value:OlapDatalaketable
	 */
	Map<String,List<OlapDatalaketable>> queryByFlags(Integer flags);

	List<OlapCube> queryListByFlags(Integer flags);

	List<CubeMapper> resetCubeStatus(List<CubeMapper> result);
}
