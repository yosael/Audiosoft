package com.sa.model.medical.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class BillId implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer officeId;
	private Long consecutive;

	@Column(name = "office_id", nullable = false)
	@NotNull
	public Integer getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Integer officeId) {
		this.officeId = officeId;
	}

	@Column(name = "consecutive", nullable = false)
	@NotNull
	public Long getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(Long consecutive) {
		this.consecutive = consecutive;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BillId))
			return false;
		BillId castOther = (BillId) other;
		return ((this.getOfficeId().equals(castOther.getOfficeId())) && (this
				.getConsecutive().equals(castOther.getConsecutive())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getOfficeId();
		result = (int) (37 * result + this.getConsecutive());
		return (int) result;
	}

}