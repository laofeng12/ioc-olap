package com.openjava.olap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapDatalaketableRepositoryImpl implements OlapDatalaketableRepositoryCustom {
	private EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
