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

@Entity
@Table(name = "medicamento_consulta")
public class MedicamentoConsulta implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Short cantidad;
	private Medicamento medicamento;
	private ClinicalHistory consulta;
	private Dosificacion dosificacion;
	private Presentacion presentacion;
	private DosificacionMedicamento selDosif;
	private PresentacionMedicamento selPresen;
	
	//LabSelected nuevo el 04/04/2017
	private LaboratorioMed laboratorio;
	private MedicamentoLaboratorios selLab;
	
	
	//agregado el 26/06/2017
	private String observacion;
	
	
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
	@ForeignKey(name = "fk_med_ascns")
	public Medicamento getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(Medicamento medicamento) {
		this.medicamento = medicamento;
	}
	
	@Column(name = "cantidad", nullable = false)
	public Short getCantidad() {
		return cantidad;
	}
	public void setCantidad(Short cantidad) {
		this.cantidad = cantidad;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "consulta_id", nullable = false)
	@ForeignKey(name = "fk_cns_asmdc")
	public ClinicalHistory getConsulta() {
		return consulta;
	}
	public void setConsulta(ClinicalHistory consulta) {
		this.consulta = consulta;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dosificacion_id", nullable = false)
	@ForeignKey(name = "fk_dos_dlmdc")
	public Dosificacion getDosificacion() {
		return dosificacion;
	}
	public void setDosificacion(Dosificacion dosificacion) {
		this.dosificacion = dosificacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "presentacion_id", nullable = false)
	@ForeignKey(name = "fk_pre_dlmdc")
	public Presentacion getPresentacion() {
		return presentacion;
	}
	public void setPresentacion(Presentacion presentacion) {
		this.presentacion = presentacion;
	}
	
	@Transient
	public DosificacionMedicamento getSelDosif() {
		return selDosif;
	}
	public void setSelDosif(DosificacionMedicamento selDosif) {
		this.selDosif = selDosif;
		setDosificacion(selDosif.getDosificacion());
	}
	
	@Transient
	public PresentacionMedicamento getSelPresen() {
		return selPresen;
	}
	public void setSelPresen(PresentacionMedicamento selPresen) {
		this.selPresen = selPresen;
		setPresentacion(selPresen.getPresentacion());
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "laboratorio_id", nullable = false)
	@ForeignKey(name = "fk_medconsulta_lab")
	public LaboratorioMed getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(LaboratorioMed laboratorio) {
		this.laboratorio = laboratorio;
	}
	
	@Transient
	public MedicamentoLaboratorios getSelLab() {
		return selLab;
	}
	public void setSelLab(MedicamentoLaboratorios selLab) {
		this.selLab = selLab;
		setLaboratorio(selLab.getLaboratorio());
	}
	
	
	@Column(name = "observacion", nullable = true,length=100)
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
	
	
	
	
	
	
}
