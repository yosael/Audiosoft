package com.sa.model.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.sa.model.acct.CuentaCobrar;

@Entity
@Table(name = "cliente_corporativo", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class ClienteCorporativo implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String descripcion;
	private String contacto;
	private String telefono1;
	private String telefono2;
	
	private List<CuentaCobrar> cuentasPagar = new ArrayList<CuentaCobrar>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length=40)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "descripcion", nullable = true, length=200)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "contacto", nullable = false, length=60)
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	
	@Column(name = "telefono1", nullable = false, length=20)
	public String getTelefono1() {
		return telefono1;
	}
	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}
	
	@Column(name = "telefono2", nullable = true, length=20)
	public String getTelefono2() {
		return telefono2;
	}
	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliCorp", cascade = CascadeType.REMOVE)
	@OrderBy("fechaIngreso DESC")
	public List<CuentaCobrar> getCuentasPagar() {
		return cuentasPagar;
	}
	public void setCuentasPagar(List<CuentaCobrar> cuentasPagar) {
		this.cuentasPagar = cuentasPagar;
	}

	
}
