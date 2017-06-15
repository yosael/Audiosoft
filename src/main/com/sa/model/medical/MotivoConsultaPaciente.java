package com.sa.model.medical;

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
@Table(name="motivo_consulta_paciente")
public class MotivoConsultaPaciente {
	
	
	private Integer id;
	private MotivoConsulta motivo;
	private MedicalAppointment consulta;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_motivo_consulta_paciente", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_motivo", nullable = false)
	@ForeignKey(name = "fk_mcp_motivo")
	public MotivoConsulta getMotivo() {
		return motivo;
	}
	public void setMotivo(MotivoConsulta motivo) {
		this.motivo = motivo;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false)
	@ForeignKey(name = "fk_mcp_consulta")
	public MedicalAppointment getConsulta() {
		return consulta;
	}
	public void setConsulta(MedicalAppointment consulta) {
		this.consulta = consulta;
	}
	
	
	

}
