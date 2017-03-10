package com.sa.kubekit.action.security;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.model.security.Rol;
import com.sa.model.security.RolMenu;
import com.sa.model.security.Usuario;

//se deben agregar las librerias de richfaces para que funcione

@Name("kuPermisos")
@Scope(ScopeType.EVENT)
@AutoCreate
public class KuPermisos {

	@In
	private LoginUser loginUser;
	
	public boolean comprobarAcceso(){
		
		FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		String url = request.getRequestURL().toString();
		Usuario user = loginUser.getUser();
		if(url.contains("sainv/home")){
			return true;
		}
		
		for(Rol rol : user.getRoles()){
			for(RolMenu menu : rol.getMenus()){
				String menuUrl = menu.getMenu().getUrl().replace("xhtml", "sa");
				int index = menuUrl.lastIndexOf("/");
				menuUrl = menuUrl.substring(0, index+1);
				if(url.contains(menuUrl)){
					return true;
				}
			}
		}
		FacesMessages.instance().add("Acceso denegado, no cuenta con los permisos necesarios para acceder al recurso solicitado");
		return false;
	}

}