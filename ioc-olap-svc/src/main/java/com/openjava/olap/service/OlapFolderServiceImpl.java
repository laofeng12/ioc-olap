package com.openjava.olap.service;

import com.openjava.olap.domain.OlapFolder;
import com.openjava.olap.query.OlapFolderDBParam;
import com.openjava.olap.query.OlapFolderSortParam;
import com.openjava.olap.repository.OlapFolderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapFolderServiceImpl implements OlapFolderService {

    @Resource
    private OlapFolderRepository olapFolderRepository;

    public Page<OlapFolder> query(OlapFolderDBParam params, Pageable pageable) {
        Page<OlapFolder> pageresult = olapFolderRepository.query(params, pageable);
        return pageresult;
    }

    public List<OlapFolder> queryDataOnly(OlapFolderDBParam params, Pageable pageable) {
        return olapFolderRepository.queryDataOnly(params, pageable);
    }

    public OlapFolder get(Long id) {
        Optional<OlapFolder> o = olapFolderRepository.findById(id);
        if (o.isPresent()) {
            OlapFolder m = o.get();
            return m;
        }
        System.out.println("找不到记录OlapFolder：" + id);
        return null;
    }

    public OlapFolder doSave(OlapFolder m) {
        if (m.getIsNew() == Boolean.TRUE){
            // 如果是分布式服务，则需要使用分布式锁加锁查询最大值
            Integer max = this.olapFolderRepository.queryMaxSortNum(m.getCreateId(),m.getType());
            if (max != null){
                m.setSortNum(max+1);
            }else {
                m.setSortNum(0);
            }
        }
        return olapFolderRepository.save(m);
    }

    @Transactional
    @Override
    public void batchUpdateSortNum(List<OlapFolderSortParam> list){
        List<Long> ids = list.stream().map(OlapFolderSortParam::getFolderId).collect(Collectors.toList());
        List<OlapFolder> record = this.olapFolderRepository.findAllById(ids);
        if (record.isEmpty()){
            return;
        }
        record.forEach(s->{
            OlapFolderSortParam param =list.stream().filter(x->x.getFolderId().equals(s.getFolderId())).findFirst().orElse(null);
            if (param != null){
                s.setSortNum(param.getSortNum());
            }
        });
        this.olapFolderRepository.saveAll(record);
    }

    public void doDelete(Long id) {
        olapFolderRepository.deleteById(id);
    }

    public void doRemove(String ids) {
        String[] items = ids.split(",");
        for (int i = 0; i < items.length; i++) {
            olapFolderRepository.deleteById(new Long(items[i]));
        }
    }

    @Override
    public List<OlapFolder> getListByCreateId(Long userId) {
        return olapFolderRepository.findByCreateIdOrderBySortNumDesc(userId);
    }

    @Override
    public List<OlapFolder> getListByTypeAndCreateId(Long userId, String type) {
        return olapFolderRepository.findByCreateIdAndTypeOrderBySortNumAsc(userId, type);
    }

    @Override
    public boolean checkExsitName(String name, Long userId,String type) {
        return checkExsitName(name, 0L, userId,type);
    }

    @Override
    public boolean checkExsitName(String name, Long folderId, Long userId,String type) {
        List<OlapFolder> folder = olapFolderRepository.findByName(name, folderId, userId,type);
        if (folder.size() > 0) {
            return true;
        }
        return false;
    }
}
