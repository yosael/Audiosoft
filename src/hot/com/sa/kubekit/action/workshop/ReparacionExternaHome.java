package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.ReparacionExterna;

@Name("reparacionExternaHome")
@Scope(ScopeType.CONVERSATION)
public class ReparacionExternaHome extends KubeDAO<ReparacionExterna> {
	
	
	private static final long serialVersionUID = 1L;
	private List<ReparacionExterna> resultList;
	
	
	public void load()
	{
	}
	
	public void cargarReparaciones()
	{
		resultList = new ArrayList<ReparacionExterna>();
		
		resultList = getEntityManager().createQuery("SELECT r FROM ReparacionExterna r").getResultList();
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

}
