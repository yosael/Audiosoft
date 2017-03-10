package com.sa.model.sales;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.security.Empresa;

@Entity
@Table(name = "cert_descuento")
public class CertDescuento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codCert;
	private Empresa empresa;
	private Date fechaIngreso;
	private Short valDescuento;
	private Short tiempoValidez;
	private String descripcion;
	private String estado;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crtdsc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "cod_cert", nullable = false, length=20)
	public String getCodCert() {
		return codCert;
	}
	public void setCodCert(String codCert) {
		this.codCert = codCert;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = true)
	@ForeignKey(name = "fk_emp_crtdsc")
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	@Column(name = "fecha_ingreso", nullable = false)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	@Column(name = "val_descuento", nullable = false)
	@Min(1)
	public Short getValDescuento() {
		return valDescuento;
	}
	public void setValDescuento(Short valDescuento) {
		this.valDescuento = valDescuento;
	}
		
	@Column(name = "tiempo_validez", nullable = false)
	@Min(1)
	public Short getTiempoValidez() {
		return tiempoValidez;
	}
	public void setTiempoValidez(Short tiempoValidez) {
		this.tiempoValidez = tiempoValidez;
	}
	
	@Column(name = "descripcion", nullable = true, length=150)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "estado", nullable = true, length=3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
}
