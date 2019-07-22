package com.openjava.platform.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapTimingrefreshRepositoryImpl implements OlapTimingrefreshRepositoryCustom {
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
