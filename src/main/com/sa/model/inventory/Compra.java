package com.sa.model.inventory;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "compra", uniqueConstraints = @UniqueConstraint(columnNames = "numero_factura"))
public class Compra extends Movimiento{

	private static final long serialVersionUID = 1L;
	private String numeroFactura;
	private Float subTotal;
	private String formaPago;
	private Pedido pedido;
	private String estado;
	
	public Compra(){
		super.setFecha(new Date());
	}
	
	@Column(name = "numero_factura", nullable = true, length = 20)
	public String getNumeroFactura() {
		return numeroFactura;
	}
	
	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}
	
	@Column(name = "sub_total", nullable = false)
	public Float getSubTotal() {
		return subTotal;
	}
	
	public void setSubTotal(Float subTotal) {
		this.subTotal = subTotal;
	}
	
	@Column(name = "forma_pago", nullable = true)
	public String getFormaPago() {
		return formaPago;
	}
	
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "compra", cascade = CascadeType.REMOVE)
	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	
	@Column(name = "estado_compra", nullable = true,length=15)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	
	
}
