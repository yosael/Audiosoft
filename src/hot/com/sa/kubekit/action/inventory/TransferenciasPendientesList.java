package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Transferencia;

//Listas de transferencias pendientes de ser aprobadas por el jefe de la
//sucursal que hace la solicitud de transferencia

@Name("transferenciasPendientesList")
@Scope(ScopeType.CONVERSATION)
public class TransferenciasPendientesList extends KubeQuery<Transferencia>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		
		if(loginUser.getUser().getSucursal()!=null){
			setJpql("select t from Transferencia t where (t.estado = 'P' OR t.estado = 'S') and " +
					"t.sucursal.id = " + loginUser.getUser().getSucursal().getId() + 
					" order by t.id desc ");
			
		}else{
			setJpql("select t from Transferencia t where (t.estado like 'P' or t.estado like 'S') order by t.id desc");
		}
	}
}
