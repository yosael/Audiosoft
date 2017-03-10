package com.sa.model.sales;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "cotizacion_combos")
public class CotizacionCombos {
	
	private Integer id;
	private CotizacionComboApa cotizacion;
	private ComboAparato combo;
	private String lado;
	private List<CotCmbsItems> itemsCotizados = new ArrayList<CotCmbsItems>();
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ctcmbs_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name = "ctcmap_id", nullable = false)
	@ForeignKey(name = "fk_cotcmb_ctcmap")
	public CotizacionComboApa getCotizacion() {
		return cotizacion;
	}
	public void setCotizacion(CotizacionComboApa cotizacion) {
		this.cotizacion = cotizacion;
	}
	
	@ManyToOne
	@JoinColumn(name = "cmbapa_id", nullable = false)
	@ForeignKey(name = "fk_cotcmb_cmbapa")
	public ComboAparato getCombo() {
		return combo;
	}
	public void setCombo(ComboAparato combo) {
		this.combo = combo;
	}
	
	@Column(name = "cmbapa_lado")
	public String getLado() {
		return lado;
	}
	public void setLado(String lado) {
		this.lado = lado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ctCmbs", cascade = CascadeType.REMOVE)
	public List<CotCmbsItems> getItemsCotizados() {
		return itemsCotizados;
	}
	public void setItemsCotizados(List<CotCmbsItems> itemsCotizados) {
		this.itemsCotizados = itemsCotizados;
	}
}
