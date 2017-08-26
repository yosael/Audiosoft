package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Compra;

@Name("compraList")
@Scope(ScopeType.CONVERSATION)
public class CompraList extends KubeQuery<Compra>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select e from Compra e order by e.id desc ");
		}else{
			setJpql("select e from Compra e where e.sucursal.id = " + loginUser.getUser().getSucursal().getId() + 
					" order by e.id desc");
		}		
	}
}
