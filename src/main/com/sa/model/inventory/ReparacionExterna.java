package com.sa.model.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Empresa;

@Entity
@Table(name = "reparacion_externa")
public class ReparacionExterna implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private Integer idReparacionExterna;
	private String customerNumber;//not null
	private Empresa empresa; // not null
	private Proveedor proveedor; // not null
	private Date fechaCreacion; //not null
	private Date fechaEnvio; //null
	private Date fechaModificacion; //null
	private String estado;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getIdReparacionExterna() {
		return idReparacionExterna;
	}
	
	public void setIdReparacionExterna(Integer idReparacionExterna) {
		this.idReparacionExterna = idReparacionExterna;
	}
	
	@Column(name= "customer_number",nullable=false,length=10)
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_reparacion_externa_empresa")
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	@ForeignKey(name = "fk_reparacion_externa_proveedor")
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", nullable = false)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_envio", nullable = true)
	public Date getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_modificacion", nullable = true)
	public Date getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	
	@Column(name = "estado", nullable = false, length=15)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	

}
