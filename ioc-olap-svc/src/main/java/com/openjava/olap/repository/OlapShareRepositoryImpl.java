package com.openjava.olap.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class OlapShareRepositoryImpl implements OlapShareRepositoryCustom {
	private EntityManager em;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
}
