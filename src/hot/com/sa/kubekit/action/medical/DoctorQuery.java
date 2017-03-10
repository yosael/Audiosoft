package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.medical.Doctor;

@Name("doctorQuery")
@Scope(ScopeType.CONVERSATION)
public class DoctorQuery extends KubeQuery<Doctor> {

	@Create
	public void init() {
		setJpql("select e from Doctor e order by e.id");
	}

}
