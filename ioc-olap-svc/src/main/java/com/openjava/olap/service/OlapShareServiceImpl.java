package com.openjava.olap.service;

import com.openjava.olap.domain.OlapCube;
import com.openjava.olap.domain.OlapShare;
import com.openjava.olap.dto.ShareUserDto;
import com.openjava.olap.query.OlapShareDBParam;
import com.openjava.olap.repository.OlapShareRepository;
import org.ljdp.component.sequence.ConcurrentSequence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 文件夹表业务层
 *
 * @author xiepc
 */
@Service
@Transactional
public class OlapShareServiceImpl implements OlapShareService {

	@Resource
	private OlapShareRepository olapShareRepository;

	@Resource
	private OlapCubeService olapCubeService;

	public Page<OlapShare> query(OlapShareDBParam params, Pageable pageable) {
		Page<OlapShare> pageresult = olapShareRepository.query(params, pageable);
		return pageresult;
	}

	public List<OlapShare> queryDataOnly(OlapShareDBParam params, Pageable pageable) {
		return olapShareRepository.queryDataOnly(params, pageable);
	}

	public OlapShare get(Long id) {
		Optional<OlapShare> o = olapShareRepository.findById(id);
		if (o.isPresent()) {
			OlapShare m = o.get();
			return m;
		}
		System.out.println("找不到记录OlapShare：" + id);
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
	@Transactional(readOnly = false)
	public void save(Long[] shareUserIds, String sourceType, Long sourceId, Long userId, String userName) {
		olapShareRepository.deleteByFkIdAndSourceId(sourceId, sourceType);
		for (Long shareUserId : shareUserIds) {
			OlapShare share = new OlapShare();
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
	@Transactional(readOnly = false)
	public void save(Long[] shareUserIds, String sourceType, Long sourceId, Long userId, String userName, String cubeName) {
		OlapCube olapCube = olapCubeService.findTableInfo(cubeName);

		olapShareRepository.deleteByFkIdAndSourceId(olapCube.getCubeId(), sourceType);
		for (Long shareUserId : shareUserIds) {
			OlapShare share = new OlapShare();
			share.setCreateId(userId);
			share.setCreateName(userName);
			share.setCreateTime(new Date());
			share.setFkId(olapCube.getCubeId());
			share.setFkType(sourceType);
			share.setIsNew(true);
			share.setShareId(ConcurrentSequence.getInstance().getSequence());
			share.setShareUserId(shareUserId);
			olapShareRepository.save(share);
		}
	}

	@Override
	public List<ShareUserDto> getList(String sourceType, String sourceId, Long userId) {
		List<Object[]> objectList = olapShareRepository.getList(sourceType, sourceId, userId);
		List<ShareUserDto> shareUserDtoList = new ArrayList<ShareUserDto>();
		for (int i = 0; i < objectList.size(); i++) {
			Object[] date = objectList.get(i);
			ShareUserDto shareUserDto = new ShareUserDto();
			shareUserDto.setShareId(Long.valueOf(date[0].toString()));       //Long id
			shareUserDto.setFkId(Long.valueOf(date[1].toString()));          //Long fkId
			shareUserDto.setFkType(date[2].toString());                      //String fkType
			shareUserDto.setShareUserId(Long.valueOf(date[3].toString()));//Long shareUserId
			shareUserDto.setCreateTime((Date) date[4]);                      //Date createTime
			shareUserDto.setCreateId(Long.valueOf(date[5].toString()));  //Long createId
			shareUserDto.setCreateName(date[6].toString());                //String createName
			shareUserDto.setShareUserName(date[7].toString());           //String shareUserName
			shareUserDtoList.add(shareUserDto);
		}
		return shareUserDtoList;
	}

	@Override
	public List<ShareUserDto> getList(String sourceType, String sourceId, Long userId, String cubeName) {
		OlapCube olapCube = olapCubeService.findTableInfo(cubeName);
		List<Object[]> objectList = olapShareRepository.getList(sourceType, String.valueOf(olapCube.getCubeId()), userId);
		List<ShareUserDto> shareUserDtoList = new ArrayList<ShareUserDto>();
		for (int i = 0; i < objectList.size(); i++) {
			Object[] date = objectList.get(i);
			ShareUserDto shareUserDto = new ShareUserDto();
			shareUserDto.setShareId(Long.valueOf(date[0].toString()));       //Long id
			shareUserDto.setFkId(Long.valueOf(date[1].toString()));          //Long fkId
			shareUserDto.setFkType(date[2].toString());                      //String fkType
			shareUserDto.setShareUserId(Long.valueOf(date[3].toString()));//Long shareUserId
			shareUserDto.setCreateTime((Date) date[4]);                      //Date createTime
			shareUserDto.setCreateId(Long.valueOf(date[5].toString()));  //Long createId
			shareUserDto.setCreateName(date[6].toString());                //String createName
			shareUserDto.setShareUserName(date[7].toString());           //String shareUserName
			shareUserDtoList.add(shareUserDto);
		}
		return shareUserDtoList;
	}
}
