package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.workshop.DefectoCapsula;

@Name("defectoCapsulaHome")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unchecked")
public class DefectoCapsulaHome extends KubeDAO<DefectoCapsula> {
	
	private static final long serialVersionUID = 1L;
	
	private List<DefectoCapsula> defectosCap = new ArrayList<DefectoCapsula>();
	
	public void getResultList() {
		defectosCap = getEntityManager().createQuery("select d from DefectoCapsula d").getResultList();
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

	public List<DefectoCapsula> getDefectosCap() {
		return defectosCap;
	}

	public void setDefectosCap(List<DefectoCapsula> defectosCap) {
		this.defectosCap = defectosCap;
	}

	
}
