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
import org.hibernate.validator.Min;

import com.sa.model.inventory.Producto;


@Entity
@Table(name = "det_venta_prod_serv")
public class DetVentaProdServ implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private VentaProdServ venta;
	private String detalle;
	private Integer cantidad;
	private Float monto;
	private Float costo;
	private String codClasifVta;
	private String codExacto;
	private Service servicio;
	private String numSerie;
	private String numLote;
	private boolean escondido;
	
	//Nuevo 
	private Producto producto;
	private ComboAparato combo;
	private Integer codCoti;
	
	//nuevo el 17/02/2017
	private Float descuentoCorp;
	
	// nuevo el 17/07/2017
	private String tipoVenta; // TLL,CMB, ITM, CST
	
	//nuevo 30/10/2017
	private Integer idConsulta;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vtaprsv_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
		
	@Column(name = "monto", nullable = false)
	public Float getMonto() {
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
	
	@Column(name = "detalle", nullable = false, length = 300)
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venta_id", nullable = false)
	@ForeignKey(name = "fk_vta_dtvta")
	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
	@Column(name = "cantidad", nullable = false)
	@Min(1)
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Column(name = "cod_clasif_vta", nullable = true, length=20)
	public String getCodClasifVta() {
		return codClasifVta;
	}
	public void setCodClasifVta(String codClasifVta) {
		this.codClasifVta = codClasifVta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = true)
	@ForeignKey(name = "fk_srv_vtpd")
	public Service getServicio() {
		return servicio;
	}
	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}
	
	@Column(name = "escondido", nullable = true)
	public boolean isEscondido() {
		return escondido;
	}
	public void setEscondido(boolean escondido) {
		this.escondido = escondido;
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
	
	@Column(name = "cod_exacto", nullable = true, length = 20)
	public String getCodExacto() {
		return codExacto;
	}
	public void setCodExacto(String codExacto) {
		this.codExacto = codExacto;
	}
	
	@Column(name = "costo", nullable = true)
	public Float getCosto() {
		return costo;
	}
	public void setCosto(Float costo) {
		this.costo = costo;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = true)
	@ForeignKey(name = "fk_detVet_prod")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "combo_id", nullable = true)
	@ForeignKey(name = "fk_detVet_combo")
	public ComboAparato getCombo() {
		return combo;
	}
	public void setCombo(ComboAparato combo) {
		this.combo = combo;
	}
	
	
	@Column(name="codCoti",nullable=true)
	public Integer getCodCoti() {
		return codCoti;
	}
	public void setCodCoti(Integer codCoti) {
		this.codCoti = codCoti;
	}
	
	
	@Column(name = "descuento_corp", nullable = true)
	public Float getDescuentoCorp() {
		return descuentoCorp;
	}
	public void setDescuentoCorp(Float descuentoCorp) {
		this.descuentoCorp = descuentoCorp;
	}
	
	
	@Column(name = "tipo_venta", nullable = true,length=3)
	public String getTipoVenta() {
		return tipoVenta;
	}
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	
	@Column(name="id_consulta",nullable=true)
	public Integer getIdConsulta() {
		return idConsulta;
	}
	public void setIdConsulta(Integer idConsulta) {
		this.idConsulta = idConsulta;
	}
	
	
	
	
	
	
	
		
}