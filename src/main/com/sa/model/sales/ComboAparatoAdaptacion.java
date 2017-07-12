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

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "combo_aparato_adaptacion")
public class ComboAparatoAdaptacion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private AdaptacionCombo adaptacion;
	private ComboAparato comboAparato;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_comboapa_adap",nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="id_adaptacion",nullable=false)
	@ForeignKey(name = "fk_comboapa_adap_adap")
	public AdaptacionCombo getAdaptacion() {
		return adaptacion;
	}
	public void setAdaptacion(AdaptacionCombo adaptacion) {
		this.adaptacion = adaptacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_combo_aparato",nullable=false)
	@ForeignKey(name = "fk_comboapa_adap_comboapa")
	public ComboAparato getComboAparato() {
		return comboAparato;
	}
	public void setComboAparato(ComboAparato comboAparato) {
		this.comboAparato = comboAparato;
	}
	
	
	

}
