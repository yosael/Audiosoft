package com.sa.model.sales;

import java.io.Serializable;

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

import com.sa.model.inventory.Producto;
//import com.sa.model.medical.ClienteCorporativo;

@Entity
@Table(name = "det_descuento_corp")
public class DetDescuentoCorp implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	Integer idDetCod;
	CodigoDescuentoCorp codigo;
	Producto producto;
	Service service;
	Float descuento;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_det_des_corp", nullable = false)
	public Integer getIdDetCod() {
		return idDetCod;
	}
	public void setIdDetCod(Integer idDetCod) {
		this.idDetCod = idDetCod;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_codigo_desc", nullable = true)
	@ForeignKey(name = "fk_descorp_coddescorp")
	public CodigoDescuentoCorp getCodigo() {
		return codigo;
	}
	public void setCodigo(CodigoDescuentoCorp codigo) {
		this.codigo = codigo;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_descorp_producto", nullable = true)
	@ForeignKey(name = "fk_descorp_producto")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_descorp_serv", nullable = true)
	@ForeignKey(name = "fk_descorp_servicio")
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	
	@Column(name = "descuento", nullable = false)
	public Float getDescuento() {
		return descuento;
	}
	public void setDescuento(Float descuento) {
		this.descuento = descuento;
	}
	
	

}
