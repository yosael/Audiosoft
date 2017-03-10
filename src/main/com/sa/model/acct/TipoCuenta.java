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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tipo_cuenta")
public class TipoCuenta implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String codigo;
	private List<CuentaContable> cuentasHijas;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cdp_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "nombre", nullable = false, length = 20)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	//AC = Activo, PV = Pasivo, CC = Capital contable
	@Column(name = "codigo", nullable = false, length = 2)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoCuenta", cascade = CascadeType.REMOVE)
	public List<CuentaContable> getCuentasHijas() {
		return cuentasHijas;
	}

	public void setCuentasHijas(List<CuentaContable> cuentasHijas) {
		this.cuentasHijas = cuentasHijas;
	}
	
	
}