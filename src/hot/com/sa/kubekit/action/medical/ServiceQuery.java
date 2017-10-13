package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.sales.Service;

@Name("serviceQuery")
@Scope(ScopeType.CONVERSATION)
public class ServiceQuery extends KubeQuery<Service> {
	
	private String buscar = "";

	@Create
	public void init() {
		
		setJpql("select e from Service e where (e.eliminado is null or e.eliminado<>'ELIM') order by e.estado ASC,e.codigo ASC");
	}
	
	
	public void buscarServicio()
	{
		
		
		if(buscar!=null && !buscar.equals(""))
			setJpql("select e from Service e where UPPER(e.name) like '%"+buscar.toUpperCase()+"%' OR UPPER(e.codigo) like '%"+buscar.toUpperCase()+"%'  AND (e.eliminado is null or e.eliminado<>'ELIM') order by e.estado ASC,e.codigo ASC");
	}

	public String getBuscar() {
		return buscar;
	}

	public void setBuscar(String buscar) {
		this.buscar = buscar;
	}
	
	
	

}