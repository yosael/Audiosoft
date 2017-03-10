package com.sa.model.crm;
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
import javax.persistence.Table;

@Entity
@Table(name = "departamento")
public class Departamento implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private List<Municipio> municipios;
	private List<Cliente> clientes = new ArrayList<Cliente>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "depto_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length = 30)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "departamento", cascade = CascadeType.REMOVE)
	public List<Municipio> getMunicipios() {
		return municipios;
	}
	public void setMunicipios(List<Municipio> municipios) {
		this.municipios = municipios;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "depto", cascade = CascadeType.REMOVE)
	public List<Cliente> getClientes() {
		return clientes;
	}
	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}
}
