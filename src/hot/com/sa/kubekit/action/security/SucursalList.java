package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Sucursal;

@Name("sucursalList")
@Scope(ScopeType.CONVERSATION)
public class SucursalList extends KubeQuery<Sucursal>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()!=null){
			setJpql("select e from Sucursal e where e.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
					" order by e.codigo ASC");
		}else{
			setJpql("select e from Sucursal e order by e.codigo ASC");
		}
		
	}
}
