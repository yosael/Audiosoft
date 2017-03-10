package com.sa.model.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "rol_menu")
public class RolMenu implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Rol rol;
	private Menu menu;
	private String url;
	private String codigoRol;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rolmnu_id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id", nullable = false)
	@ForeignKey(name = "fk_mnrl_menu")
	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	@ForeignKey(name = "fk_mnrl_menu")
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Transient
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Transient
	public String getCodigoRol() {
		return codigoRol;
	}

	public void setCodigoRol(String codigoRol) {
		this.codigoRol = codigoRol;
	}
}
