package com.sa.model.vta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name = "empresa_doc") 
public class EmpresaDoc implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String direccion;
	private String nit;
	private String telefono1;
	private String telefono2;
	private String nrc;
	private int tipoContribuyente; 
	private boolean exento = false;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "empdoc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "nombre", nullable = false, length = 80)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "direccion", nullable = true, length = 300)
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "nit", nullable = true, length = 18)
	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	@Column(name = "telefono1", nullable = true, length = 12)
	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	@Column(name = "telefono2", nullable = true, length = 12)
	public String getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}

	@Column(name = "nrc", nullable = true, length = 10)
	public String getNrc() {
		return nrc;
	}

	public void setNrc(String nrc) {
		this.nrc = nrc;
	}

	@Column(name = "tipo_contribuyente", nullable = true)
	public int getTipoContribuyente() {
		return tipoContribuyente;
	}

	public void setTipoContribuyente(int tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}

	@Column(name = "exento", nullable = true)
	public boolean isExento() {
		return exento;
	}

	public void setExento(boolean exento) {
		this.exento = exento;
	}
}
