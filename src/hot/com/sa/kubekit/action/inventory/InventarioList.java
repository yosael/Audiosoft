package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Inventario;

@Name("inventarioList")
@Scope(ScopeType.CONVERSATION)
public class InventarioList extends KubeQuery<Inventario>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select i from Inventario i order by i.producto.nombre");
		}else{
			setJpql("select i from Inventario i where i.sucursal.id = " + loginUser.getUser().getSucursal().getId() + 
					" order by i.producto.nombre");
		}
		
	}
}
