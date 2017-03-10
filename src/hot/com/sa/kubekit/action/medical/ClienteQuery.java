package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.crm.Cliente;

@Name("clienteQuery")
@Scope(ScopeType.CONVERSATION)
public class ClienteQuery extends KubeQuery<Cliente>{

	@Create
	public void init() {
		setJpql("select c from Cliente c where c.id < 300 order by c.id ");
	}
}
