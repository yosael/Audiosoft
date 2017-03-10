package com.sa.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Pedido;
import com.sa.model.inventory.Transferencia;
import com.sa.model.inventory.UbicacionPrd;

@Entity
@Table(name = "sucursal", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Sucursal implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo; //TODO EN MAYUSCULA
	private String nombre;
	private String estado; //ACT=Activo; INA=INACTIVO;
	private Empresa empresa;
	private Sucursal sucursalSuperior;
	private List<Sucursal> subSucursales = new ArrayList<Sucursal>();
	private Set<Usuario> usuarios = new HashSet<Usuario>();
	private Set<Movimiento> movimientos = new HashSet<Movimiento>();
	private Set<Transferencia> transferenciasRecibidas = new HashSet<Transferencia>();
	private List<UbicacionPrd> ubicacionesPrd = new ArrayList<UbicacionPrd>();
	
	private Set<Pedido> pedidos = new HashSet<Pedido>();
	private List<Inventario> inventarios = new ArrayList<Inventario>();
	private Integer totalInvs;
	
	//nuevo campo
	private Boolean bodega;
	
	/*public Sucursal(){
		this.estado = "ACT";
	}*/
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "codigo", nullable = false, length = 8)
	@Length(max = 8)
	@NotEmpty
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nombre", nullable = false, length = 50)
	@Length(max = 50)
	@NotEmpty
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "estado", nullable = false, length = 3)
	@Length(max = 3)
	@NotEmpty
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_sucursal_empresa")
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursal", cascade = CascadeType.REMOVE)
	public Set<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursal", cascade = CascadeType.REMOVE)
	public Set<Movimiento> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(Set<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursalDestino", cascade = CascadeType.REMOVE)
	public Set<Transferencia> getTransferenciasRecibidas() {
		return transferenciasRecibidas;
	}

	public void setTransferenciasRecibidas(
			Set<Transferencia> transferenciasRecibidas) {
		this.transferenciasRecibidas = transferenciasRecibidas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursal", cascade = CascadeType.REMOVE)
	public Set<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(Set<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursal", cascade = CascadeType.REMOVE)
	public List<Inventario> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<Inventario> inventarios) {
		this.inventarios = inventarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursal", cascade = CascadeType.REMOVE)
	public List<UbicacionPrd> getUbicacionesPrd() {
		return ubicacionesPrd;
	}

	public void setUbicacionesPrd(List<UbicacionPrd> ubicacionesPrd) {
		this.ubicacionesPrd = ubicacionesPrd;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucsuperior_id", nullable = true)
	@ForeignKey(name = "fk_sucsup_suc")
	public Sucursal getSucursalSuperior() {
		return sucursalSuperior;
	}

	public void setSucursalSuperior(Sucursal sucursalSuperior) {
		this.sucursalSuperior = sucursalSuperior;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sucursalSuperior", cascade = CascadeType.REMOVE)
	public List<Sucursal> getSubSucursales() {
		return subSucursales;
	}

	public void setSubSucursales(List<Sucursal> subSucursales) {
		this.subSucursales = subSucursales;
	}

	@Transient
	public Integer getTotalInvs() {
		return totalInvs;
	}

	public void setTotalInvs(Integer totalInvs) {
		this.totalInvs = totalInvs;
	}

	@Column(name="bodega",nullable=true)
	public Boolean getBodega() {
		return bodega;
	}

	public void setBodega(Boolean bodega) {
		this.bodega = bodega;
	}


	
	
	

}
