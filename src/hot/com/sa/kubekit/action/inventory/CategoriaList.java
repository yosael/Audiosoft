package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Categoria;

@Name("categoriaList")
@Scope(ScopeType.CONVERSATION)
public class CategoriaList extends KubeQuery<Categoria>{

	
	private String busqueda; 
	
	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		
		System.out.println("Listo las categorias");
		if(loginUser.getUser().getSucursal()==null){
			setJpql("select e from Categoria e order by e.activo DESC,e.codigo ASC ");
		}else{
			setJpql("select e from Categoria e where e.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
					" order by e.activo DESC,e.codigo ASC ");
		}		
		
	}
	
	
	public void buscarCategoria()
	{
		//Result
		setJpql("select c from Categoria c where UPPER(c.nombre) like '%"+busqueda.toUpperCase()+"%' or c.codigo like '%"+busqueda.toUpperCase()+"%' order by c.activo DESC,c.codigo ASC ");

		
	}


	public String getBusqueda() {
		return busqueda;
	}


	public void setBusqueda(String busqueda) {
		this.busqueda = busqueda;
	}
	
	
	
}
