package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Usuario;
       
@Name("usuarioList")
@Scope(ScopeType.CONVERSATION)
public class UsuarioList extends KubeQuery<Usuario>{

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select u from Usuario u order by u.nombreUsuario");
		}else{
			setJpql("select u from Usuario u where u.sucursal.id = " + loginUser.getUser().getSucursal().getId() + 
					" order by u.nombreUsuario");
		}
		
	}
}
