package com.openjava.platform.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapTimingrefresh;
import com.openjava.platform.query.OlapTimingrefreshDBParam;
import com.openjava.platform.repository.OlapTimingrefreshRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 构建定时任务业务层
 * @author zy
 *
 */
@Service
@Transactional
public class OlapTimingrefreshServiceImpl implements OlapTimingrefreshService {
	
	@Resource
	private OlapTimingrefreshRepository olapTimingrefreshRepository;
	
	public Page<OlapTimingrefresh> query(OlapTimingrefreshDBParam params, Pageable pageable){
		Page<OlapTimingrefresh> pageresult = olapTimingrefreshRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapTimingrefresh> queryDataOnly(OlapTimingrefreshDBParam params, Pageable pageable){
		return olapTimingrefreshRepository.queryDataOnly(params, pageable);
	}
	
	public OlapTimingrefresh get(Long id) {
		Optional<OlapTimingrefresh> o = olapTimingrefreshRepository.findById(id);
		if(o.isPresent()) {
			OlapTimingrefresh m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapTimingrefresh："+id);
		return null;
	}
	
	public OlapTimingrefresh doSave(OlapTimingrefresh m) {
		return olapTimingrefreshRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapTimingrefreshRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapTimingrefreshRepository.deleteById(new Long(items[i]));
		}
	}
}
