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
@Table(name="examen_lab_consulta")
public class ExamenLabConsulta implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_examen_lab", nullable = true)
	@ForeignKey(name = "fk_examen_lab_exlab")
	private ExamenLaboratorio examenLab;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_historia_consulta", nullable = true)
	@ForeignKey(name = "fk_examen_lab_consulta")
	private ClinicalHistory consulta;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ExamenLaboratorio getExamenLab() {
		return examenLab;
	}

	public void setExamenLab(ExamenLaboratorio examenLab) {
		this.examenLab = examenLab;
	}

	public ClinicalHistory getConsulta() {
		return consulta;
	}

	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	
	

}
