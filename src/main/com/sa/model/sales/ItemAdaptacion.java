package com.sa.model.sales;

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

import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Producto;


@Entity
@Table(name = "item_adaptacion")
public class ItemAdaptacion {
	
	private Integer id;
	private AdaptacionCombo adaptacion;
	private Categoria categoria;
	private Producto producto;
	private Service servicio;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_item_adaptacion",nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_adaptacion",nullable=false)
	@ForeignKey(name = "fk_item_adaptacion")
	public AdaptacionCombo getAdaptacion() {
		return adaptacion;
	}
	public void setAdaptacion(AdaptacionCombo adaptacion) {
		this.adaptacion = adaptacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="id_categoria",nullable=true)
	@ForeignKey(name="fk_adaptacion_categoria")
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_producto",nullable=true)
	@ForeignKey(name="fk_adaptacion_producto")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servicio",nullable=true)
	@ForeignKey(name = "fk_adaptacion_servicio")
	public Service getServicio() {
		return servicio;
	}
	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}

}
