package com.sa.model.medical.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class ServiceClinicalHistoryId implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer serviceId;
	private Long clinicalHistory;

	public ServiceClinicalHistoryId() {

	}

	public ServiceClinicalHistoryId(Integer serviceId, Long clinicalHistory) {
		super();
		this.serviceId = serviceId;
		this.clinicalHistory = clinicalHistory;
	}

	@Column(name = "service_id", nullable = false)
	@NotNull
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	@Column(name = "clinical_history_id", nullable = false)
	@NotNull
	public Long getClinicalHistory() {
		return clinicalHistory;
	}

	public void setClinicalHistory(Long clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ServiceClinicalHistoryId))
			return false;
		ServiceClinicalHistoryId castOther = (ServiceClinicalHistoryId) other;
		return ((this.getServiceId().equals(castOther.getServiceId())) && (this
				.getClinicalHistory().equals(castOther.getClinicalHistory())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getServiceId();
		result = (int) (37 * result + this.getClinicalHistory());
		return (int) result;
	}

}