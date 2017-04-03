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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "cod_producto")
public class CodProducto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Inventario inventario;
	private String numSerie;
	private String numLote;
	private Movimiento movimiento;
	private String estado;
	private boolean transferido;
	private Boolean enTransferencia;
	
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
	@JoinColumn(name = "inventario_id", nullable = false)
	@ForeignKey(name = "fk_inv_cod")
	public Inventario getInventario() {
		return inventario;
	}
	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}
	
	@Column(name = "estado", nullable = true, length=3)
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movimiento_id", nullable = true)
	@ForeignKey(name = "fk_mov_cod")
	public Movimiento getMovimiento() {
		return movimiento;
	}
	
	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
	}
	
	@Column(name = "num_serie", nullable = true, length=25)
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}
	
	@Column(name = "num_lote", nullable = true, length=25)
	public String getNumLote() {
		return numLote;
	}
	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}
	
	@Column(name = "en_transferencia", nullable = true)
	public Boolean getEnTransferencia(){
		return this.enTransferencia;
	}
	
	public void setEnTransferencia(Boolean enTransferencia){
		this.enTransferencia = enTransferencia;
	}
	
	@Transient
	public boolean isTransferido() {
		return transferido;
	}
	
	public void setTransferido(boolean transferido) {
		this.transferido = transferido;
	}
	
}
