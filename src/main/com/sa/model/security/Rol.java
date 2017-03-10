package com.sa.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
@Table(name = "rol", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Rol implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private String nombre;
	private String descripcion;
	private Sistema sistema;
	private List<Usuario> usuarios = new ArrayList<Usuario>();
	private List<RolMenu> menus = new ArrayList<RolMenu>();
	private boolean asociado;
	
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sistema_id", nullable = false)
	@ForeignKey(name = "fk_rol_sistema")
	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles", cascade = CascadeType.REMOVE)
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rol", cascade = CascadeType.REMOVE)
	public List<RolMenu> getMenus() {
		return menus;
	}

	public void setMenus(List<RolMenu> menus) {
		this.menus = menus;
	}
	
	@Transient
	public boolean isAsociado() {
		return asociado;
	}

	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}

}
