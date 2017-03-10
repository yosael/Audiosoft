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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Producto;

@Entity
@Table(name = "cotizacion_cmb_itm")
public class CotizacionComboItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private CotizacionComboApa cotizacion;
	private Categoria categoria;
	private Producto producto;
	private Short cantidad;
	private Double monto;
	private String descripcion;
	private boolean principal;
	private CodProducto codProducto;
	private Inventario inventario;
	private String tipoPrecio = "NRM";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "itctap_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "combo_id", nullable = false)
	@ForeignKey(name = "fk_cot_ctcmap")
	public CotizacionComboApa getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(CotizacionComboApa cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = true)
	@ForeignKey(name = "fk_cat_ctcmap")
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = true)
	@ForeignKey(name = "fk_prd_ctcmap")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@Column(name = "cantidad", nullable = false)
	@Min(1)
	public Short getCantidad() {
		return cantidad;
	}
	public void setCantidad(Short cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "descripcion", nullable = true, length = 100)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "principal", nullable = false)
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
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
	
	@Column(name = "tipo_precio", nullable = false, length = 3 ) 
	public String getTipoPrecio() {
		return tipoPrecio;
	}
	public void setTipoPrecio(String tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}
	
	@Column(name = "monto", nullable = false ) 
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	
	
		
}
