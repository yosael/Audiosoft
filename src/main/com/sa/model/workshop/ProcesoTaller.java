package com.sa.model.workshop;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "proceso_taller")
public class ProcesoTaller implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String descripcion;
	private String prcCode;
	private boolean requiereRev;
	private Set<EtapaReparacion> etapasProceso =  new HashSet<EtapaReparacion>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prctll_id", nullable = false)
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
	
	@Column(name = "descripcion", nullable = false, length = 200)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procesoTll", cascade = CascadeType.REMOVE)
	public Set<EtapaReparacion> getEtapasProceso() {
		return etapasProceso;
	}
	public void setEtapasProceso(Set<EtapaReparacion> etapasProceso) {
		this.etapasProceso = etapasProceso;
	}
	
	@Column(name = "codigo", nullable = false, length = 3, unique = true)
	public String getPrcCode() {
		return prcCode;
	}
	
	public void setPrcCode(String prcCode) {
		this.prcCode = prcCode;
	}
	
	@Column(name = "requiere_rev", nullable = false)
	public boolean isRequiereRev() {
		return requiereRev;
	}
	public void setRequiereRev(boolean requiereRev) {
		this.requiereRev = requiereRev;
	}

	
}
