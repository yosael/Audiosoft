package com.sa.model.acct;

import java.io.Serializable;
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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "cuenta_contable")
public class CuentaContable implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codCuenta;
	private TipoCuenta tipoCuenta;
	private String nombre;
	private String descripcion;
	private CuentaContable cuentaPadre;
	private List<CuentaContable> cuentasHijas;
	private List<AsientoContDet> asientosCuenta;
	private List<AsientoContDet> detalleCargos;
	private List<AsientoContDet>detalleAbonos;
	private boolean tieneHijos;
	private Double totalMovs;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cta_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "cod_cuenta", nullable = false, length = 20, unique = true)
	public String getCodCuenta() {
		return codCuenta;
	}
	public void setCodCuenta(String codCuenta) {
		this.codCuenta = codCuenta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tpocta_id", nullable = false)
	@ForeignKey(name = "fk_tpc_cuc")
	public TipoCuenta getTipoCuenta() {
		return tipoCuenta;
	}
	public void setTipoCuenta(TipoCuenta tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}
	
	@Column(name = "nombre", nullable = false, length = 30)
	public String getNombre() {
		return nombre;
	}
	
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "descripcion", nullable = true, length = 200)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ctapadre_id", nullable = true)
	@ForeignKey(name = "fk_cta_pdr")
	public CuentaContable getCuentaPadre() {
		return cuentaPadre;
	}
	public void setCuentaPadre(CuentaContable cuentaPadre) {
		this.cuentaPadre = cuentaPadre;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuentaPadre", cascade = CascadeType.REMOVE)
	public List<CuentaContable> getCuentasHijas() {
		return cuentasHijas;
	}
	public void setCuentasHijas(List<CuentaContable> cuentasHijas) {
		this.cuentasHijas = cuentasHijas;
	}
	
	@Transient
	public boolean isTieneHijos() {
		return tieneHijos;
	}
	public void setTieneHijos(boolean tieneHijos) {
		this.tieneHijos = tieneHijos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuenta", cascade = CascadeType.REMOVE)
	public List<AsientoContDet> getAsientosCuenta() {
		return asientosCuenta;
	}
	public void setAsientosCuenta(List<AsientoContDet> asientosCuenta) {
		this.asientosCuenta = asientosCuenta;
	}
	
	@Transient
	public List<AsientoContDet> getDetalleCargos() {
		return detalleCargos;
	}
	public void setDetalleCargos(List<AsientoContDet> detalleCargos) {
		this.detalleCargos = detalleCargos;
	}
	
	@Transient
	public List<AsientoContDet> getDetalleAbonos() {
		return detalleAbonos;
	}
	public void setDetalleAbonos(List<AsientoContDet> detalleAbonos) {
		this.detalleAbonos = detalleAbonos;
	}
	
	@Transient
	public Double getTotalMovs() {
		return totalMovs;
	}
	public void setTotalMovs(Double totalMovs) {
		this.totalMovs = totalMovs;
	}
	
}