package com.sa.model.inventory;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.crm.Pais;
import com.sa.model.security.Empresa;

@Entity
@Table(name = "proveedor")
public class Proveedor implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nit;
	private String razonSocial;
	private String nombreContacto;
	private String telefonos;
	private String dirTelefono;
	private String fax;
	
	private Pais pais;
	private String direccion;
	private String email;
	private String descripcion;
	private Empresa empresa;
	private Set<Producto> productos = new HashSet<Producto>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nit", nullable = true, length = 30)
	public String getNit() {
		return nit;
	}
	
	public void setNit(String nit) {
		this.nit = nit;
	}
	
	@Column(name = "razon_social", nullable = false, length = 80)
	public String getRazonSocial() {
		return razonSocial;
	}
	
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
	@Column(name = "nombre_contacto", nullable = true, length = 100)
	public String getNombreContacto() {
		return nombreContacto;
	}
	
	public void setNombreContacto(String nombreContacto) {
		this.nombreContacto = nombreContacto;
	}
	
	@Column(name = "telefonos", nullable = true, length = 30)
	public String getTelefonos() {
		return telefonos;
	}
	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}
	
	@Column(name = "direccion", nullable = true, length = 200)
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	@Column(name = "email", nullable = true, length = 60)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "descripcion", nullable = true, length = 300)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_proveedor_empresa")
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proveedor", cascade = CascadeType.REMOVE)
	public Set<Producto> getProductos() {
		return productos;
	}

	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}

	@Column(name = "dir_telefono", nullable = true, length = 30)
	public String getDirTelefono() { 
		return dirTelefono;
	}

	public void setDirTelefono(String dirTelefono) {
		this.dirTelefono = dirTelefono;
	}

	@Column(name = "fax", nullable = true, length = 30)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pais_id", nullable = false)
	@ForeignKey(name = "fk_prv_pai")
	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	
	
	
	
}
