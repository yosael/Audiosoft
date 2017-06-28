package com.sa.model.workshop;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Usuario;

@Entity
@Table(name = "etapa_rep_cliente")
public class EtapaRepCliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private EtapaReparacion etapaRep;
	private ReparacionCliente reparacionCli;
	private Date fechaInicio;
	private Date fechaEstFin;
	private Date fechaRealFin;
	private Usuario usuario; 
	private String descripcion;
	private String estado;
	private Integer numDias;
	private List<RequisicionEtapaRep> requisicionesEtapa =  new ArrayList<RequisicionEtapaRep>();
	private String historico;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "etarepcli_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etarep_id", nullable = false)
	@ForeignKey(name = "fk_etarep_cli")
	public EtapaReparacion getEtapaRep() {
		return etapaRep;
	}
	
	public void setEtapaRep(EtapaReparacion etapaRep) {
		this.etapaRep = etapaRep;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repcli_id", nullable = false)
	@ForeignKey(name = "fk_repcli_eta")
	public ReparacionCliente getReparacionCli() {
		return reparacionCli;
	}
	
	public void setReparacionCli(ReparacionCliente reparacionCli) {
		this.reparacionCli = reparacionCli;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_est_fin", nullable = false)
	public Date getFechaEstFin() {
		return fechaEstFin;
	}
	
	public void setFechaEstFin(Date fechaEstFin) {
		this.fechaEstFin = fechaEstFin;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_real_fin", nullable = true)
	public Date getFechaRealFin() {
		return fechaRealFin;
	}
	
	public void setFechaRealFin(Date fechaRealFin) {
		this.fechaRealFin = fechaRealFin;
	}
		
	@Column(name = "descripcion", nullable = true, length = 500)
	public String getDescripcion() {
		return descripcion; 
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "etapaRepCli", cascade = CascadeType.REMOVE)
	public List<RequisicionEtapaRep> getRequisicionesEtapa() {
		return requisicionesEtapa;
	}
	public void setRequisicionesEtapa(List<RequisicionEtapaRep> requisicionesEtapa) {
		this.requisicionesEtapa = requisicionesEtapa;
	}
	
	@Column(name = "estado", nullable = true, length = 3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = true)
	@ForeignKey(name = "fk_repcli_usu")
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_inicio", nullable = true)
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	@Transient
	public Integer getNumDias() {
		return numDias;
	}
	public void setNumDias(Integer numDias) {
		this.numDias = numDias;
	}
	
	
	@Column(name = "historico", nullable = true,length=10)
	public String getHistorico() {
		return historico;
	}
	public void setHistorico(String historico) {
		this.historico = historico;
	}
	
	
	
	
		
}
