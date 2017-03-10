package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.workshop.ComponenteAparato;

@Name("componenteApaHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class ComponenteApaHome extends KubeDAO<ComponenteAparato> {
	
	private static final long serialVersionUID = 1L;
	
	private List<ComponenteAparato> componentesApa = new ArrayList<ComponenteAparato>();
	
	public void getResultList() {
		componentesApa = getEntityManager().createQuery("select c from ComponenteAparato c").getResultList();
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

	public List<ComponenteAparato> getComponentesApa() {
		return componentesApa;
	}

	public void setComponentesApa(List<ComponenteAparato> componentesApa) {
		this.componentesApa = componentesApa;
	}

	
	
}
