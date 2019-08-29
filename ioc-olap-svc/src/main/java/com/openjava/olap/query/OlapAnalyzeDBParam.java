package com.openjava.olap.query;

import org.ljdp.core.db.RoDBQueryParam;

/**
 * 查询对象
 * @author xiepc
 *
 */
public class OlapAnalyzeDBParam extends RoDBQueryParam {
	private Long eq_id;//ID --主键查询


	public Long getEq_id() {
		return eq_id;
	}
	public void setEq_id(Long id) {
		this.eq_id = id;
	}

}
