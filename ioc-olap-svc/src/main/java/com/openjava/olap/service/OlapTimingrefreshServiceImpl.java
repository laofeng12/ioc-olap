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

    public OlapTimingrefresh findTableInfo(String cubeName, Long createId) {
        Optional<OlapTimingrefresh> o = olapTimingrefreshRepository.findTableInfo(cubeName, createId);
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

    //创建定时任务
    public void timingTasks(OlapTimingrefresh task, CubeDescMapper cube, Date date, OaUserVO userVO) {
        //根据是否存在立方体ID去判断是否为修改, 如果是为修改则根据用户ID和立方体名称去查询出数据并修改olap_timingrefresh表数据
        if (StringUtils.isNotBlank(cube.getCubeDescData().getUuid())) {
            OlapTimingrefresh olapTimingrefresh = findTableInfo(cube.getCubeDescData().getName(), Long.parseLong(userVO.getUserId()));
            if (olapTimingrefresh != null) {
                olapTimingrefresh.setUpdateId(Long.parseLong(userVO.getUserId()));
                olapTimingrefresh.setUpdateName(userVO.getUserAccount());
                olapTimingrefresh.setUpdateTime(date);
                olapTimingrefresh.setIsNew(false);
                olapTimingrefresh.setInterval(task.getInterval());
                olapTimingrefresh.setFrequencytype(task.getFrequencytype());
                olapTimingrefresh.setAutoReload(task.getAutoReload());
                calcExcuteTime(olapTimingrefresh);
                doSave(olapTimingrefresh);
            }
            else{
                task.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
                task.setCreateName(userVO.getUserAccount());//创建人名称
                task.setCreateTime(date);//创建时间
                task.setIsNew(true);
                task.setId(ConcurrentSequence.getInstance().getSequence());
                task.setCubeName(cube.getCubeDescData().getName());//立方体名称
                calcExcuteTime(task);
                doSave(task);
            }
        } else {
            task.setCreateId(Long.parseLong(userVO.getUserId()));//创建人id
            task.setCreateName(userVO.getUserAccount());//创建人名称
            task.setCreateTime(date);//创建时间
            task.setIsNew(true);
            task.setId(ConcurrentSequence.getInstance().getSequence());
            task.setCubeName(cube.getCubeDescData().getName());//立方体名称
            calcExcuteTime(task);
            doSave(task);
        }
    }

    private void calcExcuteTime(OlapTimingrefresh task) {
        //1 是开启,0 是未开启
        if (task.getAutoReload() == 1) {
            int interval = task.getInterval().intValue();
            Date now = new Date();
            //只获取年月日 时分秒自动填充为00 00 00
            LocalDate localDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date executionTime = java.sql.Date.valueOf(localDate);
            Calendar calendar = Calendar.getInstance();
            //拿到当前小时并加入到年月日组成当前时间
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            calendar.setTime(executionTime);
            calendar.add(Calendar.HOUR, hour);

            Date finaDate = calendar.getTime();
            task.setFinalExecutionTime(finaDate);//最后执行时间

            //当前时间加上间隔时间算出 下一次执行时间
            switch (task.getFrequencytype().toString()) {
                case "1"://小时
                    calendar.add(Calendar.HOUR, interval);
                    break;
                case "2"://天数
                    calendar.add(Calendar.DAY_OF_MONTH, +interval);
                    break;
                default://月
                    calendar.add(Calendar.MONTH, +interval);
                    break;
            }
            Date nextDate = calendar.getTime();
            task.setNextExecutionTime(nextDate);//下一次执行执行时间
        }
    }
}
