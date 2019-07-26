package com.openjava.platform.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapShare;
import com.openjava.platform.query.OlapShareDBParam;
import com.openjava.platform.repository.OlapShareRepository;
import org.ljdp.component.sequence.ConcurrentSequence;
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
public class OlapShareServiceImpl implements OlapShareService {
	
	@Resource
	private OlapShareRepository olapShareRepository;
	
	public Page<OlapShare> query(OlapShareDBParam params, Pageable pageable){
		Page<OlapShare> pageresult = olapShareRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapShare> queryDataOnly(OlapShareDBParam params, Pageable pageable){
		return olapShareRepository.queryDataOnly(params, pageable);
	}
	
	public OlapShare get(Long id) {
		Optional<OlapShare> o = olapShareRepository.findById(id);
		if(o.isPresent()) {
			OlapShare m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapShare："+id);
		return null;
	}
	
	public OlapShare doSave(OlapShare m) {
		return olapShareRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapShareRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapShareRepository.deleteById(new Long(items[i]));
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void save(Long[] shareUserIds, String sourceType, Long sourceId, Long userId,String userName) {
		olapShareRepository.deleteByFkIdAndSourceId(sourceId,sourceType);
		for (Long shareUserId : shareUserIds){
			OlapShare share=new OlapShare();
			share.setCreateId(userId);
			share.setCreateName(userName);
			share.setCreateTime(new Date());
			share.setFkId(sourceId);
			share.setFkType(sourceType);
			share.setIsNew(true);
			share.setShareId(ConcurrentSequence.getInstance().getSequence());
			share.setShareUserId(shareUserId);
			olapShareRepository.save(share);
		}
	}

	@Override
	public List<OlapShare> getList(String sourceType, String sourceId, Long userId) {
		return olapShareRepository.getList(sourceType,sourceId,userId);
	}
}
