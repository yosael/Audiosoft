package com.sa.model.medical;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "indice_terapeutico", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class IndiceTerapeutico implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private List<Medicamento> medicamentos;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length=40)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "indiceTer", cascade = CascadeType.REMOVE)
	public List<Medicamento> getMedicamentos() {
		return medicamentos;
	}
	public void setMedicamentos(List<Medicamento> medicamentos) {
		this.medicamentos = medicamentos;
	}
	
	
}
