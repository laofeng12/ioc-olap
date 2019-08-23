package com.openjava.platform.service;

import java.util.Date;
import java.util.List;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.platform.domain.OlapCube;
import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapFilter;
import com.openjava.platform.domain.OlapFilterCondidion;
import com.openjava.platform.mapper.kylin.CubeDescMapper;
import com.openjava.platform.query.OlapFilterDBParam;
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

    void doRemove(String ids);
}
