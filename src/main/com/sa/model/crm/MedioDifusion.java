package com.sa.model.crm;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "medio_difusion")
public class MedioDifusion implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	boolean activo = true;
	String nombre;
	boolean modificable = false;
	private Set<Cliente> clientes = new HashSet<Cliente>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "modificable", nullable = false)
	public boolean isModificable() {
		return modificable;
	}
	public void setModificable(boolean modificable) {
		this.modificable = modificable;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mdif", cascade = CascadeType.REMOVE)
	public Set<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(Set<Cliente> clientes) {
		this.clientes = clientes;
	}

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
