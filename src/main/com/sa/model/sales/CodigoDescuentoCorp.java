package com.sa.model.sales;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.medical.ClienteCorporativo;


@Entity
@Table(name = "codigo_descuento_corp")
public class CodigoDescuentoCorp implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	
	Integer id;
	String codigo;//debe ser unico
	ClienteCorporativo clienteCorp;
	Date fechaEmision;
	Date fechaFinalizacion;
	String estado;	//ACT=ACTIVO  INA=INACTIVO   CAD=CADUCADO
	
	List<DetDescuentoCorp> detDescuentos;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_codigo_desc", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "codigo", nullable = false,length=6)//, unique=true
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_clicorp", nullable = true)
	@ForeignKey(name = "fk_codigo_clicorp")
	public ClienteCorporativo getClienteCorp() {
		return clienteCorp;
	}
	public void setClienteCorp(ClienteCorporativo clienteCorp) {
		this.clienteCorp = clienteCorp;
	}
	
	
	@Column(name = "fecha_emision", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	
	@Column(name = "fecha_finalizacion", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}
	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}
	
	@Column(name = "estado", nullable = true,length=3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "codigo", cascade = CascadeType.REMOVE)
	public List<DetDescuentoCorp> getDetDescuentos() {
		return detDescuentos;
	}
	public void setDetDescuentos(List<DetDescuentoCorp> detDescuentos) {
		this.detDescuentos = detDescuentos;
	}

	
	
	
	
	
}
