package com.openjava.platform.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapAnalyzeAxisFilterRepositoryImpl implements OlapAnalyzeAxisFilterRepositoryCustom {
	private EntityManager em;
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
