package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.security.Rol;

@Name("rolList")
@Scope(ScopeType.CONVERSATION)
public class RolList extends KubeQuery<Rol>{

	@Create
	public void init() {
		setJpql("select r from Rol r order by r.codigo ASC");
	}
}
