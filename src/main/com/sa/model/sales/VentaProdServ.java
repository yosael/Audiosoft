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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.crm.Cliente;
import com.sa.model.inventory.Movimiento;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "venta_prod_serv")
public class VentaProdServ implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fechaVenta;
	private String tipoVenta;
	private Float monto;
	private Float prcTarjeta;
	private Cliente cliente;
	private ClienteCorporativo cliCorp;
	private Sucursal sucursal;
	private String detalle;
	private Usuario usrEfectua;
	private Usuario usrDescuento;
	private String tipoDescuento;
	private Double cantidadDescuento;
	private Integer idDetalle;
	private Empresa empresa;
	private String estado;
	private Movimiento movimiento;
	private List<DetVentaProdServ> detVenta;
	private Double restante;
	private String codTipoVenta;
	private String codTip;
	
	private String tipoDeDocumento;
	private String codComprobante;
	
	private CotizacionComboApa cotizacion;
	
	
	//Nuevo el 31/01.2017
	private TasaTarjetaCred formaPago;
	
	//Nuevo el 09/02/2017
	private String observacion;
	
	//Nuevo el 17/02/2017
	private Float totalDescuentoCorp;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vtaprsv_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "fecha_venta", nullable = false)
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	
	//CMB = Venta de combo de aparato, CST = Consulta, TLL = Trabajo de taller, ITM = Venta de productos por separado
	@Column(name = "tipo_venta", nullable = false, length = 3)
	public String getTipoVenta() {
		return tipoVenta;
	}
	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}
	
	@Column(name = "monto", nullable = false)
	public Float getMonto() {
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
	
	@Column(name = "detalle", nullable = false, length = 300)
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	
	@Column(name = "id_detalle", nullable = false)
	public Integer getIdDetalle() {
		return idDetalle;
	}
	public void setIdDetalle(Integer idDetalle) {
		this.idDetalle = idDetalle;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = true)
	@ForeignKey(name = "fk_clte_vtps")
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = true)
	@ForeignKey(name = "fk_emp_vtps")
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	//APR = Aprobada, ANU = Anulada, PEN = Pendiente, PDS= Pendiente con Descuento ABN= Pendiente con abono ABF=Abono Finalizado
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usrefe_id", nullable = false)
	@ForeignKey(name = "fk_usr_vtps")
	public Usuario getUsrEfectua() {
		return usrEfectua;
	}
	public void setUsrEfectua(Usuario usrEfectua) {
		this.usrEfectua = usrEfectua;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_suc_vtps")
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "venta", cascade = CascadeType.REMOVE)
	public List<DetVentaProdServ> getDetVenta() {
		return detVenta;
	}
	public void setDetVenta(List<DetVentaProdServ> detVenta) {
		this.detVenta = detVenta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usrdsc_id", nullable = true)
	@ForeignKey(name = "fk_urdsc_vtps")
	public Usuario getUsrDescuento() {
		return usrDescuento;
	}
	public void setUsrDescuento(Usuario usrDescuento) {
		this.usrDescuento = usrDescuento;
	}
	
	//P = porcentaje de descuento, M = Monto exacto
	@Column(name = "tipo_descuento", nullable = true, length = 1)
	public String getTipoDescuento() {
		return tipoDescuento;
	}
	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}
	
	@Column(name = "cantidad_descuento", nullable = true)
	public Double getCantidadDescuento() {
		return cantidadDescuento;
	}
	public void setCantidadDescuento(Double cantidadDescuento) {
		this.cantidadDescuento = cantidadDescuento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clicorp_id", nullable = true)
	@ForeignKey(name = "fk_clicr_vtps")
	public ClienteCorporativo getCliCorp() {
		return cliCorp;
	}
	public void setCliCorp(ClienteCorporativo cliCorp) {
		this.cliCorp = cliCorp;
	}
	
	@Column(name = "prc_tarjeta", nullable = true)
	public Float getPrcTarjeta() {
		return prcTarjeta;
	}
	public void setPrcTarjeta(Float prcTarjeta) {
		this.prcTarjeta = prcTarjeta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movimiento_id", nullable = true)
	@ForeignKey(name = "fk_movi_vtps")
	public Movimiento getMovimiento() {
		return movimiento;
	}
	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
	}
	
	@Transient 
	public Double getRestante() {
		if(getTipoDescuento()!=null && getCantidadDescuento()!=null && getMonto()!=0){
			if(getTipoDescuento().equals("P") ){
				restante= getMonto().doubleValue() - getCantidadDescuento()/100*getMonto().doubleValue();
			}else if(getCantidadDescuento()!=null){
				restante= getMonto().doubleValue() - getCantidadDescuento();
			}else{
				restante= getMonto().doubleValue();
			}
		}else {
			restante= getMonto().doubleValue();
		}
		
		if(getTotalDescuentoCorp()!=null && getMonto()!=0)
		{
			restante-=getTotalDescuentoCorp();
		}
		
		return restante;
	}
	
	public void setRestante(Double restante) {
		this.restante = restante;
	}
	
	@Column(name = "cod_tipo_venta", nullable = true,length = 25)
	public String getCodTipoVenta() {
		return codTipoVenta;
	}
	public void setCodTipoVenta(String codTipoVenta) {
		this.codTipoVenta = codTipoVenta;
	}
	
	@Column(name = "cod_tip", nullable = true,length = 25 )
	public String getCodTip() {
		return codTip;
	}
	public void setCodTip(String codTip) {
		this.codTip = codTip;
	}
	
	//F: Factura  C:CCF
	@Column(name = "tipo_de_documento", nullable = true, length = 2)
	public String getTipoDeDocumento() {
		return tipoDeDocumento;
	}
	public void setTipoDeDocumento(String tipoDeDocumento) {
		this.tipoDeDocumento = tipoDeDocumento;
	}
	
	@Column(name = "cod_comprobante", nullable = true, length = 16)
	public String getCodComprobante() {
		return codComprobante;
	}
	public void setCodComprobante(String codComprobante) {
		this.codComprobante = codComprobante;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcotizacion", nullable = true)
	@ForeignKey(name = "fk_coti_vtps")
	public CotizacionComboApa getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(CotizacionComboApa cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forma_pago", nullable = true)
	@ForeignKey(name = "fk_venta_frpago")
	public TasaTarjetaCred getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(TasaTarjetaCred formaPago) {
		this.formaPago = formaPago;
	}
	
	
	@Column(name = "observacion", nullable = true, length = 100)
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	@Column(name="total_descuento_corp",nullable=true)
	public Float getTotalDescuentoCorp() {
		return totalDescuentoCorp;
	}
	public void setTotalDescuentoCorp(Float totalDescuentoCorp) {
		this.totalDescuentoCorp = totalDescuentoCorp;
	}

	
	
	
	
	
}