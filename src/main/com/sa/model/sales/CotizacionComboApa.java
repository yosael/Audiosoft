package com.sa.model.sales;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.ForeignKey;
import com.sa.model.acct.CuentaCobrar;
import com.sa.model.crm.Cliente;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "cotizacion_cmb_apa")
public class CotizacionComboApa implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Cliente cliente;
	private String estado;
	private ComboAparato combo; //Combo seleccionado de entre la lista de combos cotizados 
	private boolean retroAuricular;
	private String detalleAparato;
	private String ladoAparato;
	private VentaProdServ idVta;
	private Sucursal sucursal;
	private Date fechaIngreso;
	private Usuario usuarioGenera;
	private Integer periodoGarantia;
	private Integer validez;
	private Date fechaAprobacion;
	private CotizacionComboApa cotizacionComboBin;
	private List<CotizacionComboApa> hijoBin =  new ArrayList<CotizacionComboApa>();
	private List<CotizacionComboItem> itemsCotizacion =  new ArrayList<CotizacionComboItem>();
	private List<CotizacionCombos> cmbCotizados = new ArrayList<CotizacionCombos>();
	private List<CotizacionPrdSvcAdicionales> cotizItmSvcAdi = new ArrayList<CotizacionPrdSvcAdicionales>();
	private CuentaCobrar cuentaCobrar;
	private Integer selComboId;
	private Date fechaVenta;
	private Date fechaCredito;
	
	private VentaProdServ venta= new VentaProdServ();
	
	//Nuevo agregado el 06/07/2017
	private TasaTarjetaCred formaPago;
	private boolean incluyeIva; 
	
	//nuevo el 26/07/2017
	private String ignorar;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctcmap_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Transient
	public ComboAparato getCombo() {
		return combo;
	}
	public void setCombo(ComboAparato combo) {
		this.combo = combo;
	}
	
	@Column(name = "retroauricular", nullable = false)
	public boolean isRetroAuricular() {
		return retroAuricular;
	}

	public void setRetroAuricular(boolean retroAuricular) {
		this.retroAuricular = retroAuricular;
	}
	
	@Column(name = "lado_aparato", nullable = false, length = 3)
	public String getLadoAparato() {
		return ladoAparato;
	}

	public void setLadoAparato(String ladoAparato) {
		this.ladoAparato = ladoAparato;
	}

	@Column(name = "detalle_aparato", nullable = true, length = 500)
	public String getDetalleAparato() {
		return detalleAparato;
	}

	public void setDetalleAparato(String detalleAparato) {
		this.detalleAparato = detalleAparato;
	}
	
	//PEN = Cotizacion pendiente, APL = Cotizacion vendida, CAN = Cotizacion cancelada
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "fk_cli_ctcmap")
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venta_id", nullable = true)
	@ForeignKey(name = "fk_vta_ctcmap")
	public VentaProdServ getIdVta() {
		return idVta;
	}
	public void setIdVta(VentaProdServ idVta) {
		this.idVta = idVta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = true)
	@ForeignKey(name = "fk_suc_ctcmap")
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cotizacion", cascade = CascadeType.REMOVE)
	public List<CotizacionComboItem> getItemsCotizacion() {
		return itemsCotizacion;
	}
	public void setItemsCotizacion(List<CotizacionComboItem> itemsCotizacion) {
		this.itemsCotizacion = itemsCotizacion;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ingreso", nullable = false)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_aprobacion", nullable = true)
	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}
	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cotizbin_id", nullable = true)
	@ForeignKey(name = "fk_cotb_ctcmap")
	public CotizacionComboApa getCotizacionComboBin() {
		return cotizacionComboBin;
	}
	public void setCotizacionComboBin(CotizacionComboApa cotizacionComboBin) {
		this.cotizacionComboBin = cotizacionComboBin;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cotizacionComboBin", cascade = CascadeType.REMOVE)
	public List<CotizacionComboApa> getHijoBin() {
		return hijoBin;
	}
	public void setHijoBin(List<CotizacionComboApa> hijoBin) {
		this.hijoBin = hijoBin;
	}
	
	@Column(name = "periodo_garantia", nullable = true)
	public Integer getPeriodoGarantia() {
		return periodoGarantia;
	}
	public void setPeriodoGarantia(Integer periodoGarantia) {
		this.periodoGarantia = periodoGarantia;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	@ForeignKey(name = "fk_cotb_usrg")
	public Usuario getUsuarioGenera() {
		return usuarioGenera;
	}
	public void setUsuarioGenera(Usuario usuarioGenera) {
		this.usuarioGenera = usuarioGenera;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cotizacion", cascade = CascadeType.REMOVE)
	public List<CotizacionCombos> getCmbCotizados() {
		return cmbCotizados;
	}
	public void setCmbCotizados(List<CotizacionCombos> cmbCotizados) {
		this.cmbCotizados = cmbCotizados;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cxc_id", nullable = true)
	@ForeignKey(name = "cotizacionCmbApa_CuentaCorbrar_fk")
	public CuentaCobrar getCuentaCobrar() {
		return cuentaCobrar;
	}
	public void setCuentaCobrar(CuentaCobrar cuentaCobrar) {
		this.cuentaCobrar = cuentaCobrar;
	}
	
	@Column(name = "selcombo_id", nullable = true)
	public Integer getSelComboId() {
		return selComboId;
	}
	public void setSelComboId(Integer selComboId) {
		this.selComboId = selComboId;
	}
	
	@Column(name ="validez",nullable = true)
	public Integer getValidez() {
		return validez;
	}
	public void setValidez(Integer validez) {
		this.validez = validez;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cotizacion", cascade = CascadeType.REMOVE)
	public List<CotizacionPrdSvcAdicionales> getCotizItmSvcAdi() {
		return cotizItmSvcAdi;
	}
	public void setCotizItmSvcAdi(List<CotizacionPrdSvcAdicionales> cotizItmSvcAdi) {
		this.cotizItmSvcAdi = cotizItmSvcAdi;
	}
	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_venta", nullable = true)
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_credito", nullable = true)
	public Date getFechaCredito() {
		return fechaCredito;
	}
	public void setFechaCredito(Date fechaCredito) {
		this.fechaCredito = fechaCredito;
	}
	
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "cotizacion", cascade = CascadeType.REMOVE)
	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forma_pago", nullable = true)
	@ForeignKey(name = "fk_cot_forma_pago")
	public TasaTarjetaCred getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(TasaTarjetaCred formaPago) {
		this.formaPago = formaPago;
	}
	
	
	@Column(name = "incluye_iva", nullable = false)
	public boolean isIncluyeIva() {
		return incluyeIva;
	}
	public void setIncluyeIva(boolean incluyeIva) {
		this.incluyeIva = incluyeIva;
	}
	
	@Column(name="ignorar",length=2,nullable=true)
	public String getIgnorar() {
		return ignorar;
	}
	public void setIgnorar(String ignorar) {
		this.ignorar = ignorar;
	}
	
	
	
	
	
}
