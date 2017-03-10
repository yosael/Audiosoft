package com.sa.model.medical;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

import com.sa.model.medical.id.ServiceClinicalHistoryId;
import com.sa.model.sales.Service;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:39
 */
@Entity
@Table(name = "service_clinical_history")
public class ServiceClinicalHistory {

	private ServiceClinicalHistoryId serviceClinicalHistoryId;
	private Integer consultationPurpose;
	private Integer externalCause;
	private String principalDiagnostic;
	private String relatedDiagnostic1;
	private String relatedDiagnostic2;
	private String relatedDiagnostic3;
	private Integer diagnosticType;
	private Integer embodimentProcessArea;
	private Integer procedurePurpose;
	private Integer servingStaff;
	private String complication;
	private Integer surgicalEmbodiment;
	private Service service;
	private ClinicalHistory clinicalHistory;
	private MedicalAppointmentService medicalAppointmentService;

	@EmbeddedId
	public ServiceClinicalHistoryId getServiceClinicalHistoryId() {
		return serviceClinicalHistoryId;
	}

	public void setServiceClinicalHistoryId(
			ServiceClinicalHistoryId serviceClinicalHistoryId) {
		this.serviceClinicalHistoryId = serviceClinicalHistoryId;
	}

	@Column(name = "consultation_purpose", nullable = true)
	public Integer getConsultationPurpose() {
		return consultationPurpose;
	}

	public void setConsultationPurpose(Integer consultationPurpose) {
		this.consultationPurpose = consultationPurpose;
	}

	@Column(name = "external_cause", nullable = true)
	public Integer getExternalCause() {
		return externalCause;
	}

	public void setExternalCause(Integer externalCause) {
		this.externalCause = externalCause;
	}

	@Column(name = "principal_diagnostic", nullable = true, length = 10)
	@Length(max = 10)
	public String getPrincipalDiagnostic() {
		return principalDiagnostic;
	}

	public void setPrincipalDiagnostic(String principalDiagnostic) {
		this.principalDiagnostic = principalDiagnostic;
	}

	@Column(name = "related_diagnostic1", nullable = true, length = 10)
	@Length(max = 10)
	public String getRelatedDiagnostic1() {
		return relatedDiagnostic1;
	}

	public void setRelatedDiagnostic1(String relatedDiagnostic1) {
		this.relatedDiagnostic1 = relatedDiagnostic1;
	}

	@Column(name = "related_diagnostic2", nullable = true, length = 10)
	@Length(max = 10)
	public String getRelatedDiagnostic2() {
		return relatedDiagnostic2;
	}

	public void setRelatedDiagnostic2(String relatedDiagnostic2) {
		this.relatedDiagnostic2 = relatedDiagnostic2;
	}

	@Column(name = "related_diagnostic3", nullable = true, length = 10)
	@Length(max = 10)
	public String getRelatedDiagnostic3() {
		return relatedDiagnostic3;
	}

	public void setRelatedDiagnostic3(String relatedDiagnostic3) {
		this.relatedDiagnostic3 = relatedDiagnostic3;
	}

	@Column(name = "diagnostic_type", nullable = true)
	public Integer getDiagnosticType() {
		return diagnosticType;
	}

	public void setDiagnosticType(Integer diagnosticType) {
		this.diagnosticType = diagnosticType;
	}

	@Column(name = "embodiment_process_area", nullable = true)
	public Integer getEmbodimentProcessArea() {
		return embodimentProcessArea;
	}

	public void setEmbodimentProcessArea(Integer embodimentProcessArea) {
		this.embodimentProcessArea = embodimentProcessArea;
	}

	@Column(name = "procedure_purpose", nullable = true)
	public Integer getProcedurePurpose() {
		return procedurePurpose;
	}

	public void setProcedurePurpose(Integer procedurePurpose) {
		this.procedurePurpose = procedurePurpose;
	}

	@Column(name = "serving_staff", nullable = true)
	public Integer getServingStaff() {
		return servingStaff;
	}

	public void setServingStaff(Integer servingStaff) {
		this.servingStaff = servingStaff;
	}

	@Column(name = "complication", nullable = true, length = 10)
	@Length(max = 10)
	public String getComplication() {
		return complication;
	}

	public void setComplication(String complication) {
		this.complication = complication;
	}

	@Column(name = "surgical_embodiment", nullable = true)
	public Integer getSurgicalEmbodiment() {
		return surgicalEmbodiment;
	}

	public void setSurgicalEmbodiment(Integer surgicalEmbodiment) {
		this.surgicalEmbodiment = surgicalEmbodiment;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_id", nullable = false, updatable = false, insertable = false)
	@ForeignKey(name = "service_clinical_history_service_fk")
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "clinical_history_id", nullable = false, updatable = false, insertable = false)
	@ForeignKey(name = "service_clinical_history_clinical_history_fk")
	public ClinicalHistory getClinicalHistory() {
		return clinicalHistory;
	}

	public void setClinicalHistory(ClinicalHistory clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "medical_appointment_id", referencedColumnName = "medical_appointment_id", nullable = true),
			@JoinColumn(name = "medical_service_id", referencedColumnName = "service_id", nullable = true) })
	@ForeignKey(name = "service_clinical_history_medical_appointment_service_fk")
	public MedicalAppointmentService getMedicalAppointmentService() {
		return medicalAppointmentService;
	}

	public void setMedicalAppointmentService(
			MedicalAppointmentService medicalAppointmentService) {
		this.medicalAppointmentService = medicalAppointmentService;
	}

}