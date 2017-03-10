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
@Table(name = "diagnostico_consulta")
public class DiagnosticoConsulta implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private DiagnosticoMed diagnostico;
	private String nomDiagnostico;
	private ClinicalHistory consulta;
	private boolean principal;
	
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
	@JoinColumn(name = "diagnostico_id", nullable = true)
	@ForeignKey(name = "fk_diagn_ascon")
	public DiagnosticoMed getDiagnostico() {
		return diagnostico;
	}
	public void setDiagnostico(DiagnosticoMed diagnostico) {
		this.diagnostico = diagnostico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consulta_id", nullable = false)
	@ForeignKey(name = "fk_cons_asdnc")
	public ClinicalHistory getConsulta() {
		return consulta;
	}
	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	@Column(name = "nom_diagnostico", nullable = false, length = 80)
	public String getNomDiagnostico() {
		return nomDiagnostico;
	}
	public void setNomDiagnostico(String nomDiagnostico) {
		this.nomDiagnostico = nomDiagnostico;
	}
	
	@Column(name = "principal", nullable = false)
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
	
	
	
}
