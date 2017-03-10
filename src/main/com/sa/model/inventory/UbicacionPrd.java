package com.sa.model.inventory;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotEmpty;

import com.sa.model.security.Sucursal;

@Entity
@Table(name = "ubicacion_prd")
public class UbicacionPrd implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private Sucursal sucursal;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length = 20)
	@NotEmpty
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_ubipr_suc")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	
}
