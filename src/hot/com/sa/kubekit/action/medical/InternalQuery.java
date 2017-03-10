package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.medical.Internal;

@Name("internalQuery")
@Scope(ScopeType.CONVERSATION)
public class InternalQuery extends KubeQuery<Internal> {

	@Create
	public void init() {
		setJpql("select e from Internal e order by e.id");
	}

}
