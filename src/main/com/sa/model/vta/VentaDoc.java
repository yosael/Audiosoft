package com.sa.model.vta;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity 
@Table(name = "venta_doc")  
public class VentaDoc implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Long correlativo;
	private String serie;  
	private Date fecha;
	private Sucursal sucursal;
	private Float total = new Float(0);
	private Float iva = new Float(0);
	private Float percibido = new Float(0);
	private Float retenido = new Float(0);
	private String formaPago;	
	private String estado;
	private Float descuento = new Float(0);
	private ComprobanteImpresion comprobante;
	private ClienteDoc cliente;
	private List<DetVentaDoc> detVentas = new ArrayList<DetVentaDoc>();
	private Usuario usuario;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vtadoc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "correlativo", nullable = false)
	public Long getCorrelativo() {
		return correlativo;
	}

	public void setCorrelativo(Long correlativo) {
		this.correlativo = correlativo;
	}
	
	@Column(name = "serie", nullable = true, length = 10)
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = false)
	@NotNull
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "total", nullable = false)
	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	@Column(name = "iva", nullable = false)
	public Float getIva() {
		return iva;
	}

	public void setIva(Float iva) {
		this.iva = iva;
	}

	@Column(name = "percibido", nullable = false)
	public Float getPercibido() {
		return percibido;
	}

	public void setPercibido(Float percibido) {
		this.percibido = percibido;
	}

	@Column(name = "retenido", nullable = false)
	public Float getRetenido() {
		return retenido;
	}

	public void setRetenido(Float retenido) {
		this.retenido = retenido;
	}

	//EFE = Efectivo, TRJ = Tarjeta, CHQ = Cheque, CRD = Credito
	@Column(name = "forma_pago", nullable = false, length = 3)
	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	@Column(name = "descuento", nullable = false)
	public Float getDescuento() {
		return descuento;
	}

	public void setDescuento(Float descuento) {
		this.descuento = descuento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comprobante_impresion_id", nullable = true)
	@ForeignKey(name = "fk_ventadoc_comp")
	public ComprobanteImpresion getComprobante() {
		return comprobante;
	}

	public void setComprobante(ComprobanteImpresion comprobante) {
		this.comprobante = comprobante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = true)
	@ForeignKey(name = "fk_ventadoc_clientedoc")
	public ClienteDoc getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDoc cliente) {
		this.cliente = cliente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "venta", cascade = CascadeType.REMOVE)
	public List<DetVentaDoc> getDetVentas() {
		return detVentas;
	}

	public void setDetVentas(List<DetVentaDoc> detVentas) {
		this.detVentas = detVentas;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = true)
	@ForeignKey(name = "fk_suc_asigcpr")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	//APL = Aplicado, ANU = Anulado
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = true)
	@ForeignKey(name = "fk_ventadoc_user")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	
	
	
	
}
