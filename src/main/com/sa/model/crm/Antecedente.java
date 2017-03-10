package com.sa.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "antecedente")
public class Antecedente {
	
	private int id;
	private String nombreAntecedente;
	private boolean selected;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_antecedente", nullable = false)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	@Column(name= "nombre_antecedente", nullable=false,length=30)
	public String getNombreAntecedente() {
		return nombreAntecedente;
	}
	public void setNombreAntecedente(String nombreAntecedente) {
		this.nombreAntecedente = nombreAntecedente;
	}
	
	
	@Transient
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	public Antecedente() {
		super();
	}
	
	
	
	
	
	

}
