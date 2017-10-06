package com.sa.model.crm;

import java.io.Serializable;
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

import com.sa.model.security.Usuario;
import com.sa.model.vta.ComprobanteImpresion;

@Entity
@Table(name = "cheque_doc")
public class ChequeDoc implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String ordenDe;
	private Float monto;
	private String montoLetras;
	private Date fecha;
	private ProveedorDoc proveedor;
	private String descripcion;
	private String estado;
	private String lugar;
	private ComprobanteImpresion comprobante;
	
	//agregado el 05/10/2017
	private String numCheque;
	private String concepto;
	private Usuario autorizadoPor;
	private String recibe;
	private Usuario elaboradoPor;
	
	private String tipoComprobante;
	
	private String banco;
	private String cuenta;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cliente_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "orden_de", nullable = false, length = 100)
	public String getOrdenDe() {
		return ordenDe;
	}

	public void setOrdenDe(String ordenDe) {
		this.ordenDe = ordenDe;
	}

	@Column(name = "monto", nullable = false)
	public Float getMonto() {
		return monto;
	}

	public void setMonto(Float monto) {
		this.monto = monto;
	}

	@Column(name = "monto_letras", nullable = false, length = 120)
	public String getMontoLetras() {
		return montoLetras;
	}

	public void setMontoLetras(String montoLetras) {
		this.montoLetras = montoLetras;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = false)
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prvdoc_id", nullable = true)
	@ForeignKey(name = "fk_chequedoc_provdoc")
	public ProveedorDoc getProveedor() {
		return proveedor;
	}

	public void setProveedor(ProveedorDoc proveedor) {
		this.proveedor = proveedor;
	}

	@Column(name = "descripcion", nullable = true, length = 200)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comprobante_impresion_id", nullable = true)
	@ForeignKey(name = "fk_chqdoc_comp")
	public ComprobanteImpresion getComprobante() {
		return comprobante;
	}

	public void setComprobante(ComprobanteImpresion comprobante) {
		this.comprobante = comprobante;
	}

	// APL = Aplicado, ANU = Anulado
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "lugar", nullable = false, length = 30)
	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	
	@Column(name="num_cheque",length=15,nullable=true)
	public String getNumCheque() {
		return numCheque;
	}

	public void setNumCheque(String numCheque) {
		this.numCheque = numCheque;
	}

	@Column(name="concepto",length=50,nullable=true)
	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autorizado_por", nullable = true)
	@ForeignKey(name = "fk_cheque_autori")
	public Usuario getAutorizadoPor() {
		return autorizadoPor;
	}

	public void setAutorizadoPor(Usuario autorizadoPor) {
		this.autorizadoPor = autorizadoPor;
	}

	@Column(name="recibe",length=60,nullable=true)
	public String getRecibe() {
		return recibe;
	}

	public void setRecibe(String recibe) {
		this.recibe = recibe;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "elaborado_por", nullable = true)
	@ForeignKey(name = "fk_cheque_elab")
	public Usuario getElaboradoPor() {
		return elaboradoPor;
	}

	public void setElaboradoPor(Usuario elaboradoPor) {
		this.elaboradoPor = elaboradoPor;
	}

	@Column(name="tipo_comprobante",nullable=true,length=30)
	public String getTipoComprobante() {
		return tipoComprobante;
	}

	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}

	@Column(name="banco",nullable=true,length=30)
	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	@Column(name="cuenta",nullable=true,length=20)
	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	
	
	
	
}
