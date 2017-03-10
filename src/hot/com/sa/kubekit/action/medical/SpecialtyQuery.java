package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.medical.Specialty;

@Name("specialtyQuery")
@Scope(ScopeType.CONVERSATION)
public class SpecialtyQuery extends KubeQuery<Specialty> {

	@Create
	public void init() {
		setJpql("select e from Specialty e order by e.name");
	}

}
