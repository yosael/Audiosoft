package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Empresa;

@Name("empresaActivaList")
@Scope(ScopeType.CONVERSATION)
public class EmpresaActivaList extends KubeQuery<Empresa>{

	@Create
	public void init() {
		setJpql("select e from Empresa e where e.estado = 'ACT' order by e.nombre");
	}
}
