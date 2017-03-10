package com.sa.model.medical.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

@Embeddable
public class ThirdId implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer identificationTypeId;
	private String numId;

	@Column(name = "identification_type_id", nullable = false)
	@NotNull
	public Integer getIdentificationTypeId() {
		return identificationTypeId;
	}

	public void setIdentificationTypeId(Integer identificationTypeId) {
		this.identificationTypeId = identificationTypeId;
	}

	@Column(name = "num_id", nullable = false)
	@Pattern(regex = "([0-9]+)|([0-9]+-[0-9]{1})", message = "#{ofiuco_messages.get('thirdDAO_error1')}")
	@NotNull
	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ThirdId))
			return false;
		ThirdId castOther = (ThirdId) other;
		return ((this.getIdentificationTypeId().equals(castOther
				.getIdentificationTypeId())) && (this.getNumId()
				.equals(castOther.getNumId())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getIdentificationTypeId();
		result = result + this.getNumId().hashCode();
		return (int) result;
	}

}