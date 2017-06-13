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
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.security.Empresa;

@Entity
@Table(name = "categoria")
public class Categoria implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private String nombre;
	private String descripcion;
	private Categoria categoriaPadre;
	private boolean tieneNumSerie;
	private boolean tieneNumLote;
	private boolean deTaller;
	private Set<Categoria> subCategorias = new HashSet<Categoria>();
	private Set<Producto> productos = new HashSet<Producto>();
	private Empresa empresa;
	private Boolean reqMolde;
	private Boolean reqEnsamble;
	
	private Boolean activo;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "codigo", nullable = false, length = 11)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "nombre", nullable = false, length = 30)
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_padre_id", nullable = true)
	@ForeignKey(name = "fk_categoria_padre")
	public Categoria getCategoriaPadre() {
		return categoriaPadre;
	}
	
	public void setCategoriaPadre(Categoria categoriaPadre) {
		this.categoriaPadre = categoriaPadre;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "categoriaPadre", cascade = CascadeType.REMOVE)
	public Set<Categoria> getSubCategorias() {
		return subCategorias;
	}
	
	public void setSubCategorias(Set<Categoria> subCategorias) {
		this.subCategorias = subCategorias;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria", cascade = CascadeType.REMOVE)
	public Set<Producto> getProductos() {
		return productos;
	}
	
	public void setProductos(Set<Producto> productos) {
		this.productos = productos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_categoria_empresa")
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Column(name = "tiene_num_serie", nullable = false)
	public boolean isTieneNumSerie() {
		return tieneNumSerie;
	}

	public void setTieneNumSerie(boolean tieneNumSerie) {
		this.tieneNumSerie = tieneNumSerie;
	}

	@Column(name = "tiene_num_lote", nullable = false)
	public boolean isTieneNumLote() {
		return tieneNumLote;
	}

	public void setTieneNumLote(boolean tieneNumLote) {
		this.tieneNumLote = tieneNumLote;
	}

	@Column(name = "de_taller", nullable = false)
	public boolean isDeTaller() {
		return deTaller;
	}

	public void setDeTaller(boolean deTaller) {
		this.deTaller = deTaller;
	}
	
	
	@Column(name= "req_molde",nullable=true)
	public Boolean getReqMolde() {
		return reqMolde;
	}

	public void setReqMolde(Boolean reqMolde) {
		this.reqMolde = reqMolde;
	}
	
	
	@Column(name= "req_ensamble",nullable=true)
	public Boolean getReqEnsamble() {
		return reqEnsamble;
	}

	public void setReqEnsamble(Boolean reqEnsamble) {
		this.reqEnsamble = reqEnsamble;
	}

	
	@Column(name= "activo", nullable = true)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	

	
	
	
	
	
}
