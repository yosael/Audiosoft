package com.sa.model.inventory.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.NotNull;

@Embeddable
public class ItemPedidoId implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer inventarioId;
	private Integer pedidoId;
	
	@Column(name = "inventario_id", nullable = false)
	@NotNull
	public Integer getInventarioId() {
		return inventarioId;
	}
	
	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}
	
	@Column(name = "pedido_id", nullable = false)
	@NotNull
	public Integer getPedidoId() {
		return pedidoId;
	}
	
	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}
	
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ItemPedidoId))
			return false;
		ItemPedidoId castOther = (ItemPedidoId) other;
		return ((this.getInventarioId().equals(castOther.getInventarioId())) && (this
				.getPedidoId().equals(castOther.getPedidoId())));
	}

	public int hashCode() {
		long result = 17;
		result = 37 * result + this.getInventarioId();
		result = (int) (37 * result + this.getPedidoId());
		return (int) result;
	}

}
