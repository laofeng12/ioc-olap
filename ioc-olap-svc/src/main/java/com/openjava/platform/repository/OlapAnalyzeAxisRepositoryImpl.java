package com.openjava.platform.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapAnalyzeAxisRepositoryImpl implements OlapAnalyzeAxisRepositoryCustom {
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
