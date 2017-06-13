package com.sa.model.medical;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.medical.id.MedicalAppointmentServiceId;
import com.sa.model.sales.Service;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:39
 */
@Entity
@Table(name = "medical_appointment_service")
public class MedicalAppointmentService {
 
	private MedicalAppointmentServiceId medicalAppointmentServiceId;
	private Service service;
	private MedicalAppointment medicalAppointment;
	private ServiceClinicalHistory serviceClinicalHistory;

	public MedicalAppointmentService() {
	}

	@EmbeddedId
	public MedicalAppointmentServiceId getMedicalAppointmentServiceId() {
		return medicalAppointmentServiceId;
	}

	public void setMedicalAppointmentServiceId(
			MedicalAppointmentServiceId medicalAppointmentServiceId) {
		this.medicalAppointmentServiceId = medicalAppointmentServiceId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "service_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "medical_appointmet_service_service_fk")
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "medical_appointment_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "medical_appointmet_service_medical_appointmet_fk")
	public MedicalAppointment getMedicalAppointment() {
		return medicalAppointment;
	}

	public void setMedicalAppointment(MedicalAppointment medicalAppointment) {
		this.medicalAppointment = medicalAppointment;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "medicalAppointmentService")
	public ServiceClinicalHistory getServiceClinicalHistory() {
		return serviceClinicalHistory;
	}

	public void setServiceClinicalHistory(
			ServiceClinicalHistory serviceClinicalHistory) {
		this.serviceClinicalHistory = serviceClinicalHistory;
	}

}
