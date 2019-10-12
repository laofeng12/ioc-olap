package com.openjava.olap.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javax.annotation.Resource;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.domain.OlapTimingrefresh;
import com.openjava.olap.mapper.kylin.CubeDescDataMapper;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.query.OlapTimingrefreshDBParam;
import com.openjava.olap.repository.OlapTimingrefreshRepository;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 构建定时任务业务层
 *
 * @author zy
 */
@Service
@Transactional
public class OlapTimingrefreshServiceImpl implements OlapTimingrefreshService {

    @Resource
    private OlapTimingrefreshRepository olapTimingrefreshRepository;

    public Page<OlapTimingrefresh> query(OlapTimingrefreshDBParam params, Pageable pageable) {
        Page<OlapTimingrefresh> pageresult = olapTimingrefreshRepository.query(params, pageable);
        return pageresult;
    }

    @Override
    public List<OlapTimingrefresh> findTiming(int frequencyType) {
        return olapTimingrefreshRepository.findTiming(frequencyType);
    }

    public List<OlapTimingrefresh> queryDataOnly(OlapTimingrefreshDBParam params, Pageable pageable) {
        return olapTimingrefreshRepository.queryDataOnly(params, pageable);
    }

    public OlapTimingrefresh findTableInfo(String cubeName) {
        Optional<OlapTimingrefresh> o = olapTimingrefreshRepository.findTableInfo(cubeName);
        if (o.isPresent()) {
            OlapTimingrefresh m = o.get();
            return m;
        }
        return null;
    }

    public OlapTimingrefresh get(Long id) {
        Optional<OlapTimingrefresh> o = olapTimingrefreshRepository.findById(id);
        if (o.isPresent()) {
            OlapTimingrefresh m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapTimingrefresh：" + id);
        return null;
    }

    public OlapTimingrefresh doSave(OlapTimingrefresh m) {
        return olapTimingrefreshRepository.save(m);
    }

    public void doDelete(Long id) {
        olapTimingrefreshRepository.deleteById(id);
    }

    public void deleteCubeName(String cubeName) {
        olapTimingrefreshRepository.deleteCubeName(cubeName);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapTimingrefreshRepository.deleteById(new Long(items[i]));
        }
    }


}
