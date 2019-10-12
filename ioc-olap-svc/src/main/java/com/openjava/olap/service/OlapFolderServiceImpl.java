package com.openjava.olap.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.olap.domain.OlapFolder;
import com.openjava.olap.query.OlapFolderDBParam;
import com.openjava.olap.repository.OlapFolderRepository;
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
        return olapFolderRepository.save(m);
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
        return olapFolderRepository.findByCreateIdAndTypeOrderBySortNumDesc(userId, type);
    }

    @Override
    public boolean checkExsitName(String name, Long userId) {
        return checkExsitName(name, 0L, userId);
    }

    @Override
    public boolean checkExsitName(String name, Long folderId, Long userId) {
        List<OlapFolder> folder = olapFolderRepository.findByName(name, folderId, userId);
        if (folder.size() > 0) {
            return true;
        }
        return false;
    }
}
