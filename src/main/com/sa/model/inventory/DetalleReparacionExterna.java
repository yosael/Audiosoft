package com.sa.model.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sa.model.workshop.ReparacionCliente;

@Entity
@Table(name = "detalle_reparacion_externa")
public class DetalleReparacionExterna implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private Integer idDetalleRep;
	private ReparacionExterna reparacionExterna;
	private ReparacionCliente reparacionCliente; //null
	private Producto aparato;
	private String numeroSerie; //null
	private Producto piezaReparacion;//Si la pieza esta vacia significa que se va a reparar el aparato. null
	private Date fechaRecibido; //null
	private String comentario;
	private String estado;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_rep", nullable = false)
	public Integer getIdDetalleRep() {
		return idDetalleRep;
	}
	public void setIdDetalleRep(Integer idDetalleRep) {
		this.idDetalleRep = idDetalleRep;
	}
	
	
	public ReparacionExterna getReparacionExterna() {
		return reparacionExterna;
	}
	public void setReparacionExterna(ReparacionExterna reparacionExterna) {
		this.reparacionExterna = reparacionExterna;
	}
	
	
	public ReparacionCliente getReparacionCliente() {
		return reparacionCliente;
	}
	public void setReparacionCliente(ReparacionCliente reparacionCliente) {
		this.reparacionCliente = reparacionCliente;
	}
	
	
	public Producto getAparato() {
		return aparato;
	}
	public void setAparato(Producto aparato) {
		this.aparato = aparato;
	}
	
	
	public String getNumeroSerie() {
		return numeroSerie;
	}
	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}
	
	
	public Producto getPiezaReparacion() {
		return piezaReparacion;
	}
	public void setPiezaReparacion(Producto piezaReparacion) {
		this.piezaReparacion = piezaReparacion;
	}
	
	
	public Date getFechaRecibido() {
		return fechaRecibido;
	}
	public void setFechaRecibido(Date fechaRecibido) {
		this.fechaRecibido = fechaRecibido;
	}
	
	
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	

}
