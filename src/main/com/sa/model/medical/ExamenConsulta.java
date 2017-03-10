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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.sales.Service;

@Entity
@Table(name = "examen_consulta")
public class ExamenConsulta implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Service examen;
	private String nomExamen;
	private String comentario;
	private ClinicalHistory consulta;
	private boolean asociado;
	
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
	@JoinColumn(name = "examen_id", nullable = true)
	@ForeignKey(name = "fk_exa_ascon")
	public Service getExamen() {
		return examen;
	}
	public void setExamen(Service examen) {
		this.examen = examen;
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
	
	@Column(name = "comentario", nullable = true, length =400)
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	@Column(name = "nom_examen", nullable = false, length =80)
	public String getNomExamen() {
		return nomExamen;
	}
	public void setNomExamen(String nomExamen) {
		this.nomExamen = nomExamen;
	}
	
	
	@Transient
	public boolean isAsociado() {
		return asociado;
	}
	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}
	
	
	
}
