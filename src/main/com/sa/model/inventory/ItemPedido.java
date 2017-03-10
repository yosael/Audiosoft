package com.sa.model.inventory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.id.ItemPedidoId;

@Entity
@Table(name = "item_pedido")
public class ItemPedido implements Serializable{

	private static final long serialVersionUID = 1L;
	private ItemPedidoId itemPedidoId;
	private Integer cantidad;
	private Inventario inventario;
	private Pedido pedido;
	private float costoUnitario;
	
	public ItemPedido(){
		this.itemPedidoId = new ItemPedidoId();
	}
	
	@EmbeddedId
	public ItemPedidoId getItemPedidoId() {
		return itemPedidoId;
	}
	
	public void setItemPedidoId(ItemPedidoId itemPedidoId) {
		this.itemPedidoId = itemPedidoId;
	}

	@Column(name = "cantidad", nullable = false)
	@Min(value=1)
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventario_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_pedido_inventario")
	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_pedido_pedido")
	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	@Column(name = "costo_unitario", nullable = false)
	@Min(value=1)
	public float getCostoUnitario() {
		return costoUnitario;
	}

	public void setCostoUnitario(float costoUnitario) {
		this.costoUnitario = costoUnitario;
	}

}
