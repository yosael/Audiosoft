package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Sistema;

@Name("sistemaList")
@Scope(ScopeType.CONVERSATION)
public class SistemaList extends KubeQuery<Sistema>{

	@Create
	public void init() {
		setJpql("select e from Sistema e order by e.nombre");
	}
}
