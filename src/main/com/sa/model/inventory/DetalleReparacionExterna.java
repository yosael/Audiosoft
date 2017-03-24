package com.sa.model.inventory;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Sucursal;
import com.sa.model.workshop.ReparacionCliente;

@Entity
@Table(name = "detalle_reparacion_externa")
public class DetalleReparacionExterna implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private Integer idDetalleRep;
	private ReparacionExterna reparacionExterna;
	private ReparacionCliente reparacionCliente; //null
	private Producto aparato;
	private CodProducto codigo; //null
	private Producto piezaReparacion;//Si la pieza esta vacia significa que se va a reparar el aparato. null
	private Date fechaRecibido; //null
	private Date fechaModificacion;
	private String comentario;
	private String estado;
	private Integer idNuevoCodigo;
	private Boolean llevaPieza;
	
	//private Sucursal sucursal;
	
	//No son parte de la db
	private CodProducto nuevoCodigo = new CodProducto();
	private int candidad;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_detalle_rep", nullable = false)
	public Integer getIdDetalleRep() {
		return idDetalleRep;
	}
	public void setIdDetalleRep(Integer idDetalleRep) {
		this.idDetalleRep = idDetalleRep;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reparacion_externa", nullable = false)
	@ForeignKey(name = "fk_detalle_reparacion_reparacion")
	public ReparacionExterna getReparacionExterna() {
		return reparacionExterna;
	}
	public void setReparacionExterna(ReparacionExterna reparacionExterna) {
		this.reparacionExterna = reparacionExterna;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reparacion_cliente", nullable = true)
	@ForeignKey(name = "fk_detalle_reparacion_reparacion_cliente")
	public ReparacionCliente getReparacionCliente() {
		return reparacionCliente;
	}
	public void setReparacionCliente(ReparacionCliente reparacionCliente) {
		this.reparacionCliente = reparacionCliente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aparato", nullable = false)
	@ForeignKey(name = "fk_detalle_reparacion_aparato")
	public Producto getAparato() {
		return aparato;
	}
	public void setAparato(Producto aparato) {
		this.aparato = aparato;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo", nullable = true)
	@ForeignKey(name = "fk_detalle_reparacion_codigo")
	public CodProducto getCodigo() {
		return codigo;
	}
	public void setCodigo(CodProducto codigo) {
		this.codigo = codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pieza_reparacion", nullable = true)
	@ForeignKey(name = "fk_detalle_reparacion_pieza")
	public Producto getPiezaReparacion() {
		
		return piezaReparacion;
	}
	public void setPiezaReparacion(Producto piezaReparacion) {
		this.piezaReparacion = piezaReparacion;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_recibido", nullable = true)
	public Date getFechaRecibido() {
		return fechaRecibido;
	}
	public void setFechaRecibido(Date fechaRecibido) {
		this.fechaRecibido = fechaRecibido;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_modificacion", nullable = true)
	public Date getFechaModificacion() {
		return fechaModificacion;
	}
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	@Column(name = "comentario", nullable = true, length=80)
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	@Column(name = "estado", nullable = false, length=15)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal", nullable = true)
	@ForeignKey(name = "fk_detalle_reparacion_sucursal")
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}*/
	
	@Transient
	public CodProducto getNuevoCodigo() {
		return nuevoCodigo;
	}
	public void setNuevoCodigo(CodProducto nuevoCodigo) {
		this.nuevoCodigo = nuevoCodigo;
	}
	
	@Transient
	public int getCandidad() {
		return candidad;
	}
	public void setCandidad(int candidad) {
		this.candidad = candidad;
	}
	
	
	@Column(name = "id_nuevo_codigo", nullable = true)
	public Integer getIdNuevoCodigo() {
		return idNuevoCodigo;
	}
	public void setIdNuevoCodigo(Integer idNuevoCodigo) {
		this.idNuevoCodigo = idNuevoCodigo;
	}
	
	
	@Column(name = "lleva_pieza", nullable = true)
	public Boolean getLlevaPieza() {
		return llevaPieza;
	}
	public void setLlevaPieza(Boolean llevaPieza) {
		this.llevaPieza = llevaPieza;
	}
	
	
	

}
