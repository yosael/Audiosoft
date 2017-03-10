package com.sa.model.inventory.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class ItemId implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer inventarioId;
	private Integer movimientoId;
	
	@Column(name = "inventario_id", nullable = true)
	@NotNull
	public Integer getInventarioId() {
		return inventarioId;
	}
	
	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}
	
	@Column(name = "movimiento_id", nullable = true)
	@NotNull
	public Integer getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Integer movimientoId) {
		this.movimientoId = movimientoId;
	}
	
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemId))
			return false;
		ItemId castOther = (ItemId) other;
		return ((this.getInventarioId().equals(castOther.getInventarioId())) && (this
				.getMovimientoId().equals(castOther.getMovimientoId())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getInventarioId();
		result = (int) (37 * result + this.getMovimientoId());
		return (int) result;
	}

	

}
