package com.sa.kubekit.action.util;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;

public abstract class KubeSearcher<E> {

	private List<E> resultList;

	@In
	protected EntityManager entityManager;

	public void search() {
		if (validateParams()) {
			searchImpl();
		}
	}

	protected abstract void searchImpl();

	protected abstract boolean validateParams();

	public List<E> getResultList() {
		return resultList;
	}

	public void setResultList(List<E> resultList) {
		this.resultList = resultList;
	}

}
