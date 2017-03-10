package com.sa.kubekit.action.medical;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.medical.Prescription;

@Name("prescriptionQuery")
@Scope(ScopeType.CONVERSATION)
public class PrescriptionQuery extends KubeQuery<Prescription>{

	@Create
	public void init() {
		setJpql("select e from Prescription e order by e.estado, e.id");
	}
}
