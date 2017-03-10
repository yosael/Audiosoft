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
@Table(name = "diagnostico_med", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class DiagnosticoMed implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private String nombre;
	private String descripcion;
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
	
	@Column(name = "nombre", nullable = false, length=40)
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
	
	@Column(name = "codigo", nullable = false, length=10)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Transient
	public boolean isAsociado() {
		return asociado;
	}
	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}
	
	
	
}
