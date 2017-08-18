package com.sa.model.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="ItemPrecompra")
public class ItemPrecompra {
	
	
	private Integer id;
	private Compra compra;
	private Inventario producto;
	private Integer cantidad;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra", nullable = false)
	@ForeignKey(name = "fk_compra_itemprecompra")
	public Compra getCompra() {
		return compra;
	}
	public void setCompra(Compra compra) {
		this.compra = compra;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inventario", nullable = false)
	@ForeignKey(name = "fk_inventario_itemprecompra")
	public Inventario getProducto() {
		return producto;
	}
	public void setProducto(Inventario producto) {
		this.producto = producto;
	}
	
	
	@Column(name="cantidad",nullable=false)
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	
	
	

}
