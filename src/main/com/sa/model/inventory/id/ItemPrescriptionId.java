package com.sa.model.inventory.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class ItemPrescriptionId implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer inventarioId;
	private Integer prescriptionId;
	
	@Column(name = "inventario_id", nullable = false)
	@NotNull
	public Integer getInventarioId() {
		return inventarioId;
	}
	
	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}
	
	@Column(name = "prescription_id", nullable = false)
	@NotNull
	public Integer getPrescriptionId() {
		return prescriptionId;
	}
	
	public void setPrescriptionId(Integer prescriptionId) {
		this.prescriptionId = prescriptionId;
	}
	
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemPrescriptionId))
			return false;
		ItemPrescriptionId castOther = (ItemPrescriptionId) other;
		return ((this.getInventarioId().equals(castOther.getInventarioId())) && (this
				.getPrescriptionId().equals(castOther.getPrescriptionId())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getInventarioId();
		result = (int) (37 * result + this.getPrescriptionId());
		return (int) result;
	}

}
