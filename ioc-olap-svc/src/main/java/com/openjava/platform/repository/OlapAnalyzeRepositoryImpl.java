package com.openjava.platform.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapAnalyzeRepositoryImpl implements OlapAnalyzeRepositoryCustom {
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
