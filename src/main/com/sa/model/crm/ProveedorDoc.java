package com.sa.model.crm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "proveedor_doc")
public class ProveedorDoc implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String razonSocial;
	private String dui;
	private String nit;
	private String nomContacto;
	private String telContacto;
	private String telefono;
	private String direccion;
	private String email;
	private String nrc;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cliente_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "razon_social", nullable = false, length = 50)
	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	@Column(name = "dui", nullable = true, length = 12)
	public String getDui() {
		return dui;
	}

	public void setDui(String dui) {
		this.dui = dui;
	}

	@Column(name = "nit", nullable = false, length = 20)
	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	@Column(name = "nom_contacto", nullable = false, length = 70)
	public String getNomContacto() {
		return nomContacto;
	}

	public void setNomContacto(String nomContacto) {
		this.nomContacto = nomContacto;
	}

	@Column(name = "tel_contacto", nullable = false, length = 12)
	public String getTelContacto() {
		return telContacto;
	}

	public void setTelContacto(String telContacto) {
		this.telContacto = telContacto;
	}

	@Column(name = "telefono", nullable = false, length = 12)
	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Column(name = "direccion", nullable = true, length = 300)
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "email", nullable = true, length = 50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
	
	@Column(name = "nrc", nullable = false, length = 12)
	public String getNrc() {
		return nrc;
	}

	public void setNrc(String nrc) {
		this.nrc = nrc;
	}
}
