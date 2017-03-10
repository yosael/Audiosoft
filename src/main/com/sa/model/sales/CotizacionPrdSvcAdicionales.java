package com.sa.model.sales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

import com.sa.model.inventory.Producto;

@Entity
@Table(name = "cotizacion_prdsvc_adicionales")
public class CotizacionPrdSvcAdicionales{
	private Integer id;
	private CotizacionComboApa cotizacion;	
	private Integer cantidad;
	private String tipPreCotizado;
	private Float precioCotizado;
	private Service servicio;
	private Producto producto;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cotitmadi_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "ctcmap_id", nullable = false)
	@ForeignKey(name = "fk_cotcmb_ctcmap")	
	public CotizacionComboApa getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(CotizacionComboApa cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	@ManyToOne
	@JoinColumn(name = "service_id", nullable = true)
	@ForeignKey(name = "fk_cotitmadi_service")
	public Service getServicio() {
		return servicio;
	}
	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}
	
	@Column (name="cantidad")
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "tip_pre_cotizado", nullable = true, length = 255)
	@Length(max = 255)
	public String getTipPreCotizado() {
		return tipPreCotizado;
	}
	public void setTipPreCotizado(String tipPreCotizado) {
		this.tipPreCotizado = tipPreCotizado;
	}
	
	@Column (name="precio_cotizado")
	public Float getPrecioCotizado() {
		return precioCotizado;
	}
	public void setPrecioCotizado(Float precioCotizado) {
		this.precioCotizado = precioCotizado;
	}
	
	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = true)
	@ForeignKey(name = "fk_cotitmadi_producto")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}


}
