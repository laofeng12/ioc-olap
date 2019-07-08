package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapFolder;
import com.openjava.platform.query.OlapFolderDBParam;
import com.openjava.platform.repository.OlapFolderRepository;
import org.ljdp.core.db.RoDBQueryParam;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 文件夹表业务层
 * @author xiepc
 *
 */
@Service
@Transactional
public class OlapFolderServiceImpl implements OlapFolderService {
	
	@Resource
	private OlapFolderRepository olapFolderRepository;
	
	public Page<OlapFolder> query(OlapFolderDBParam params, Pageable pageable){
		Page<OlapFolder> pageresult = olapFolderRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapFolder> queryDataOnly(OlapFolderDBParam params, Pageable pageable){
		return olapFolderRepository.queryDataOnly(params, pageable);
	}
	
	public OlapFolder get(Long id) {
		Optional<OlapFolder> o = olapFolderRepository.findById(id);
		if(o.isPresent()) {
			OlapFolder m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapFolder："+id);
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
		return olapFolderRepository.findByCreateIdAndTypeOrderBySortNumDesc(userId,type);
	}
}
