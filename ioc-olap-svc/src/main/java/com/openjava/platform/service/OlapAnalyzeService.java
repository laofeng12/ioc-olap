package com.openjava.platform.service;

import java.util.List;

import com.openjava.platform.domain.OlapAnalyze;
import com.openjava.platform.domain.OlapRealQuery;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import com.openjava.platform.query.OlapAnalyzeDBParam;
import com.openjava.platform.vo.AnalyzeAxisVo;
import com.openjava.platform.vo.AnalyzeVo;
import com.openjava.platform.vo.AnyDimensionVo;
import org.ljdp.component.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件夹表业务层接口
 * @author xiepc
 *
 */
public interface OlapAnalyzeService {
	Page<OlapAnalyze> query(OlapAnalyzeDBParam params, Pageable pageable);
	
	List<OlapAnalyze> queryDataOnly(OlapAnalyzeDBParam params, Pageable pageable);
	
	OlapAnalyze get(Long id);
	
	OlapAnalyze doSave(OlapAnalyze m);
	
	void doDelete(Long id);
	void doRemove(String ids);

	List<OlapAnalyze> getListWithFolderId(Long folderId);

	List<OlapAnalyze> getAllShares(Long userId);

	AnalyzeVo save(AnalyzeVo analyzeVo) throws APIException;

	AnalyzeVo getVo(Long id);

    AnyDimensionVo query(Long cubeId,List<AnalyzeAxisVo> axises,String userId) throws APIException;

	AnyDimensionVo query(Long analyzeId, Long cubeId,String userId) throws APIException;

    QueryResultMapper queryDimension(Long tableId, Long columnId, Long userId,String key,Integer offeset,Integer limit) throws APIException;
}
