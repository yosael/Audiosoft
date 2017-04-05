package com.sa.model.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "medicamento_laboratorios")
public class MedicamentoLaboratorios implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	
	private Integer id;
	private Medicamento medicamento;
	private LaboratorioMed laboratorio;
	
	
	
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
	@ForeignKey(name = "fk_medlab_med")
	public Medicamento getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "laboratorio_id", nullable = false)
	@ForeignKey(name = "fk_medlab_lab")
	public LaboratorioMed getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(LaboratorioMed laboratorio) {
		this.laboratorio = laboratorio;
	}
	
	
	
	

}
