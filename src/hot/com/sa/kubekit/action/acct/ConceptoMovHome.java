package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.ConceptoMov;

@Name("conceptoMovHome")
@Scope(ScopeType.CONVERSATION)
public class ConceptoMovHome extends KubeDAO<ConceptoMov>{

	private static final long serialVersionUID = 1L;
	private Integer cnmId;
	private String concepto;
	private List<ConceptoMov> resultList = new ArrayList<ConceptoMov>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("concmv_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("concmv_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("concmv_deleted")));
	}
	
	public void load() {
		try{
			setInstance(getEntityManager().find(ConceptoMov.class, cnmId));
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new ConceptoMov());
		}
	}
	
	public void getConceptosList() {
		resultList = getEntityManager()
				.createQuery("SELECT c FROM ConceptoMov c ")
				.getResultList();
	}
	
	public List<ConceptoMov> getConceptosCoincidence(Object cnp) {
		if(cnp != null && !cnp.toString().trim().equals(""))
			resultList = getEntityManager()
					.createQuery("SELECT c FROM ConceptoMov c " +
							"	WHERE UPPER(c.nombre) LIKE UPPER(CONCAT('%',:conc, '%'))")
					.setParameter("conc", cnp.toString())
					.getResultList();
		return resultList;
	}
	
	public void guardarConcepto() {
		//Buscamos otro concepto que se llame igual
		List<ConceptoMov> coinci = getEntityManager()
				.createQuery("SELECT c FROM ConceptoMov c " +
						"	WHERE UPPER(c.nombre) = UPPER(:nom)")
				.setParameter("nom", getConcepto().toUpperCase())
				.getResultList();
		if(coinci == null || coinci.size() <= 0) {
			ConceptoMov concMov = new ConceptoMov();
			concMov.setNombre(getConcepto());
			select(concMov);
			save();
		} else {
			select(coinci.get(0));
		}
	}
	
	@Override
	public boolean preSave() {
		
		return true;
	}

	@Override
	public boolean preModify() {
		
		
		return true;
	}
	
	

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<ConceptoMov> getResultList() {
		return resultList;
	}

	public void setResultList(List<ConceptoMov> resultList) {
		this.resultList = resultList;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
}
