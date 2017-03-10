package com.sa.model.inventory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "pedido")
public class Pedido implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String descripcion;
	private Date fechaInicio;
	private Date fechaFinalizo;
	private String estado; // P=Pendiente; A=Aprobados; R=Rechazado;
	private Compra compra;
	private Sucursal sucursal;
	private Proveedor proveedor;
	private Set<ItemPedido> items = new HashSet<ItemPedido>();
	private Usuario usuario;
	private Float subtotal;
	
	public Pedido(){
		this.estado="P";
		this.fechaInicio=new Date();
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "descripcion", nullable = true)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_inicio", nullable = false)
	@NotNull
	public Date getFechaInicio() {
		return fechaInicio;
	}
	
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_finalizado", nullable = true)
	public Date getFechaFinalizo() {
		return fechaFinalizo;
	}
	
	public void setFechaFinalizo(Date fechaFinalizo) {
		this.fechaFinalizo = fechaFinalizo;
	}
	
	@Column(name = "estado", nullable = false)
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compra_id", nullable = true)
	@ForeignKey(name = "fk_pedido_compra")
	public Compra getCompra() {
		return compra;
	}
	public void setCompra(Compra compra) {
		this.compra = compra;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_pedido_sucuarsal")
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	@ForeignKey(name = "fk_pedido_proveedor")
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedido", cascade = CascadeType.REMOVE)
	public Set<ItemPedido> getItems() {
		return items;
	}

	public void setItems(Set<ItemPedido> items) {
		this.items = items;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	@ForeignKey(name = "fk_pedido_usuario")
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Float getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(Float subtotal) {
		this.subtotal = subtotal;
	}

	
}
