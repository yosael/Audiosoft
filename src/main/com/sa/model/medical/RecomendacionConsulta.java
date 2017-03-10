package com.sa.model.medical;

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
@Table(name = "recomendacion_consulta")
public class RecomendacionConsulta implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private RecomendacionMed recomendacion;
	private String nomRecomendacion;
	private ClinicalHistory consulta;
	
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
	@JoinColumn(name = "recomendacion_id", nullable = false)
	@ForeignKey(name = "fk_recm_ascon")
	public RecomendacionMed getRecomendacion() {
		return recomendacion;
	}
	public void setRecomendacion(RecomendacionMed recomendacion) {
		this.recomendacion = recomendacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consulta_id", nullable = false)
	@ForeignKey(name = "fk_cons_asrcm")
	public ClinicalHistory getConsulta() {
		return consulta;
	}
	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	@Column(name = "nom_recomendacion", nullable = false, length=80)
	public String getNomRecomendacion() {
		return nomRecomendacion;
	}
	public void setNomRecomendacion(String nomRecomendacion) {
		this.nomRecomendacion = nomRecomendacion;
	}
	
	
	
}
