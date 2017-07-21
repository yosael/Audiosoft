package com.sa.model.workshop;

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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.sales.Service;

@Entity
@Table(name = "servicio_reparacion")
public class ServicioReparacion implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private ReparacionCliente reparacion;
	private Service servicio;
	private String estado;
	private String generado;
	private boolean cobrar;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reparacion_id", nullable = false)
	@ForeignKey(name = "fk_rep_assrv")
	public ReparacionCliente getReparacion() {
		return reparacion;
	}
	public void setReparacion(ReparacionCliente reparacion) {
		this.reparacion = reparacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "servicio_id", nullable = false)
	@ForeignKey(name = "fk_srv_asrep")
	public Service getServicio() {
		return servicio;
	}
	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}
	
	@Column(name = "estado", nullable = true)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@Column(name = "generado", nullable = true,length=3)
	public String getGenerado() {
		return generado;
	}
	public void setGenerado(String generado) {
		this.generado = generado;
	}
	
	@Transient
	public boolean isCobrar() {
		return cobrar;
	}
	public void setCobrar(boolean cobrar) {
		this.cobrar = cobrar;
	}
	
	
	
	
}
