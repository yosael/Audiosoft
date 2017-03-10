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
@Table(name = "dosificacion_medicamento")
public class DosificacionMedicamento implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Medicamento medicamento;
	private Dosificacion dosificacion;
	
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
	@JoinColumn(name = "medicamento_id", nullable = false)
	@ForeignKey(name = "fk_med_asdos")
	public Medicamento getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dosificacion_id", nullable = false)
	@ForeignKey(name = "fk_dos_asmed")
	public Dosificacion getDosificacion() {
		return dosificacion;
	}
	public void setDosificacion(Dosificacion dosificacion) {
		this.dosificacion = dosificacion;
	}
	
	
	
}
