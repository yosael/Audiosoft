package com.sa.model.acct;

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

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "asiento_cont_det")
public class AsientoContDet implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private AsientoContable asiento;
	private CuentaContable cuenta;
	private String tipo;
	private Float monto;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cnm_id", nullable = false)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "asiento_id", nullable = false)
	@ForeignKey(name = "fk_asi_det")
	public AsientoContable getAsiento() {
		return asiento;
	}

	public void setAsiento(AsientoContable asiento) {
		this.asiento = asiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cuentac_id", nullable = false)
	@ForeignKey(name = "fk_cc_asdt")
	public CuentaContable getCuenta() {
		return cuenta;
	}

	public void setCuenta(CuentaContable cuenta) {
		this.cuenta = cuenta;
	}

	//CRG = Cargo, ABO = Abono
	@Column(name = "tipo", nullable = false, length = 3)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}