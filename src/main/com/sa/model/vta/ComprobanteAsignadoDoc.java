package com.sa.model.vta;

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

import com.sa.model.security.Sucursal;

@Entity 
@Table(name = "comprobante_asignado_doc")
public class ComprobanteAsignadoDoc {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 	
	private ComprobanteImpresion comprobante;
	private String serie;
	private Integer numInicio;
	private Integer numFinal;
	private Integer numActual;
	private Sucursal sucursal;
	private Date fechaAsignacion;
	private Date fechaFinalizacion;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cprimp_id", nullable = true)
	@ForeignKey(name = "fk_compimp_cprasg")
	public ComprobanteImpresion getComprobante() {
		return comprobante;
	}

	public void setComprobante(ComprobanteImpresion comprobante) {
		this.comprobante = comprobante;
	}

	@Column(name = "serie", nullable = true, length = 15)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(name = "num_inicio", nullable = false)
	public Integer getNumInicio() {
		return numInicio;
	}

	public void setNumInicio(Integer numInicio) {
		this.numInicio = numInicio;
	}

	@Column(name = "num_final", nullable = false)
	public Integer getNumFinal() {
		return numFinal;
	}

	public void setNumFinal(Integer numFinal) {
		this.numFinal = numFinal;
	}

	@Column(name = "num_actual", nullable = false)
	public Integer getNumActual() {
		return numActual;
	}

	public void setNumActual(Integer numActual) {
		this.numActual = numActual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = true)
	@ForeignKey(name = "fk_suc_cprasg")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_asignacion", nullable = false)
	public Date getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(Date fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_finalizacion", nullable = true)
	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}

	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}

	
}
