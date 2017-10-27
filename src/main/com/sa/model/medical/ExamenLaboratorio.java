package com.sa.model.medical;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="examen_laboratorio")
public class ExamenLaboratorio implements Serializable {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_examen_lab",nullable=false)
	private int idExamenLab;
	
	
	@Column(name="nombre",length=40,nullable=false)
	private String nombre;
	
	@Column(name="codigo",length=8,nullable=true)
	private String codigo;
	
	@Column(name="categoria",length=8,nullable=true)
	private String categoria;

	
	
	public int getIdExamenLab() {
		return idExamenLab;
	}

	public void setIdExamenLab(int idExamenLab) {
		this.idExamenLab = idExamenLab;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	
	

}
