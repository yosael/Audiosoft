package com.sa.model.vta;

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

import com.sa.model.inventory.Inventario;

@Entity 
@Table(name = "det_venta_doc")
public class DetVentaDoc implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private VentaDoc venta;
	private Integer cantidad;
	private String detalle;
	private Float precioUnitario;
	private Float total;
	private String tipo;
	private Inventario producto;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dtvtadoc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vtadoc_id", nullable = true)
	@ForeignKey(name = "fk_detventadoc_ventadoc")
	public VentaDoc getVenta() {
		return venta;
	}

	public void setVenta(VentaDoc venta) {
		this.venta = venta;
	}

	@Column(name = "cantidad", nullable = false)
	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Column(name = "detalle", nullable = false, length = 300)
	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	@Column(name = "precio_unitario", nullable = false)
	public Float getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(Float precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	@Column(name = "total", nullable = false)
	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	@Column(name = "tipo", nullable = false, length = 1)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventario_id", nullable = true)
	@ForeignKey(name = "fk_detventadoc_inventario")
	public Inventario getProducto() {
		return producto;
	}

	public void setProducto(Inventario producto) {
		this.producto = producto;
	}	
}
