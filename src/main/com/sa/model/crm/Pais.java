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

import com.sa.model.inventory.Producto;

@Entity
@Table(name = "pais")
public class Pais implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer numIso;
	private String codIso2;
	private String codIso3;
	private String nombreEsp;
	private String nombreEng;
	private List<Producto> productos =new ArrayList<Producto>();
	private List<Cliente> clientes = new ArrayList<Cliente>();
	
		@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pais_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "num_iso", nullable = false)
	public Integer getNumIso() {
		return numIso;
	}
	public void setNumIso(Integer numIso) {
		this.numIso = numIso;
	}
	
	@Column(name = "cod_iso_2", nullable = false, length = 2)
	public String getCodIso2() {
		return codIso2;
	}
	public void setCodIso2(String codIso2) {
		this.codIso2 = codIso2;
	}
	
	@Column(name = "cod_iso_3", nullable = false, length = 3)
	public String getCodIso3() {
		return codIso3;
	}
	public void setCodIso3(String codIso3) {
		this.codIso3 = codIso3;
	}
	
	@Column(name = "nombre_esp", nullable = false, length = 50)
	public String getNombreEsp() {
		return nombreEsp;
	}
	public void setNombreEsp(String nombreEsp) {
		this.nombreEsp = nombreEsp;
	}
	
	@Column(name = "nombre_eng", nullable = true, length = 50)
	public String getNombreEng() {
		return nombreEng;
	}
	public void setNombreEng(String nombreEng) {
		this.nombreEng = nombreEng;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "origen", cascade = CascadeType.REMOVE)
	public List<Producto> getProductos() {
		return productos;
	}
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pais", cascade = CascadeType.REMOVE)
	public List<Cliente> getClientes() {
		return clientes;
	}
	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}
	
	
}