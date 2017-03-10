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
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.security.Empresa;

@Entity
@Table(name = "clase_producto")
public class ClaseProducto implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String descripcion;
	private Float gananciaNormal;
	private Float gananciaMinima;
	private Float gananciaOferta;
	private Empresa empresa;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length=10)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_clsprd_emp")
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Column(name = "ganancia_normal", nullable = true)
	public Float getGananciaNormal() {
		return gananciaNormal;
	}

	public void setGananciaNormal(Float gananciaNormal) {
		this.gananciaNormal = gananciaNormal;
	}

	@Column(name = "ganancia_minima", nullable = false)
	public Float getGananciaMinima() {
		return gananciaMinima;
	}

	public void setGananciaMinima(Float gananciaMinima) {
		this.gananciaMinima = gananciaMinima;
	}

	@Column(name = "ganancia_oferta", nullable = false)
	public Float getGananciaOferta() {
		return gananciaOferta;
	}

	public void setGananciaOferta(Float gananciaOferta) {
		this.gananciaOferta = gananciaOferta;
	}

}
