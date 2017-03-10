package com.sa.model.medical.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class MedicalAppointmentServiceId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer medicalAppointmentId;
	private Integer serviceId;

	public MedicalAppointmentServiceId() {
		super();
	}

	public MedicalAppointmentServiceId(Integer medicalAppointmentId,
			Integer procedureId) {
		super();
		this.medicalAppointmentId = medicalAppointmentId;
		this.serviceId = procedureId;
	}

	@Column(name = "medical_appointment_id", nullable = false)
	@NotNull
	public Integer getMedicalAppointmentId() {
		return medicalAppointmentId;
	}

	public void setMedicalAppointmentId(Integer medicalAppointmentId) {
		this.medicalAppointmentId = medicalAppointmentId;
	}

	@Column(name = "service_id", nullable = false)
	@NotNull
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BillId))
			return false;
		BillId castOther = (BillId) other;
		return ((this.getMedicalAppointmentId().equals(castOther.getOfficeId())) && (this
				.getServiceId().equals(castOther.getConsecutive())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getMedicalAppointmentId();
		result = (int) (37 * result + this.getServiceId());
		return (int) result;
	}

}
