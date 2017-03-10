package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Transferencia;

@Name("transferenciasSucursalList")
@Scope(ScopeType.CONVERSATION)
public class TransferenciasSucursalList extends KubeQuery<Transferencia>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()!=null){
			setJpql("select t from Transferencia t where " +
					"t.sucursalDestino.id = " + loginUser.getUser().getSucursal().getId() + 
					" order by t.fecha desc");
		}else{
			setJpql("select t from Transferencia t order by t.fecha desc");
		}
	}
}
