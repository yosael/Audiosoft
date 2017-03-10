package com.sa.model.workshop;


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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Producto;

@Entity
@Table(name = "item_requisicion_eta")
public class ItemRequisicionEta implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private RequisicionEtapaRep reqEtapa;
	private Integer cantidad;
	private Producto producto;
	private String numSerie;
	private String numLote;
	private Inventario inventario;
	private CodProducto codProducto;
	private String ubicacionActual;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "itmreq_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reqeta_id", nullable = false)
	@ForeignKey(name = "fk_reqeta_itm")
	public RequisicionEtapaRep getReqEtapa() {
		return reqEtapa;
	}
	
	public void setReqEtapa(RequisicionEtapaRep reqEtapa) {
		this.reqEtapa = reqEtapa;
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
	@JoinColumn(name = "id", nullable = false)
	@ForeignKey(name = "fk_prd_reqitm")
	public Producto getProducto() {
		return producto;
	}
	
	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	@Column(name = "num_serie", nullable = true, length = 400)
	public String getNumSerie() {
		if(numSerie == null)
			return "";
		return numSerie;
	}

	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	@Column(name = "num_lote", nullable = true, length = 400)
	public String getNumLote() {
		if(numLote == null)
			return "";
		return numLote;
	}

	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}
	
	@Column(name = "ubicacion_actual", nullable = true, length = 20)
	public String getUbicacionActual() {
		return ubicacionActual;
	}
	public void setUbicacionActual(String ubicacionActual) {
		this.ubicacionActual = ubicacionActual;
	}
	
	@Transient
	public CodProducto getCodProducto() {
		return codProducto;
	}
	public void setCodProducto(CodProducto codProducto) {
		this.codProducto = codProducto;
	}

	@Transient
	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}
	
	
}
