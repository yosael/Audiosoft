package com.sa.model.sales;

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
@Table(name = "costo_servicio")
public class CostoServicio implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private ComboAparato combo;
	private Service servicio;
	private Float valor;
	private boolean showCotizacion;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cscmbap_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "combo_id", nullable = false)
	@ForeignKey(name = "fk_cmb_cscmba")
	public ComboAparato getCombo() {
		return combo;
	}
	public void setCombo(ComboAparato combo) {
		this.combo = combo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = false)
	@ForeignKey(name = "fk_srv_cscmba")
	public Service getServicio() {
		return servicio;
	}
	public void setServicio(Service servicio) {
		this.servicio = servicio;
	}
	
	@Column(name = "valor", nullable = false)
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
	}
	
	@Transient
	public boolean isShowCotizacion() {
		return showCotizacion;
	}
	public void setShowCotizacion(boolean showCotizacion) {
		this.showCotizacion = showCotizacion;
	}
	
	
	
}
