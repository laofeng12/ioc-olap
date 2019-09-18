package com.openjava.olap.service;

import java.util.*;
import javax.annotation.Resource;

import com.openjava.admin.user.vo.OaUserVO;
import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.mapper.kylin.CubeDescDataMapper;
import com.openjava.olap.mapper.kylin.CubeDescMapper;
import com.openjava.olap.query.OlapCubeDBParam;
import com.openjava.olap.repository.OlapCubeRepository;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.ljdp.component.sequence.SequenceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapCubeServiceImpl implements OlapCubeService {

    @Resource
    private OlapCubeRepository olapCubeRepository;

    public Page<OlapCube> query(OlapCubeDBParam params, Pageable pageable) {
        Page<OlapCube> pageresult = olapCubeRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapCube> queryDataOnly(OlapCubeDBParam params, Pageable pageable) {
        return olapCubeRepository.queryDataOnly(params, pageable);
    }

    public OlapCube findTableInfo(String cubeName, Long createId) {
        Optional<OlapCube> o = olapCubeRepository.findTableInfo(cubeName, createId);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        return null;
    }

    public OlapCube findTableInfo(String cubeName) {
        Optional<OlapCube> o = olapCubeRepository.findTableInfo(cubeName);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        return null;
    }

    public OlapCube get(Long id) {
        Optional<OlapCube> o = olapCubeRepository.findById(id);
        if (o.isPresent()) {
            OlapCube m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapCube：" + id);
        return null;
    }

    public OlapCube doSave(OlapCube m) {
        return olapCubeRepository.save(m);
    }

    public void doDelete(Long id) {
        olapCubeRepository.deleteById(id);
    }

    public void deleteCubeName(String cubeName) {
        olapCubeRepository.deleteCubeName(cubeName);
    }

    public List<OlapCube> getOlapShareByShareUserId(String shareUserId) {
        return olapCubeRepository.getOlapShareByShareUserId(shareUserId);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapCubeRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public List<OlapCube> findByUserId(Long createId) {
        return olapCubeRepository.findByUserId(createId);
    }

    @Override
    public List<OlapCube> findAll() {
        return olapCubeRepository.findAll();
    }

    @Override
    public ArrayList<OlapCube> getValidListByUserId(Long userId) {
        return olapCubeRepository.findByCreateIdAndFlags(userId, 1);
    }

    //保存OLAP_CUBE表
    public OlapCube saveCube(CubeDescMapper cube, Date date, OaUserVO userVO, Long dimensionLength, Long dimensionFiledLength, Long measureFiledLength) {
        CubeDescDataMapper cubeDescData = cube.getCubeDescData();
        //根据名称查询是否已经包含数据
        OlapCube olapCube = null;
        if (StringUtils.isNotBlank(cubeDescData.getUuid())) {
            olapCube = findTableInfo(cubeDescData.getName(), Long.parseLong(userVO.getUserId()));
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setDimensionLength(dimensionLength);
            olapCube.setDimensionFiledLength(dimensionFiledLength);
            olapCube.setMeasureFiledLength(measureFiledLength);
            olapCube.setUpdateId(Long.parseLong(userVO.getUserId()));
            olapCube.setUpdateName(userVO.getUserAccount());
            olapCube.setUpdateTime(date);
            olapCube.setIsNew(false);
        } else {
            olapCube = new OlapCube();
            olapCube.setName(cubeDescData.getName());
            olapCube.setRemark(cubeDescData.getDescription());
            olapCube.setCubeId(ConcurrentSequence.getInstance().getSequence());
            olapCube.setDimensionLength(dimensionLength);
            olapCube.setDimensionFiledLength(dimensionFiledLength);
            olapCube.setMeasureFiledLength(measureFiledLength);
            olapCube.setCreateTime(date);
            olapCube.setCreateId(Long.parseLong(userVO.getUserId()));
            olapCube.setCreateName(userVO.getUserAccount());
            olapCube.setFlags(0);
            olapCube.setIsNew(true);
        }
        return olapCube;
    }
}
