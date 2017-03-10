package com.sa.model.medical;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "recomendacion_med", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class RecomendacionMed implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String codigo;
	
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
	
	@Column(name = "nombre", nullable = true, length=60)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Transient
	public boolean isAsociado() {
		return asociado;
	}
	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}
	
	@Column(name="codigo",nullable=true,length=6)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	
	
}
