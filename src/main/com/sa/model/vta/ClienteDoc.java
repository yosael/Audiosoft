package com.sa.model.vta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity   
@Table(name = "cliente_doc") 
public class ClienteDoc implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	private Integer id; 
	private String nombre; 
	private String apellido; 
	private String giro; 
	private String direccion;
	private String dui;
	private String nit;
	private String telefono1;
	private String telefono2;
	private String nrc;
	private Short tipoContribuyente;
	private boolean exento = false;
	private boolean omisionMinimoRet = false;
	private Integer idCliente;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
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
	
	@Column(name = "apellido", nullable = false, length = 80)
	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	@Column(name = "direccion", nullable = true, length = 300)
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "dui", nullable = true, length = 20)
	public String getDui() {
		return dui;
	}

	public void setDui(String dui) {
		this.dui = dui;
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
	public Short getTipoContribuyente() {
		return tipoContribuyente;
	}

	public void setTipoContribuyente(Short tipoContribuyente) {
		this.tipoContribuyente = tipoContribuyente;
	}

	@Column(name = "exento", nullable = true)
	public boolean isExento() {
		return exento;
	}

	public void setExento(boolean exento) {
		this.exento = exento;
	}

	@Column(name = "giro", nullable = true, length = 100)
	public String getGiro() {
		return giro;
	}

	public void setGiro(String giro) {
		this.giro = giro;
	}
	
	@Column(name = "omision_minimo_ret", nullable = true)
	public boolean isOmisionMinimoRet() {
		return omisionMinimoRet;
	}

	public void setOmisionMinimoRet(boolean omisionMinimoRet) {
		this.omisionMinimoRet = omisionMinimoRet;
	}

	@Transient
	public String getPersonaoempresa() {
		if(tipoContribuyente != null) {
			if(tipoContribuyente < 3)
				return nombre + " " + apellido;
			else
				return nombre;
		} else
			return nombre + " " + apellido;
	}

	
	@Column(name= "id_cliente",nullable=true)
	public Integer getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}
	
	
	
}
