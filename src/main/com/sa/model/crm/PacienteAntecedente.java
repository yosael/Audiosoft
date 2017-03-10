package com.sa.model.crm;

import java.util.Date;

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
@Table(name="paciente_antecedente")
public class PacienteAntecedente {
	
	private Integer id;
	private Antecedente antecedente;
	private Cliente paciente;
	private Date fechaAntecedente;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pa", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_antecedente", nullable = false)
	@ForeignKey(name = "fk_pa_antecedente")
	public Antecedente getAntecedente() {
		return antecedente;
	}
	public void setAntecedente(Antecedente antecedente) {
		this.antecedente = antecedente;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "fk_pa_cliente")
	public Cliente getPaciente() {
		return paciente;
	}
	public void setPaciente(Cliente paciente) {
		this.paciente = paciente;
	}
	
	
	@Column(name = "fecha_antecedente", nullable = false)
	public Date getFechaAntecedente() {
		return fechaAntecedente;
	}
	public void setFechaAntecedente(Date fechaAntecedente) {
		this.fechaAntecedente = fechaAntecedente;
	}
	
	
	
	

}
