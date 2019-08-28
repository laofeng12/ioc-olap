package com.openjava.olap.query;

import org.ljdp.core.db.RoDBQueryParam;

/**
 * 查询对象
 * @author zy
 *
 */
public class OlapDatalaketableDBParam extends RoDBQueryParam {
	private Long eq_id;//主键ID --主键查询


	public Long getEq_id() {
		return eq_id;
	}
	public void setEq_id(Long id) {
		this.eq_id = id;
	}

}
