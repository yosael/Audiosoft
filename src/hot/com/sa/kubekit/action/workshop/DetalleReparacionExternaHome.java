package com.sa.kubekit.action.workshop;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.DetalleReparacionExterna;

@Name("detalleReparacionExternaHome")
@Scope(ScopeType.CONVERSATION)
public class DetalleReparacionExternaHome extends KubeDAO<DetalleReparacionExterna> {
	
	
	private static final long serialVersionUID = 1L;
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return true;
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
