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
@Table(name="exam_imagino_consulta")
public class ExamImagenoConsulta implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_examen_imageno", nullable = true)
	@ForeignKey(name = "fk_examen_imageno_exaco")
	private ExamImagenologia examen;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_historia_consulta", nullable = true)
	@ForeignKey(name = "fk_examen_imageno_consulta")
	private ClinicalHistory consulta;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ExamImagenologia getExamen() {
		return examen;
	}

	public void setExamen(ExamImagenologia examen) {
		this.examen = examen;
	}

	public ClinicalHistory getConsulta() {
		return consulta;
	}

	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	

}
