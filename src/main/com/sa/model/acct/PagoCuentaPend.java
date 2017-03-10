package com.sa.model.acct;

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

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Sucursal;

@Entity
@Table(name = "pago_cuenta_pend")
public class PagoCuentaPend implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private CuentaCobrar cuentaCobrar;
	//private CuentaPagar cuentaPagar;
	private String comentario;
	private Float monto;
	private Float remanente;
	private Date fechaIngreso;
	private Sucursal sucursal;
	private CondicionPago condicionPago;
	private AsientoContable asiento;
	private String estado;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pcpe_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "monto", nullable = false)
	public Float getMonto() {
		return monto;
	}

	public void setMonto(Float monto) {
		this.monto = monto;
	}

	@Column(name = "fecha_ingreso", nullable = false)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "condpgo_id", nullable = true)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "succxc_id", nullable = true)
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

	@Column(name = "remanente", nullable = false)
	public Float getRemanente() {
		return remanente;
	}

	public void setRemanente(Float remanente) {
		this.remanente = remanente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cxc_id", nullable = true)
	@ForeignKey(name = "fk_pcpe_cxc")
	public CuentaCobrar getCuentaCobrar() {
		return cuentaCobrar;
	}

	public void setCuentaCobrar(CuentaCobrar cuentaCobrar) {
		this.cuentaCobrar = cuentaCobrar;
	}
	
	@Column(name="estado",nullable=true,length=15)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	
	
	
	

}