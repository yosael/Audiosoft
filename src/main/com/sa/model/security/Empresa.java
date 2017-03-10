package com.sa.model.security;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Marca;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Proveedor;
import com.sa.model.inventory.UnidadMedida;

@Entity
@Table(name = "empresa", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Empresa implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo; //TODO EN MAYUSCULA
	private String nombre;
	private String descripcion;
	private String giro;
	private String estado; //ACT=Activo; INA=INACTIVO;
	private Set<Sucursal> sucursales = new HashSet<Sucursal>();
	private Set<UnidadMedida> unidadesMedida = new HashSet<UnidadMedida>();
	private Set<Marca> marcas = new HashSet<Marca>();
	private Set<Categoria> categorias = new HashSet<Categoria>();
	private Set<Producto> productos = new HashSet<Producto>();
	private Set<Proveedor> proveedores = new HashSet<Proveedor>();
	
	public Empresa(){
		this.estado = "ACT";
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "codigo", nullable = false, length = 8)
	@Length(max = 8, min = 2)
	@NotEmpty
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nombre", nullable = false, length = 50)
	@Length(max = 50)
	@NotEmpty
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "descripcion", nullable = true, length = 200)
	@Length(max = 200)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "giro", nullable = false, length = 50)
	@Length(max = 50)
	@NotEmpty
	public String getGiro() {
		return giro;
	}

	public void setGiro(String giro) {
		this.giro = giro;
	}

	@Column(name = "estado", nullable = false, length = 3)
	@Length(max = 3)
	@NotEmpty
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(Set<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<UnidadMedida> getUnidadesMedida() {
		return unidadesMedida;
	}

	public void setUnidadesMedida(Set<UnidadMedida> unidadesMedida) {
		this.unidadesMedida = unidadesMedida;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<Marca> getMarcas() {
		return marcas;
	}

	public void setMarcas(Set<Marca> marcas) {
		this.marcas = marcas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<Categoria> categorias) {
		this.categorias = categorias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<Producto> getProductos() {
		return productos;
	}

	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.REMOVE)
	public Set<Proveedor> getProveedores() {
		return proveedores;
	}

	public void setProveedores(Set<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}
}
