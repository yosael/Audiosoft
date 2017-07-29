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

import com.sa.model.inventory.Producto;

@Entity
@Table(name = "cotizacion_combos_items")
public class CotCmbsItems {
	Integer id;
	CotizacionCombos ctCmbs;
	ItemComboApa item;//Este valor ya no se utiliza. Se dejo para no afectar a valores historicos. Se quito porque al tener items de combos solo con categorias. Al realizar este registro el producto se registraba en los items del combo y generaba inconsistencias en las cotizaciones
	Producto producto;// nuevo agregado el 27/07/2017. Este es el valor utlizado para saber que es lo que se cotizo
	private String tipoPrecio;
	private Float precioCotizado;
	
	// nuevo agregado el 13/07/2017 .. Aqui se guardaran SOLO los servicios que se han agregado desde la adaptacion del combo. Debido a que no estan asociados directamnte al combo. solo cuando se requiera
	private Service servicioCotizado;
	
	//nuevo agregado el27/07/2017
	private Short cantidad;
	private Boolean principal;
	private Boolean generaRequisicion;
	private String nombreAdaptacion;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctcbitm_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "ctcmbs_id", nullable = false)
	@ForeignKey(name = "fk_ctcbitm_ctcmbs")
	public CotizacionCombos getCtCmbs() {
		return ctCmbs;
	}
	public void setCtCmbs(CotizacionCombos ctCmbs) {
		this.ctCmbs = ctCmbs;
	}
	
	@ManyToOne
	@JoinColumn(name = "itcbap_id", nullable = true)
	@ForeignKey(name = "fk_ctcbitm_itcbap")
	public ItemComboApa getItem() {
		return item;
	}
	
	public void setItem(ItemComboApa item) {
		this.item = item;
	}
	
	@Column (name="tipo_precio")
	public String getTipoPrecio() {
		return tipoPrecio;
	}
	public void setTipoPrecio(String tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}
	
	@Column (name="precio_cotizado")
	public Float getPrecioCotizado() {
		return precioCotizado;
	}
	public void setPrecioCotizado(Float precioCotizado) {
		this.precioCotizado = precioCotizado;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "id_servicio", nullable = true)
	@ForeignKey(name = "fk_servicio_itcbap")
	public Service getServicioCotizado() {
		return servicioCotizado;
	}
	public void setServicioCotizado(Service servicioCotizado) {
		this.servicioCotizado = servicioCotizado;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "id_producto", nullable = true)
	@ForeignKey(name = "fk_cotcmbitm_producto")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@Column(name="cantidad",nullable=true)
	public Short getCantidad() {
		return cantidad;
	}
	public void setCantidad(Short cantidad) {
		this.cantidad = cantidad;
	}
	
	
	@Column(name="principal",nullable=true)
	public Boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	
	@Column(name="genera_requisicion",nullable=true)
	public Boolean isGeneraRequisicion() {
		return generaRequisicion;
	}
	public void setGeneraRequisicion(Boolean generaRequisicion) {
		this.generaRequisicion = generaRequisicion;
	}
	
	@Column(name="nombre_adaptacion",nullable=true,length=50)
	public String getNombreAdaptacion() {
		return nombreAdaptacion;
	}
	public void setNombreAdaptacion(String nombreAdaptacion) {
		this.nombreAdaptacion = nombreAdaptacion;
	}
	
	
	
	
	
	
	
	
	
}
