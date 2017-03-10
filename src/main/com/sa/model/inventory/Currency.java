package com.sa.model.inventory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Min;


@Entity
@Table(name = "currency", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Currency implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String descripcion;
	private Float minVal;
	private Float maxVal;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = true, length=30)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "descripcion", nullable = true, length=200)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "min_val", nullable = true)
	@Min(value = 0)
	public Float getMinVal() {
		return minVal;
	}
	public void setMinVal(Float minVal) {
		this.minVal = minVal;
	}
	
	@Column(name = "max_val", nullable = false)
	@Min(value = 0)
	public Float getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(Float maxVal) {
		this.maxVal = maxVal;
	}
	
	
}
