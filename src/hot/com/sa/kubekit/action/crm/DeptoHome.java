package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Departamento;



@Name("deptoHome")
@Scope(ScopeType.CONVERSATION)
public class DeptoHome  extends KubeDAO<Departamento> {
	private static final long serialVersionUID = 1L;
	private List<Departamento> resultList = new ArrayList<Departamento>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("deptoHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("deptoHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("deptoHome_deleted")));
		loadResultList();
	}
	
	//@SuppressWarnings("unchecked")
	public void loadResultList(){
		
		System.out.println("cargo los departamentos");
		setResultList(getEntityManager().createQuery("select d from Departamento d")
				.getResultList());
	}
	
	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
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

	public List<Departamento> getResultList() {
		return resultList;
	}

	public void setResultList(List<Departamento> resultList) {
		this.resultList = resultList;
	}

}
