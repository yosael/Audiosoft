package com.sa.kubekit.action.inventory;

import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Producto;

@Name("productoList")
@Scope(ScopeType.CONVERSATION)
public class ProductoList extends KubeQuery<Producto>{
	private String nomCoinci;
	
	private boolean verInactivos=false;

	@In
	private LoginUser loginUser;
	
	@Create
	public void init() {
		if(loginUser.getUser().getSucursal()==null)
		{
			
			if(verInactivos)
			{
				setJpql("select p from Producto p where (UPPER(p.nombre) like "+ "UPPER('%" + getNomCoinci() + "%')" +
						" OR UPPER(p.categoria.codigo) like UPPER('%" + this.getNomCoinci() + "%')   OR UPPER(p.referencia) like " + "UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.modelo) like UPPER('%" + this.getNomCoinci() + "%') ) order by p.referencia,p.categoria.codigo,p.nombre" );
			}
			else
			{
				setJpql("select p from Producto p where (UPPER(p.nombre) like "+ "UPPER('%" + getNomCoinci() + "%')" +
						" OR UPPER(p.categoria.codigo) like UPPER('%" + this.getNomCoinci() + "%')   OR UPPER(p.referencia) like " + "UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.modelo) like UPPER('%" + this.getNomCoinci() + "%') ) and p.activo=true order by p.referencia,p.categoria.codigo,p.nombre" );
			}
			
		}
		else
		{
			
			if(verInactivos)
			{
				setJpql("select p from Producto p where (p.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
						") AND (UPPER(p.nombre) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.referencia) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.modelo) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.categoria.codigo) like UPPER('%" + this.getNomCoinci() + "%')) order by p.referencia,p.categoria.codigo,p.nombre ");
			}
			else
			{
				setJpql("select p from Producto p where (p.empresa.id = " + loginUser.getUser().getSucursal().getEmpresa().getId() + 
						") AND (UPPER(p.nombre) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.referencia) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.modelo) like UPPER('%" + this.getNomCoinci() + "%') OR UPPER(p.categoria.codigo) like UPPER('%" + this.getNomCoinci() + "%')) and p.activo=true order by p.referencia,p.categoria.codigo,p.nombre ");
			}
			
		}
		
	}
	
	public void recargar()
	{
		nomCoinci="";
		
		setResultList(new ArrayList<Producto>());
		
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public boolean isVerInactivos() {
		return verInactivos;
	}

	public void setVerInactivos(boolean verInactivos) {
		this.verInactivos = verInactivos;
	}
	
	
	
}
