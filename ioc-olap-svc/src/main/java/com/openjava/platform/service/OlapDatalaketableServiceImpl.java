package com.openjava.platform.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;

import com.openjava.platform.domain.OlapCubeTable;
import com.openjava.platform.domain.OlapDatalaketable;
import com.openjava.platform.query.OlapDatalaketableDBParam;
import com.openjava.platform.repository.OlapDatalaketableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 构建选择表业务层
 * @author zy
 *
 */
@Service
@Transactional
public class OlapDatalaketableServiceImpl implements OlapDatalaketableService {
	
	@Resource
	private OlapDatalaketableRepository olapDatalaketableRepository;
	
	public Page<OlapDatalaketable> query(OlapDatalaketableDBParam params, Pageable pageable){
		Page<OlapDatalaketable> pageresult = olapDatalaketableRepository.query(params, pageable);
		return pageresult;
	}
	
	public List<OlapDatalaketable> queryDataOnly(OlapDatalaketableDBParam params, Pageable pageable){
		return olapDatalaketableRepository.queryDataOnly(params, pageable);
	}
	
	public OlapDatalaketable get(Long id) {
		Optional<OlapDatalaketable> o = olapDatalaketableRepository.findById(id);
		if(o.isPresent()) {
			OlapDatalaketable m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapDatalaketable："+id);
		return null;
	}
	
	public OlapDatalaketable doSave(OlapDatalaketable m) {
		return olapDatalaketableRepository.save(m);
	}
	
	public void doDelete(Long id) {
		olapDatalaketableRepository.deleteById(id);
	}
	public void doRemove(String ids) {
		String[] items = ids.split(",");
		for (int i = 0; i < items.length; i++) {
			olapDatalaketableRepository.deleteById(new Long(items[i]));
		}
	}

	@Override
	public ArrayList<OlapDatalaketable> getListByCubeName(String cubeName) {
		return olapDatalaketableRepository.getListByCubeName(cubeName);
	}
}
