package com.sa.model.workshop;


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
import org.hibernate.validator.Min;

import com.sa.model.security.AreaNegocio;

@Entity
@Table(name = "etapa_reparacion")
public class EtapaReparacion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String codEta;
	private String descripcion;
	private Integer tiempoEstimado;
	private Short orden;
	private boolean aceptaReqs;
	private ProcesoTaller procesoTll;
	private AreaNegocio areaEncargada;
	private EtapaReparacion loopBack;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "etarep_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length = 30)
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "tiempo_estimado", nullable = false)
	@Min(value=0)
	public Integer getTiempoEstimado() {
		return tiempoEstimado;
	}
	
	public void setTiempoEstimado(Integer tiempoEstimado) {
		this.tiempoEstimado = tiempoEstimado;
	}
	
	@Column(name = "orden", nullable = false)
	@Min(value=1)
	public Short getOrden() {
		return orden;
	}
	
	public void setOrden(Short orden) {
		this.orden = orden;
	}
	
	@Column(name = "descripcion", nullable = false, length = 150)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prctll_id", nullable = false)
	@ForeignKey(name = "fk_prc_eta")
	public ProcesoTaller getProcesoTll() {
		return procesoTll;
	}
	
	public void setProcesoTll(ProcesoTaller procesoTll) {
		this.procesoTll = procesoTll;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "areneg_id", nullable = false)
	@ForeignKey(name = "fk_are_eta")
	public AreaNegocio getAreaEncargada() {
		return areaEncargada;
	}

	public void setAreaEncargada(AreaNegocio areaEncargada) {
		this.areaEncargada = areaEncargada;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etaback_id", nullable = true)
	@ForeignKey(name = "fk_eta_back")
	public EtapaReparacion getLoopBack() {
		return loopBack;
	}

	public void setLoopBack(EtapaReparacion loopBack) {
		this.loopBack = loopBack;
	}

	@Column(name = "acepta_reqs", nullable = false)
	public boolean isAceptaReqs() {
		return aceptaReqs;
	}

	public void setAceptaReqs(boolean aceptaReqs) {
		this.aceptaReqs = aceptaReqs;
	}

	@Column(name = "cod_eta", nullable = false, length=3)
	public String getCodEta() {
		return codEta;
	}

	public void setCodEta(String codEta) {
		this.codEta = codEta;
	}
	
	
}
