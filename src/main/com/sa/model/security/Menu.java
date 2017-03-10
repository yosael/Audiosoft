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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
@Table(name = "menu", uniqueConstraints = @UniqueConstraint(columnNames = {"nombre_spanish","nombre_english"}))
public class Menu implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombreSpanish;
	private String nombreEnglish;
	private String url;
	private Integer orden;
	private boolean visible;
	private String estado;
	private Menu menuPadre;
	private Sistema sistema;
	private List<Menu> subMenus = new ArrayList<Menu>();
	private List<RolMenu> roles = new ArrayList<RolMenu>();
	private String urlMenuPapa;
	private boolean asociado;
	private boolean opcAsociada;
	
	public Menu(){ 
		this.estado = "ACT";
		this.visible = true;
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

	@Column(name = "url", nullable = true, length = 100)
	@Length(max = 100)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name = "visible", nullable = false)
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_padre_id", nullable = true)
	@ForeignKey(name = "fk_menu_padre")
	public Menu getMenuPadre() {
		return menuPadre;
	}

	public void setMenuPadre(Menu menuPadre) {
		this.menuPadre = menuPadre;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "menuPadre", cascade = CascadeType.REMOVE)
	@OrderBy("orden")
	public List<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<Menu> subMenus) {
		this.subMenus = subMenus;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sistema_id", nullable = false)
	@ForeignKey(name = "menu_sistema_fk")
	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "menu", cascade = CascadeType.REMOVE)
	public List<RolMenu> getRoles() {
		return roles;
	}

	public void setRoles(List<RolMenu> roles) {
		this.roles = roles;
	}

	@Transient
	public boolean isAsociado() {
		return asociado;
	}

	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}

	@Column(name = "nombre_spanish", nullable = true, length = 50)
	@Length(max = 100)
	@NotEmpty
	public String getNombreSpanish() {
		return nombreSpanish;
	}

	public void setNombreSpanish(String nombreSpanish) {
		this.nombreSpanish = nombreSpanish;
	}

	@Column(name = "nombre_english", nullable = true, length = 50)
	@Length(max = 100)
	@NotEmpty
	public String getNombreEnglish() {
		return nombreEnglish;
	}

	public void setNombreEnglish(String nombreEnglish) {
		this.nombreEnglish = nombreEnglish;
	}

	@Column(name = "orden", nullable = true)
	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	@Transient
	public boolean isOpcAsociada() {
		return opcAsociada;
	}

	public void setOpcAsociada(boolean opcAsociada) {
		this.opcAsociada = opcAsociada;
	}

	@Transient
	public String getUrlMenuPapa() {
		return urlMenuPapa;
	}

	public void setUrlMenuPapa(String urlMenuPapa) {
		this.urlMenuPapa = urlMenuPapa;
	}	
}
