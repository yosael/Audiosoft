package com.sa.model.sales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "cotizacion_combos_items")
public class CotCmbsItems {
	Integer id;
	CotizacionCombos ctCmbs;
	ItemComboApa item;
	private String tipoPrecio;
	private Float precioCotizado;
	
	// nuevo agregado el 13/07/2017 .. Aqui se guardaran SOLO los servicios que se han agregado desde la adaptacion del combo. Debido a que no estan asociados directamnte al combo. solo cuando se requiera
	private Service servicioCotizado;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctcbitm_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "ctcmbs_id", nullable = false)
	@ForeignKey(name = "fk_ctcbitm_ctcmbs")
	public CotizacionCombos getCtCmbs() {
		return ctCmbs;
	}
	public void setCtCmbs(CotizacionCombos ctCmbs) {
		this.ctCmbs = ctCmbs;
	}
	
	@ManyToOne
	@JoinColumn(name = "itcbap_id", nullable = true)
	@ForeignKey(name = "fk_ctcbitm_itcbap")
	public ItemComboApa getItem() {
		return item;
	}
	
	public void setItem(ItemComboApa item) {
		this.item = item;
	}
	
	@Column (name="tipo_precio")
	public String getTipoPrecio() {
		return tipoPrecio;
	}
	public void setTipoPrecio(String tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}
	
	@Column (name="precio_cotizado")
	public Float getPrecioCotizado() {
		return precioCotizado;
	}
	public void setPrecioCotizado(Float precioCotizado) {
		this.precioCotizado = precioCotizado;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "id_servicio", nullable = true)
	@ForeignKey(name = "fk_servicio_itcbap")
	public Service getServicioCotizado() {
		return servicioCotizado;
	}
	public void setServicioCotizado(Service servicioCotizado) {
		this.servicioCotizado = servicioCotizado;
	}
	
	
	

}
