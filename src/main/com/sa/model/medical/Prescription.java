package com.sa.model.medical;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.inventory.ItemPrescription;
import com.sa.model.inventory.Movimiento;
//import com.sun.istack.internal.NotNull;

@Entity
@Table(name = "prescription")
public class Prescription implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer estado; // 0 => Pendiente, 1 => Despachada, 2 => Cancelada
	private Date fechaGeneracion;
	private Date fechaDespacho;
	private String observaciones;
	private MedicalAppointment medicalAppointment;
	private Movimiento movimiento;
	private Set<ItemPrescription> itemsPrescriptions = new HashSet<ItemPrescription>();

	public Prescription(){
		this.estado = 0;
		this.fechaGeneracion = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "estado", nullable = false)
	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	@Column(name = "fecha_generacion", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaGeneracion() {
		return fechaGeneracion;
	}

	public void setFechaGeneracion(Date fechaGeneracion) {
		this.fechaGeneracion = fechaGeneracion;
	}

	@Column(name = "fecha_despacho", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFechaDespacho() {
		return fechaDespacho;
	}

	public void setFechaDespacho(Date fechaDespacho) {
		this.fechaDespacho = fechaDespacho;
	}

	@Column(name = "observaciones", nullable = true)
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cita_id", nullable = false)
	@ForeignKey(name = "cita_medica_prescripcion_fk")
	public MedicalAppointment getMedicalAppointment() {
		return medicalAppointment;
	}

	public void setMedicalAppointment(MedicalAppointment medicalAppointment) {
		this.medicalAppointment = medicalAppointment;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movimiento_id", nullable = true)
	@ForeignKey(name = "fk_prescription_movimiento")
	public Movimiento getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prescription", cascade = CascadeType.REMOVE)
	public Set<ItemPrescription> getItemsPrescriptions() {
		return itemsPrescriptions;
	}

	public void setItemsPrescriptions(Set<ItemPrescription> itemsPrescriptions) {
		this.itemsPrescriptions = itemsPrescriptions;
	}
	
}
