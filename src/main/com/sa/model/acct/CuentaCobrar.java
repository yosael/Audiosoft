package com.sa.model.acct;

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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.crm.Cliente;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.security.Sucursal;

@Entity
@Table(name = "cuenta_cobrar")
public class CuentaCobrar implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String comprobante;
	private ConceptoMov concepto;
	private String comentario;
	private Float monto;
	private Float remanente;
	private String estado;
	private Date fechaIngreso;
	private Date fechaVencimiento;
	private Date fechaFinalizacion;
	private Integer diasPlazo;
	private Cliente cliente;
	private ClienteCorporativo cliCorp;
	private Sucursal sucursal;
	private CondicionPago condicionPago;
	private AsientoContable asiento;
	private boolean cxcSel;
	private List<PagoCuentaPend> pagosCxc = new ArrayList<PagoCuentaPend>();
	private CotizacionComboApa cotizacion;
	
	//nuevo
	private Integer id_venta;
	//private String numeroInfo;
	
	//Nuevo 14/12/2016
	
	private String numFactura;
	private String numQuedan;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cxc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "comprobante", nullable = false, length = 20)
	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concepto_id", nullable = true)
	@ForeignKey(name = "fk_cnp_cxc")
	public ConceptoMov getConcepto() {
		return concepto;
	}

	public void setConcepto(ConceptoMov concepto) {
		this.concepto = concepto;
	}

	@Column(name = "monto", nullable = false)
	public Float getMonto() {
		return monto;
	}

	public void setMonto(Float monto) {
		this.monto = monto;
	}
	
	@Column(name = "fecha_ingreso", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	@Transient
	public String getFechaIngresoFlt() {
		return new java.text.SimpleDateFormat("dd/MM/yyyy").format(getFechaIngreso());
	}

	@Column(name = "dias_plazo", nullable = false)
	@Min(1)
	public Integer getDiasPlazo() {
		return diasPlazo;
	}

	public void setDiasPlazo(Integer diasPlazo) {
		this.diasPlazo = diasPlazo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "fk_cli_cxc")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "condpgo_id", nullable = false)
	@ForeignKey(name = "fk_cnp_cxc")
	public CondicionPago getCondicionPago() {
		return condicionPago;
	}

	public void setCondicionPago(CondicionPago condicionPago) {
		this.condicionPago = condicionPago;
	}

	@Column(name = "comentario", nullable = true, length = 300)
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Column(name = "fecha_vencimiento", nullable = false)
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "succxc_id", nullable = false)
	@ForeignKey(name = "fk_suc_cxc")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "asiento_id", nullable = true)
	@ForeignKey(name = "fk_asi_cxc")
	public AsientoContable getAsiento() {
		return asiento;
	}

	public void setAsiento(AsientoContable asiento) {
		this.asiento = asiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clicorp_id", nullable = true)
	@ForeignKey(name = "fk_clic_cxc")
	public ClienteCorporativo getCliCorp() {
		return cliCorp;
	}

	public void setCliCorp(ClienteCorporativo cliCorp) {
		this.cliCorp = cliCorp;
	}

	@Transient
	public boolean isCxcSel() {
		return cxcSel;
	}

	public void setCxcSel(boolean cxcSel) {
		this.cxcSel = cxcSel;
	}

	@Column(name = "remanente", nullable = true)
	public Float getRemanente() {
		return remanente;
	}

	public void setRemanente(Float remanente) {
		this.remanente = remanente;
	}

	//ACT = Activa, PGD = Pagada, CAN = Cancelada
	@Column(name = "estado", nullable = false)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuentaCobrar", cascade = CascadeType.REMOVE)
	@OrderBy("fechaIngreso DESC")
	public List<PagoCuentaPend> getPagosCxc() {
		return pagosCxc;
	}

	public void setPagosCxc(List<PagoCuentaPend> pagosCxc) {
		this.pagosCxc = pagosCxc;
	}

	@Column(name = "fecha_finalizacion", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}

	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "cuentaCobrar", cascade = CascadeType.REMOVE)
	public CotizacionComboApa getCotizacion() {
		return cotizacion;
	}

	public void setCotizacion(CotizacionComboApa cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	
	@Column(name="id_venta",nullable=true)
	public Integer getId_venta() {
		return id_venta;
	}

	public void setId_venta(Integer id_venta) {
		this.id_venta = id_venta;
	}

	
	@Column(name="num_factura",nullable=true,length=35)
	public String getNumFactura() {
		return numFactura;
	}

	public void setNumFactura(String numFactura) {
		this.numFactura = numFactura;
	}

	
	@Column(name="num_quedan",nullable=true,length=35)
	public String getNumQuedan() {
		return numQuedan;
	}

	public void setNumQuedan(String numQuedan) {
		this.numQuedan = numQuedan;
	}

	
	/*@Column(name="numeroInfo",nullable=true)
	public String getNumeroInfo() {
		return numeroInfo;
	}

	public void setNumeroInfo(String numeroInfo) {
		this.numeroInfo = numeroInfo;
	}*/
	
	
	
	
	
	
	
}