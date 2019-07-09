package com.openjava.platform.query;

import java.util.Date;

import org.ljdp.core.db.RoDBQueryParam;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 查询对象
 * @author xiepc
 *
 */
public class OlapShareDBParam extends RoDBQueryParam {
	private Long eq_id;//主键ID --主键查询
	
	
	public Long getEq_id() {
		return eq_id;
	}
	public void setEq_id(Long id) {
		this.eq_id = id;
	}
	
}