package com.sa.model.security;

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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.Max;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotEmpty;

import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Pedido;
import com.sa.model.inventory.Transferencia;
import com.sa.model.medical.Doctor;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = "nombre_usuario"))
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombreUsuario;
	private String nombreCompleto;
	private String pass;
	private Date fechaUltimoAcceso; 
	private Integer numeroIntentos;
	private String estado;//ACT=Activo;INA=Inactivo
	private Sucursal sucursal;
	private boolean accionEspecial;
	private boolean notificacionInv = false;
	private boolean notificacionMed = false;
	private boolean autorizDesc = false;
	private AreaNegocio areaUsuario;
	private Set<Rol> roles = new HashSet<Rol>();
	private Set<Movimiento> movimientos = new HashSet<Movimiento>();
	private Set<Pedido> pedidos = new  HashSet<Pedido>();
	private Set<Transferencia> transferencias = new HashSet<Transferencia>();
	private Doctor doctorReg;
	
	public Usuario(){
		this.numeroIntentos = 0;
		this.estado = "ACT";
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

	@Column(name = "nombre_usuario", nullable = false, length = 20)
	@Length(max = 20)
	@NotEmpty
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	@Column(name = "pass", nullable = false, length = 60)
	@Length(max = 60)
	@NotEmpty
	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ultimo_acceso", nullable = true)
	public Date getFechaUltimoAcceso() {
		return fechaUltimoAcceso;
	}

	public void setFechaUltimoAcceso(Date fechaUltimoAcceso) {
		this.fechaUltimoAcceso = fechaUltimoAcceso;
	}

	@Column(name = "numero_intentos", nullable = false)
	@Max(value = 5)
	@Min(value = 0)
	public Integer getNumeroIntentos() {
		return numeroIntentos;
	}

	public void setNumeroIntentos(Integer numeroIntentos) {
		this.numeroIntentos = numeroIntentos;
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
	@JoinColumn(name = "sucursal_id", nullable = true)
	@ForeignKey(name = "sucursal_usuario_fk")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "rol_usuario", 
		joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"), 
				inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "id"))
	@ForeignKey(name = "fk_rol_usuario", inverseName = "fk_usuario_rol")
	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.REMOVE)
	public Set<Movimiento> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(Set<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.REMOVE)
	public Set<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(Set<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioGenera", cascade = CascadeType.REMOVE)
	public Set<Transferencia> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(Set<Transferencia> transferencias) {
		this.transferencias = transferencias;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "areneg_id", nullable = true)
	@ForeignKey(name = "are_usu_fk")
	public AreaNegocio getAreaUsuario() {
		return areaUsuario;
	}

	public void setAreaUsuario(AreaNegocio areaUsuario) {
		this.areaUsuario = areaUsuario;
	}

	@Column(name = "nombre_completo", nullable = true, length = 100)
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.REMOVE)
	@JoinColumn(nullable = true)  
	public Doctor getDoctorReg() {
		return doctorReg;
	}

	public void setDoctorReg(Doctor doctor) {
		this.doctorReg = doctor;
	}
	
	@Column(name = "accion_especial", nullable = false)
	public boolean isAccionEspecial() {
		return accionEspecial;
	}

	public void setAccionEspecial(boolean accionEspecial) {
		this.accionEspecial = accionEspecial;
	}

	@Column(name = "notificacion_inv", nullable = false)
	public boolean isNotificacionInv() {
		return notificacionInv;
	}

	public void setNotificacionInv(boolean notificacionInv) {
		this.notificacionInv = notificacionInv;
	}

	@Column(name = "notificacion_med", nullable = false)
	public boolean isNotificacionMed() {
		return notificacionMed;
	}

	public void setNotificacionMed(boolean notificacionMed) {
		this.notificacionMed = notificacionMed;
	}

	@Column(name = "autoriz_descuento", nullable = false)
	public boolean isAutorizDesc() {
		return autorizDesc;
	}

	public void setAutorizDesc(boolean autorizDesc) {
		this.autorizDesc = autorizDesc;
	}
	
	
}
