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

@Entity
@Table(name = "sistema", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Sistema implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo; //TODO EN MAYUSCULA
	private String nombre;
	private String descripcion;
	private String estado; //ACT=Activo; INA=INACTIVO;
	private Set<Menu> menus = new HashSet<Menu>();
	private Set<Rol> roles = new HashSet<Rol>();
	
	public Sistema(){
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
	@Length(max = 8)
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
	
	@Column(name = "estado", nullable = false, length = 3)
	@Length(max = 3)
	@NotEmpty
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sistema", cascade = CascadeType.REMOVE)
	public Set<Menu> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sistema", cascade = CascadeType.REMOVE)
	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}
	
}
