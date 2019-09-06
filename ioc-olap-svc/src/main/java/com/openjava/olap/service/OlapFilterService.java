package com.openjava.olap.service;

import java.util.Date;
import java.util.List;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.OlapFilter;
import com.openjava.olap.domain.OlapFilterCondidion;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.query.OlapFilterDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 构建过滤业务层接口
 *
 * @author zy
 */
public interface OlapFilterService {
    Page<OlapFilter> query(OlapFilterDBParam params, Pageable pageable);

    List<OlapFilter> queryDataOnly(OlapFilterDBParam params, Pageable pageable);

    List<OlapFilter> filter(CubeDescMapper cube, List<OlapFilterCondidion> filterCondidionList, Date date, OaUserVO userVO);

    OlapFilter findTableInfo(String cubeName, Long createId);

    OlapFilter get(Long id);

    OlapFilter doSave(OlapFilter m);

    void doDelete(Long id);
    void deleteCubeName(String cubeName);
    void doRemove(String ids);
}
