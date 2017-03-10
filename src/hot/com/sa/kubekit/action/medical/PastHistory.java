package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.model.crm.Cliente;
import com.sa.model.medical.ClinicalHistory;

@Name("pastHistory")
@Scope(ScopeType.CONVERSATION)
public class PastHistory {

	private Integer size = 5;
	private Cliente cliente;
	private Long noShowConsecutive;
	private List<ClinicalHistory> clinicalHistories;

	@In
	private EntityManager entityManager;

	public List<ClinicalHistory> load(Cliente cliente,
			Long noShowConsecutive) {
		this.cliente = cliente;
		this.noShowConsecutive = noShowConsecutive;
		return load();
	}

	@SuppressWarnings("unchecked")
	public List<ClinicalHistory> load() {
		System.out.println("Entro al pastHistory.load()");
		clinicalHistories = new ArrayList<ClinicalHistory>();
		if (noShowConsecutive == null)
			this.noShowConsecutive = new Long(-1);
		System.out.println("****Valor de cons = " + noShowConsecutive);
		if (cliente != null)
			//if (clinicalHistoryType == null)
				clinicalHistories = entityManager
						.createQuery(
								"select h from ClinicalHistory h where h.cliente = :cliente and " +
								"h.consecutive != :cons order by h.creationDate desc")
								.setParameter("patient", cliente)
								.setParameter("cons",noShowConsecutive).setMaxResults(size)
								.getResultList();
		System.out.println("*******Historias pasadas encontradas = " + clinicalHistories.size());
		return clinicalHistories;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Long getNoShowConsecutive() {
		return noShowConsecutive;
	}

	public void setNoShowConsecutive(Long noShowConsecutive) {
		this.noShowConsecutive = noShowConsecutive;
	}

	public List<ClinicalHistory> getClinicalHistories() {
		return clinicalHistories;
	}

	public void setClinicalHistories(List<ClinicalHistory> clinicalHistories) {
		this.clinicalHistories = clinicalHistories;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	
	
}