package com.sa.kubekit.action.medical;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.model.medical.ServiceClinicalHistory;

@Name("generalContainer")
@Scope(ScopeType.CONVERSATION)
public class GeneralContainer {

	private String treatment;
	private String exams;
	private String mode;
	private List<ServiceClinicalHistory> diagnostics;

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getExams() {
		return exams;
	}

	public void setExams(String exams) {
		this.exams = exams;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public List<ServiceClinicalHistory> getDiagnostics() {
		return diagnostics;
	}

	public void setDiagnostics(List<ServiceClinicalHistory> diagnostics) {
		this.diagnostics = diagnostics;
	}

}
