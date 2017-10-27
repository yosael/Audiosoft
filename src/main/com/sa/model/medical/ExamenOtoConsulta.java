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
@Table(name="examen_oto_consulta")
public class ExamenOtoConsulta implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_examen_oto", nullable = true)
	@ForeignKey(name = "fk_examen_oto_exaco")
	private ExamenOtoneurologia examen;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_historia_consulta", nullable = true)
	@ForeignKey(name = "fk_examen_oto_consulta")
	private ClinicalHistory consulta;
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ExamenOtoneurologia getExamen() {
		return examen;
	}

	public void setExamen(ExamenOtoneurologia examen) {
		this.examen = examen;
	}

	public ClinicalHistory getConsulta() {
		return consulta;
	}

	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	
	

}
