package com.sa.model.sales;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tasa_tarjeta_cred")
public class TasaTarjetaCred implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private Float porcentaje;
	private Short pagos;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tstrc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length = 80)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "porcentaje", nullable = false, length = 3)
	public Float getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(Float porcentaje) {
		this.porcentaje = porcentaje;
	}
	
	@Column(name = "pagos", nullable = false)
	public Short getPagos() {
		return pagos;
	}
	public void setPagos(Short pagos) {
		this.pagos = pagos;
	}
	
	
	
}