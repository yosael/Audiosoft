package com.sa.kubekit.action.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;

//version 1
public abstract class KubeQuery<E> {
	private List<E> resultList;
	private String jpql;
	SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy");

	public String filtrarFecha(Date fecha) {
		if(fecha != null)
			return df.format(fecha);
		else
			return "";
	}
	
	@In
	protected EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<E> getResultList() {
		resultList = entityManager.createQuery(jpql).getResultList();
		return resultList;
	}

	public void setResultList(List<E> resultList) {
		this.resultList = resultList;
	}

	public String getJpql() {
		return jpql;
	}

	public void setJpql(String jpql) {
		this.jpql = jpql;
	}

}
