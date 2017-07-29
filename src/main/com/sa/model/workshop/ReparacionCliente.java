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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.crm.Cliente;
import com.sa.model.inventory.DetalleReparacionExterna;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "reparacion_cliente")
@Inheritance(strategy = InheritanceType.JOINED)
public class ReparacionCliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaIngreso;
	private Date fechaFin;
	private Date fechaEntrega;
	private String detalleEntrega;
	private String nombreRecibe;
	private Float costo;
	private String descripcion;
	private Sucursal sucursal;
	private Cliente cliente;
	private ProcesoTaller proceso;
	private AparatoCliente aparatoRep;
	private String estado;
	private String lblEstado;
	private String currEtaNom;
	private boolean aprobada;
	private List<EtapaRepCliente> etapasReparacion = new ArrayList<EtapaRepCliente>();
	private List<CondAparatoRep> condsAparatorep = new ArrayList<CondAparatoRep>();
	private List<ComponenteDefRep> compsDefAparato = new ArrayList<ComponenteDefRep>();
	private List<DefCapsulaRep> defCapsAparato = new ArrayList<DefCapsulaRep>();
	private List<ServicioReparacion> serviciosRep = new ArrayList<ServicioReparacion>();
	private EtapaRepCliente currEtapa;
	private String tipoRep;
	private Integer idCot;
	private String estadoRepExterna;
	private Float adelanto;
	
	private List<DetalleReparacionExterna> detallesReparacionExterna = new ArrayList<DetalleReparacionExterna>();
	
	
	// nuevo el 20/07/2017
	private Float ingresosTaller;
	private Float descuentos;
	private Float descuentos_garantia;
	
	//nuevo agregado el 20/07/2017
	private Usuario usuarioRecibe;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "repcli_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cli_id", nullable = false)
	@ForeignKey(name = "fk_cli_rep")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apacli_id", nullable = true)
	@ForeignKey(name = "fk_apacli_rep")
	public AparatoCliente getAparatoRep() {
		return aparatoRep;
	}

	public void setAparatoRep(AparatoCliente aparatoRep) {
		this.aparatoRep = aparatoRep;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reparacionCli", cascade = CascadeType.REMOVE)
	@OrderBy("fechaEstFin ASC") // se hizo para que el reporte de la reparacion muestre un orden cronologico de las etapas de reparacion.
	public List<EtapaRepCliente> getEtapasReparacion() {
		return etapasReparacion;
	}

	public void setEtapasReparacion(List<EtapaRepCliente> etapasReparacion) {
		this.etapasReparacion = etapasReparacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ingreso", nullable = false)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	@Column(name = "costo", nullable = true)
	@Min(value = 0)
	public Float getCosto() {
		return costo;
	}

	public void setCosto(Float costo) {
		this.costo = costo;
	}

	@Column(name = "descripcion", nullable = true, length = 400)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prctll_id", nullable = false)
	@ForeignKey(name = "fk_prc_rep")
	public ProcesoTaller getProceso() {
		return proceso;
	}

	public void setProceso(ProcesoTaller proceso) {
		this.proceso = proceso;
	}

	@Column(name = "estado", nullable = true, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "repCliente", cascade = CascadeType.REMOVE)
	public List<CondAparatoRep> getCondsAparatorep() {
		return condsAparatorep;
	}

	public void setCondsAparatorep(List<CondAparatoRep> condsAparatorep) {
		this.condsAparatorep = condsAparatorep;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "repCliente", cascade = CascadeType.REMOVE)
	public List<ComponenteDefRep> getCompsDefAparato() {
		return compsDefAparato;
	}

	public void setCompsDefAparato(List<ComponenteDefRep> compsDefAparato) {
		this.compsDefAparato = compsDefAparato;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "repCliente", cascade = CascadeType.REMOVE)
	public List<DefCapsulaRep> getDefCapsAparato() {
		return defCapsAparato;
	}

	public void setDefCapsAparato(List<DefCapsulaRep> defCapsAparato) {
		this.defCapsAparato = defCapsAparato;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_fin", nullable = true)
	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_suc_rep")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reparacion", cascade = CascadeType.REMOVE)
	public List<ServicioReparacion> getServiciosRep() {
		return serviciosRep;
	}

	public void setServiciosRep(List<ServicioReparacion> serviciosRep) {
		this.serviciosRep = serviciosRep;
	}

	public boolean isAprobada() {
		return aprobada;
	}

	public void setAprobada(boolean aprobada) {
		this.aprobada = aprobada;
	}

	@Transient
	public EtapaRepCliente getCurrEtapa() {
		return currEtapa;
	}

	public void setCurrEtapa(EtapaRepCliente currEtapa) {
		this.currEtapa = currEtapa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_entrega", nullable = true)
	public Date getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	@Column(name = "detalle_entrega", nullable = true, length = 100)
	public String getDetalleEntrega() {
		return detalleEntrega;
	}

	public void setDetalleEntrega(String detalleEntrega) {
		this.detalleEntrega = detalleEntrega;
	}

	@Column(name = "nombre_recibe", nullable = true, length = 80)
	public String getNombreRecibe() {
		return nombreRecibe;
	}

	public void setNombreRecibe(String nombreRecibe) {
		this.nombreRecibe = nombreRecibe;
	}

	@Transient
	public String getLblEstado() {
		return lblEstado;
	}

	public void setLblEstado(String lblEstado) {
		this.lblEstado = lblEstado;
	}

	@Transient
	public String getCurrEtaNom() {
		return currEtaNom;
	}

	public void setCurrEtaNom(String currEtaNom) {
		this.currEtaNom = currEtaNom;
	}

	//(Nuevo) TipoReparacion CRE= por venta al credito,   VNT= Por venta   NML= normal
	@Column(name="tipo_rep",length=3,nullable=true)
	public String getTipoRep() {
		return tipoRep;
	}

	public void setTipoRep(String tipoRep) {
		this.tipoRep = tipoRep;
	}

	@Column(name="id_cot",nullable=true)
	public Integer getIdCot() {
		return idCot;
	}

	public void setIdCot(Integer idCot) {
		this.idCot = idCot;
	}

	//ENV= enviada a reparacion, RCP= Reparacion Cliente con pieza o aparato recibida
	@Column(name="estado_rep_externa",nullable=true,length=3)
	public String getEstadoRepExterna() {
		return estadoRepExterna;
	}

	public void setEstadoRepExterna(String estadoRepExterna) {
		this.estadoRepExterna = estadoRepExterna;
	}

	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reparacionCliente", cascade = CascadeType.REMOVE)
	public List<DetalleReparacionExterna> getDetallesReparacionExterna() {
		return detallesReparacionExterna;
	}

	public void setDetallesReparacionExterna(
			List<DetalleReparacionExterna> detallesReparacionExterna) {
		this.detallesReparacionExterna = detallesReparacionExterna;
	}

	
	@Column(name = "adelanto", nullable = true)
	@Min(value = 0)
	public Float getAdelanto() {
		return adelanto;
	}

	public void setAdelanto(Float adelanto) {
		this.adelanto = adelanto;
	}

	
	@Column(name="ingresos_taller",nullable=true)
	public Float getIngresosTaller() {
		return ingresosTaller;
	}

	public void setIngresosTaller(Float ingresosTaller) {
		this.ingresosTaller = ingresosTaller;
	}

	@Column(name="descuentos",nullable=true)
	public Float getDescuentos() {
		return descuentos;
	}

	public void setDescuentos(Float descuentos) {
		this.descuentos = descuentos;
	}

	
	@Column(name="descuentos_garantia",nullable=true)
	public Float getDescuentos_garantia() {
		return descuentos_garantia;
	}

	public void setDescuentos_garantia(Float descuentos_garantia) {
		this.descuentos_garantia = descuentos_garantia;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_recibe_id", nullable = true)
	@ForeignKey(name = "fk_rep_usrec")
	public Usuario getUsuarioRecibe() {
		return usuarioRecibe;
	}

	public void setUsuarioRecibe(Usuario usuarioRecibe) {
		this.usuarioRecibe = usuarioRecibe;
	}

	

	
	
	
	
	
}
