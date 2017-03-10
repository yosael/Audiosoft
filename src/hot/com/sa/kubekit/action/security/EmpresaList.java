package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Empresa;

@Name("empresaList")
@Scope(ScopeType.CONVERSATION)
public class EmpresaList extends KubeQuery<Empresa>{

	@Create
	public void init() {
		setJpql("select e from Empresa e order by e.nombre");
	}
}
