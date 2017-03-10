package com.sa.model.security;

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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "config_impresora")
public class ConfigImpresora implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Sucursal sucursal;
	private String nombrePrinter;
	private String tipoImpresora = "T";
	private Short margenSup;
	private Short margenInf;
	private Short margenIzq;
	private Short margenDer;
	private String headerIzq;
	private String headerCtr;
	private String headerDer;
	private String footerIzq;
	private String footerCtr;
	private String footerDer;
	private boolean impresionSilenciosa = true;
	private boolean ajustarImpresion = false;
	private Short altoPapel;
	private Short anchoPapel;
	private String estado;	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cfgprt_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_cfgprt_suc")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@Column(name = "nombre_printer", nullable = false, length = 50)
	public String getNombrePrinter() {
		return nombrePrinter;
	}

	public void setNombrePrinter(String nombrePrinter) {
		this.nombrePrinter = nombrePrinter;
	}

	@Column(name = "margen_sup", nullable = true, scale = 4)
	public Short getMargenSup() {
		if(margenSup == null)
			return 0;
		return margenSup;
	}

	public void setMargenSup(Short margenSup) {
		this.margenSup = margenSup;
	}

	@Column(name = "margen_inf", nullable = true, scale = 4)
	public Short getMargenInf() {
		if(margenInf == null)
			return 0;
		return margenInf;
	}

	public void setMargenInf(Short margenInf) {
		this.margenInf = margenInf;
	}

	@Column(name = "margen_izq", nullable = true, scale = 4)
	public Short getMargenIzq() {
		if(margenIzq == null)
			return 0;
		return margenIzq;
	}

	public void setMargenIzq(Short margenIzq) {
		this.margenIzq = margenIzq;
	}

	@Column(name = "margen_der", nullable = true, scale = 4)
	public Short getMargenDer() {
		if(margenDer == null)
			return 0;
		return margenDer;
	}

	public void setMargenDer(Short margenDer) {
		this.margenDer = margenDer;
	}

	@Column(name = "header_izq", nullable = true, length = 30)
	public String getHeaderIzq() {
		if(headerIzq == null)
			return "";
		return headerIzq;
	}

	public void setHeaderIzq(String headerIzq) {
		this.headerIzq = headerIzq;
	}

	@Column(name = "header_ctr", nullable = true, length = 30)
	public String getHeaderCtr() {
		if(headerCtr == null)
			return "";
		return headerCtr;
	}

	public void setHeaderCtr(String headerCtr) {
		this.headerCtr = headerCtr;
	}

	@Column(name = "header_der", nullable = true, length = 30)
	public String getHeaderDer() {
		if(headerDer == null)
			return "";
		return headerDer;
	}

	public void setHeaderDer(String headerDer) {
		this.headerDer = headerDer;
	}

	@Column(name = "footer_izq", nullable = true, length = 30)
	public String getFooterIzq() {
		if(footerIzq == null)
			return "";
		return footerIzq;
	}

	public void setFooterIzq(String footerIzq) {
		this.footerIzq = footerIzq;
	}

	@Column(name = "footer_ctr", nullable = true, length = 30)
	public String getFooterCtr() {
		if(footerCtr == null)
			return "";
		return footerCtr;
	}

	public void setFooterCtr(String footerCtr) {
		this.footerCtr = footerCtr;
	}

	@Column(name = "footer_der", nullable = true, length = 30)
	public String getFooterDer() {
		if(footerDer == null)
			return "";
		return footerDer;
	}

	public void setFooterDer(String footerDer) {
		this.footerDer = footerDer;
	}

	@Column(name = "impresion_silenciosa", nullable = false)
	public boolean isImpresionSilenciosa() {
		return impresionSilenciosa;
	}

	public void setImpresionSilenciosa(boolean impresionSilenciosa) {
		this.impresionSilenciosa = impresionSilenciosa;
	}

	@Column(name = "ajustar_impresion", nullable = false)
	public boolean isAjustarImpresion() {
		return ajustarImpresion;
	}

	public void setAjustarImpresion(boolean ajustarImpresion) {
		this.ajustarImpresion = ajustarImpresion;
	}
	
	@Transient
	public byte getAjustarImpresionPrt() {
		if(isAjustarImpresion())
			return (byte)1;
		else
			return (byte)0;
	}

	@Column(name = "alto_papel", nullable = true, scale = 5)
	public Short getAltoPapel() {
		if(altoPapel == null)
			return 0;
		return altoPapel;
	}

	public void setAltoPapel(Short altoPapel) {
		this.altoPapel = altoPapel;
	}

	@Column(name = "ancho_papel", nullable = true, scale = 5)
	public Short getAnchoPapel() {
		if(anchoPapel == null)
			return 0;
		return anchoPapel;
	}

	public void setAnchoPapel(Short anchoPapel) {
		this.anchoPapel = anchoPapel;
	}

	//ACT = Activo, INA = Inactivo
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	//
	@Column(name = "tipo_impresora", nullable = false, length = 1)
	public String getTipoImpresora() {
		return tipoImpresora;
	}

	public void setTipoImpresora(String tipoImpresora) {
		this.tipoImpresora = tipoImpresora;
	}
	
	
}
