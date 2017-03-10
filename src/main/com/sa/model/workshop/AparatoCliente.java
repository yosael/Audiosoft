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

import com.sa.model.crm.Cliente;

@Entity
@Table(name = "aparato_cliente")
public class AparatoCliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String marca;
	private String modelo;
	private String numSerie;
	private String numLote;
	private Cliente cliente;
	private Date fechaAdquisicion;
	private String ladoAparato;
	private String ladoAparatoBin;
	private Integer periodoGarantia;
	private Date fechaGarRep;
	private Integer periodoGarantiaRep;
	private boolean hechoMedida;
	private boolean customApa;
	private boolean retroAuricular;
	private boolean activo;
	private String userIdRecibe;
	private Float costoVenta;
	private String detalleAparato;
	private String estado;
	private Integer idPrd;
	
	private boolean garantiaVigente = false;
	
	private List<ReparacionCliente> reparacionesApa =  new ArrayList<ReparacionCliente>();
	private List<PiezaAparatoCliente> piezasApa =  new ArrayList<PiezaAparatoCliente>();
	
	//Campos nuevos
	//private boolean garantiaValida;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "apacli_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "fk_cli_apa")
	public Cliente getCliente() {
		return cliente;
	}
	
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_adquisicion", nullable = false)
	public Date getFechaAdquisicion() {
		return fechaAdquisicion;
	}
	
	public void setFechaAdquisicion(Date fechaAdquisicion) {
		this.fechaAdquisicion = fechaAdquisicion;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aparatoRep", cascade = CascadeType.REMOVE)
	public List<ReparacionCliente> getReparacionesApa() {
		return reparacionesApa;
	}

	public void setReparacionesApa(List<ReparacionCliente> reparacionesApa) {
		this.reparacionesApa = reparacionesApa;
	}

	@Column(name = "lado_aparato", nullable = false, length = 3)
	public String getLadoAparato() {
		return ladoAparato;
	}

	public void setLadoAparato(String ladoAparato) {
		this.ladoAparato = ladoAparato;
	}

	@Column(name = "user_id_recibe", nullable = true, length = 20)
	public String getUserIdRecibe() {
		return userIdRecibe;
	}

	public void setUserIdRecibe(String userIdRecibe) {
		this.userIdRecibe = userIdRecibe;
	}

	@Column(name = "hecho_medida", nullable = false)
	public boolean isHechoMedida() {
		return hechoMedida;
	}

	public void setHechoMedida(boolean hechoMedida) {
		this.hechoMedida = hechoMedida;
	}

	@Column(name = "modelo", nullable = true, length = 54)
	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Column(name = "numSerie", nullable = true, length = 80)
	public String getNumSerie() {
		return numSerie;
	}

	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	@Column(name = "marca", nullable = true, length = 20)
	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	@Column(name = "nombre", nullable = true, length = 80)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "retroauricular", nullable = false)
	public boolean isRetroAuricular() {
		return retroAuricular;
	}

	public void setRetroAuricular(boolean retroAuricular) {
		this.retroAuricular = retroAuricular;
	}

	@Column(name = "num_lote", nullable = true, length = 20)
	public String getNumLote() {
		return numLote;
	}

	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}

	@Column(name = "costo_venta", nullable = true)
	public Float getCostoVenta() {
		return costoVenta;
	}

	public void setCostoVenta(Float costoVenta) {
		this.costoVenta = costoVenta;
	}

	@Column(name = "detalle_aparato", nullable = true, length = 500)
	public String getDetalleAparato() {
		return detalleAparato;
	}

	public void setDetalleAparato(String detalleAparato) {
		this.detalleAparato = detalleAparato;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aparatoCliente", cascade = CascadeType.REMOVE)
	public List<PiezaAparatoCliente> getPiezasApa() {
		return piezasApa;
	}

	public void setPiezasApa(List<PiezaAparatoCliente> piezasApa) {
		this.piezasApa = piezasApa;
	}

	//ACT = Activo, COT = Cotizado   PNP=Pendiente de pago
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "id_prd", nullable = true)
	public Integer getIdPrd() {
		return idPrd;
	}

	public void setIdPrd(Integer idPrd) {
		this.idPrd = idPrd;
	}

	@Column(name = "externo", nullable = false)
	public boolean isCustomApa() {
		return customApa;
	}

	public void setCustomApa(boolean customApa) {
		this.customApa = customApa;
	}

	@Transient
	public String getLadoAparatoBin() {
		return ladoAparatoBin;
	}

	public void setLadoAparatoBin(String ladoAparatoBin) {
		this.ladoAparatoBin = ladoAparatoBin;
	}

	@Column(name = "periodo_garantia", nullable = true)
	public Integer getPeriodoGarantia() {
		return periodoGarantia;
	}

	public void setPeriodoGarantia(Integer periodoGarantia) {
		this.periodoGarantia = periodoGarantia;
	}

	@Column(name = "fecha_gar_rep", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getFechaGarRep() {
		return fechaGarRep;
	}

	public void setFechaGarRep(Date fechaGarRep) {
		this.fechaGarRep = fechaGarRep;
	}

	@Column(name = "periodo_gar_rep", nullable = true)
	public Integer getPeriodoGarantiaRep() {
		return periodoGarantiaRep;
	}

	public void setPeriodoGarantiaRep(Integer periodoGarantiaRep) {
		this.periodoGarantiaRep = periodoGarantiaRep;
	}

	@Transient
	public boolean isGarantiaVigente() {
		return garantiaVigente;
	}

	public void setGarantiaVigente(boolean garantiaVigente) {
		this.garantiaVigente = garantiaVigente;
	}

	
	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
	
	
	
	
}
