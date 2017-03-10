package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Proveedor;

@Name("proveedorList")
@Scope(ScopeType.CONVERSATION)
public class ProveedorList extends KubeQuery<Proveedor>{
	
	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select e from Proveedor e order by e.razonSocial");
		}else{
			setJpql("select e from Proveedor e where e.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
					" order by e.razonSocial");
		}
		
	}
}
