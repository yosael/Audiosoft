package com.sa.model.sales;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.ManyToAny;

import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Producto;

@Entity
@Table(name="adaptacion_combo")
public class AdaptacionCombo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String nombre;
	private String estado; // ACT=Activo, INA=Inactivo, ELI=Eliminado
	
	private List<ItemAdaptacion> itemsAdaptacion = new ArrayList<ItemAdaptacion>();
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_adaptacion",nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre",length=30,nullable=false)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name="estado",length=3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@OneToMany(fetch =FetchType.LAZY,mappedBy = "adaptacion", cascade = CascadeType.MERGE)
	public List<ItemAdaptacion> getItemsAdaptacion() {
		return itemsAdaptacion;
	}
	public void setItemsAdaptacion(List<ItemAdaptacion> itemsAdaptacion) {
		this.itemsAdaptacion = itemsAdaptacion;
	}
	
	
	
	

}
