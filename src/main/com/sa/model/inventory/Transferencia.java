package com.sa.model.inventory;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "transferencia")
public class Transferencia extends Movimiento{

	private static final long serialVersionUID = -4435882679592993729L;
	private String observacionDestino;
	private Date fechaRecibo;
	private String estado; //G=Generada; P=Pendiente; A=Aceptado; D=Descartado; R=Rechazado;
	private Sucursal sucursalDestino;
	private Usuario usuarioGenera;
	private Movimiento entrada;
	
	public Transferencia(){
		super.setFecha(new Date());
		super.setTipoMovimiento("S");
		super.setRazon("T");
		this.estado = "G";
	}
	
	@Column(name = "observacion_destino", nullable = true)
	public String getObservacionDestino() {
		return observacionDestino;
	}
	
	public void setObservacionDestino(String observacionDestino) {
		this.observacionDestino = observacionDestino;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_recibo", nullable = true)
	public Date getFechaRecibo() {
		return fechaRecibo;
	}
	
	public void setFechaRecibo(Date fechaRecibo) {
		this.fechaRecibo = fechaRecibo;
	}
	
	@Column(name = "estado", nullable = false)
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_destino_id", nullable = false)
	@ForeignKey(name = "fk_transfer_sucursal_destino")
	public Sucursal getSucursalDestino() {
		return sucursalDestino;
	}
	
	public void setSucursalDestino(Sucursal sucursalDestino) {
		this.sucursalDestino = sucursalDestino;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_genera_id", nullable = true)
	@ForeignKey(name = "fk_transferencia_usuario")
	public Usuario getUsuarioGenera() {
		return usuarioGenera;
	}

	public void setUsuarioGenera(Usuario usuarioGenera) {
		this.usuarioGenera = usuarioGenera;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entrada_id", nullable = true)
	@ForeignKey(name = "fk_transferencia_entrada")
	public Movimiento getEntrada() {
		return entrada;
	}

	public void setEntrada(Movimiento entrada) {
		this.entrada = entrada;
	}
	
	
	
	
	
	
	
	
}
