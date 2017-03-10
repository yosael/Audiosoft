package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.UnidadMedida;

@Name("unidadList") 
@Scope(ScopeType.CONVERSATION)
public class UnidadMedidaList extends KubeQuery<UnidadMedida>{
	
	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select e from UnidadMedida e order by e.nombre");
		}else{
			setJpql("select e from UnidadMedida e where e.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
					" order by e.nombre");
		}
		
	}
}
