package com.openjava.olap.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.query.OlapTimingrefreshDBParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * 构建定时任务业务层接口
 *
 * @author zy
 */
public interface OlapTimingrefreshService {
    Page<OlapTimingrefresh> query(OlapTimingrefreshDBParam params, Pageable pageable);

    List<OlapTimingrefresh> queryDataOnly(OlapTimingrefreshDBParam params, Pageable pageable);

    List<OlapTimingrefresh> findTiming(int frequencyType);

    OlapTimingrefresh findTableInfo(String cubeName);

    OlapTimingrefresh get(Long id);

    OlapTimingrefresh doSave(OlapTimingrefresh m);

    void deleteCubeName(String cubeName);

    void doDelete(Long id);

    void doRemove(String ids);
}
