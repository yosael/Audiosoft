package com.sa.model.medical;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

import com.sa.model.crm.Cliente;
import com.sa.model.security.Sucursal;
import com.sun.istack.internal.NotNull;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "medical_appointment")
public class MedicalAppointment implements Cloneable {

	private Integer id;
	private Date dateTime;
	// 0 => agendada, 1 => atendida, 2 => cancelada, 3 => inasistencia, 4 => Confirmada, 5 => Bloque reservado
	private Integer status;
	private String cancellationReason;
	private Integer iva;
	private String comment; 
	//private String authorizationNum;
	private Doctor doctor;
	private ClinicalHistory clinicalHistory;
	private Cliente cliente;
	private Sucursal sucursal;
	private Prescription prescription;
	private List<MedicalAppointmentService> medicalAppointmentServices = new ArrayList<MedicalAppointmentService>();
	private List<MedicalAppointmentService> serviciosMedicos = new ArrayList<MedicalAppointmentService>();

	public MedicalAppointment() {
		iva = 0;
		status = 0;
	}

	public MedicalAppointment clone() {
		MedicalAppointment clone = null;
		try {
			clone = (MedicalAppointment) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
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

	@Column(name = "date_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "cancellation_reason", nullable = true, length = 255)
	@Length(max = 255)
	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	@Column(name = "iva", nullable = false)
	public Integer getIva() {
		return iva;
	}

	public void setIva(Integer iva) {
		this.iva = iva;
	}

	@Column(name = "comment", nullable = true, length = 255)
	@Length(max = 255)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
/*
	@Column(name = "authorization_num", nullable = true, length = 50)
	@Pattern(regex = "[0-9]*", message = "#{ofiuco_messages.get('medicalAppointmentDAO_error5')}")
	@Length(max = 50)
	public String getAuthorizationNum() {
		return authorizationNum;
	}

	public void setAuthorizationNum(String authorizationNum) {
		this.authorizationNum = authorizationNum;
	}*/

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "doctor_id", nullable = true)
	@ForeignKey(name = "medical_apopointment_doctor_fk")
	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "medicalAppointment")
	public ClinicalHistory getClinicalHistory() {
		return clinicalHistory;
	}

	public void setClinicalHistory(ClinicalHistory clinicalHistory) {
		this.clinicalHistory = clinicalHistory;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "medicalAppointment", cascade = CascadeType.REMOVE)
	public List<MedicalAppointmentService> getMedicalAppointmentServices() {
		return medicalAppointmentServices;
	}

	public void setMedicalAppointmentServices(
			List<MedicalAppointmentService> medicalAppointmentServices) {
		this.medicalAppointmentServices = medicalAppointmentServices;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cliente_id", nullable = true)
	@ForeignKey(name = "cita_medica_cliente_fk")
	public Cliente getCliente() { 
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "medicalAppointment", cascade = CascadeType.REMOVE)
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}
	
	@Transient
	public List<MedicalAppointmentService> getServiciosMedicos() {
		return serviciosMedicos;
	}

	public void setServiciosMedicos(List<MedicalAppointmentService> serviciosMedicos) {
		this.serviciosMedicos = serviciosMedicos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "ctmd_suc_fk")
	
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	
	
}