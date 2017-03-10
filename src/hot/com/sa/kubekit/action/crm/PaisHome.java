package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Pais;

@Name("paisHome")
@Scope(ScopeType.CONVERSATION)
public class PaisHome extends KubeDAO<Pais> {
	
	private static final long serialVersionUID = 1L;

	private List<Pais> resultList = new ArrayList<Pais>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("paisHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("paisHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("paisHome_deleted")));
		loadResultList();
	}
	

		
	//@SuppressWarnings("unchecked")
	public void loadResultList(){
		resultList = getEntityManager().createQuery("select p from Pais p order by id")
				.getResultList();
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

	public List<Pais> getResultList() {
		return resultList;
	}

	public void setResultList(List<Pais> resultList) {
		this.resultList = resultList;
	}

	

}
