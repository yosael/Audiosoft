package com.sa.model.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "medicamento", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Medicamento implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private IndiceTerapeutico indiceTer;
	private SustanciaActiva sustanciaAct;
	//private LaboratorioMed laboratorio;
	private List<DosificacionMedicamento> dosificaciones;
	private List<PresentacionMedicamento> presentaciones;
	
	private List<MedicamentoLaboratorios> medicamentosLab = new ArrayList<MedicamentoLaboratorios>();
	
	private boolean asociado;
	
	//agregado el 05/07/2017
	private String informacionMedicamento;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = true, length=40)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "indice_id", nullable = false)
	@ForeignKey(name = "fk_ind_med")
	public IndiceTerapeutico getIndiceTer() {
		return indiceTer;
	}
	
	public void setIndiceTer(IndiceTerapeutico indiceTer) {
		this.indiceTer = indiceTer;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sustancia_id", nullable = false)
	@ForeignKey(name = "fk_sus_med")
	public SustanciaActiva getSustanciaAct() {
		return sustanciaAct;
	}
	public void setSustanciaAct(SustanciaActiva sustanciaAct) {
		this.sustanciaAct = sustanciaAct;
	}
	
	/*@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "laboratorio_id", nullable = false)
	@ForeignKey(name = "fk_lab_med")
	public LaboratorioMed getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(LaboratorioMed laboratorio) {
		this.laboratorio = laboratorio;
	}*/
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "medicamento", cascade = CascadeType.REMOVE)
	public List<DosificacionMedicamento> getDosificaciones() {
		return dosificaciones;
	}
	public void setDosificaciones(List<DosificacionMedicamento> dosificaciones) {
		this.dosificaciones = dosificaciones;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "medicamento", cascade = CascadeType.REMOVE)
	public List<PresentacionMedicamento> getPresentaciones() {
		return presentaciones;
	}
	public void setPresentaciones(List<PresentacionMedicamento> presentaciones) {
		this.presentaciones = presentaciones;
	}
	
	@Transient
	public boolean isAsociado() {
		return asociado;
	}
	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "medicamento", cascade = CascadeType.REMOVE)
	public List<MedicamentoLaboratorios> getMedicamentosLab() {
		return medicamentosLab;
	}
	public void setMedicamentosLab(List<MedicamentoLaboratorios> medicamentosLab) {
		this.medicamentosLab = medicamentosLab;
	}
	
	
	@Column(name = "informacion_medicamento", nullable = true, length=500)
	public String getInformacionMedicamento() {
		return informacionMedicamento;
	}
	public void setInformacionMedicamento(String informacionMedicamento) {
		this.informacionMedicamento = informacionMedicamento;
	}
	
	
	
	
	
}
