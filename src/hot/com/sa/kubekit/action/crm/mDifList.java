package com.sa.kubekit.action.crm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.crm.MedioDifusion;

@Name("mDifList")
@Scope(ScopeType.CONVERSATION)

public class mDifList extends KubeQuery<MedioDifusion>{

	
	@Create
	public void init() {
		setJpql("select md from MedioDifusion md order by md.id ASC ");	
	}
}
