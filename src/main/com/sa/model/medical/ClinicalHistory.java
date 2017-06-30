package com.sa.model.medical;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

import com.sa.model.crm.Cliente;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:39
 */
@Entity
@Table(name = "clinical_history")
@Inheritance(strategy = InheritanceType.JOINED)
public class ClinicalHistory {

	private Long consecutive;
	private Date creationDate;
	private Date lastModificationDate;
	private String consultationReason;
	private String observation;
	private String treatmentDescriptive;
	private String examsDescriptive;
	private Doctor doctor;
	private MedicalAppointment medicalAppointment;
	private Cliente cliente;
	
	private List<ServiceClinicalHistory> serviceClinicalHistories = new ArrayList<ServiceClinicalHistory>(0);
	private List<MedicamentoConsulta> medicamentos = new ArrayList<MedicamentoConsulta>();
	private List<RecomendacionConsulta> recomendaciones = new ArrayList<RecomendacionConsulta>();
	private List<DiagnosticoConsulta> diagnosticos = new ArrayList<DiagnosticoConsulta>();
	private List<ExamenConsulta> examenes = new ArrayList<ExamenConsulta>();
	
	private String proximaCita;
	
	
	
	public ClinicalHistory() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Long getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(Long consecutive) {
		this.consecutive = consecutive;
	}

	@Column(name = "creation_date", nullable = false)
	//@Temporal(TemporalType.DATE)
	//@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		
		/*java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setTime(creationDate);
		System.out.println("******************");
		System.out.println("******************");
		System.out.println("La fecha ingresada es "+cal.getTime());
		System.out.println("******************");
		System.out.println("******************");*/
		/*DateFormat newdateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Date ndia1=null;
		String fechaString=newdateFormat1.format(creationDate);
		Date fechafinal=new Date();
		
		try {
			
			fechafinal= newdateFormat1.parse(fechaString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("*********Fecha Final "+fechafinal);*/
		
		//this.creationDate =creationDate;
		this.creationDate =creationDate;
	}

	@Column(name = "last_modification_date", nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	@Column(name = "consultation_reason", nullable = true)
	public String getConsultationReason() {
		return consultationReason;
	}

	public void setConsultationReason(String consultationReason) {
		this.consultationReason = consultationReason;
	}

	@Column(name = "observation", nullable = true)
	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	@Column(name = "treatment_descriptive", nullable = true)
	public String getTreatmentDescriptive() {
		return treatmentDescriptive;
	}

	public void setTreatmentDescriptive(String treatmentDescriptive) {
		this.treatmentDescriptive = treatmentDescriptive;
	}

	@Column(name = "exams_descriptive", nullable = true)
	public String getExamsDescriptive() {
		return examsDescriptive;
	}

	public void setExamsDescriptive(String examsDescriptive) {
		this.examsDescriptive = examsDescriptive;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "doctor_id", nullable = false)
	@ForeignKey(name = "clinical_history_doctor_fk")
	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medical_appointment_id", nullable = true)
	@ForeignKey(name = "clinical_history_medical_appointment_fk")
	public MedicalAppointment getMedicalAppointment() {
		return medicalAppointment;
	}

	public void setMedicalAppointment(MedicalAppointment medicalAppointment) {
		this.medicalAppointment = medicalAppointment;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "clinicalHistory")
	public List<ServiceClinicalHistory> getServiceClinicalHistories() {
		return serviceClinicalHistories;
	}

	public void setServiceClinicalHistories(
			List<ServiceClinicalHistory> serviceClinicalHistories) {
		this.serviceClinicalHistories = serviceClinicalHistories;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "historia_clinica_cliente_fk")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "consulta")
	public List<MedicamentoConsulta> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(List<MedicamentoConsulta> medicamentos) {
		this.medicamentos = medicamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "consulta")
	public List<RecomendacionConsulta> getRecomendaciones() {
		return recomendaciones;
	}

	public void setRecomendaciones(List<RecomendacionConsulta> recomendaciones) {
		this.recomendaciones = recomendaciones;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "consulta")
	public List<ExamenConsulta> getExamenes() {
		return examenes;
	}

	public void setExamenes(List<ExamenConsulta> examenes) {
		this.examenes = examenes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "consulta")
	public List<DiagnosticoConsulta> getDiagnosticos() {
		return diagnosticos;
	}

	public void setDiagnosticos(List<DiagnosticoConsulta> diagnosticos) {
		this.diagnosticos = diagnosticos;
	}
	
	
	@Column(name="proxima_cita",nullable=true,length=50)
	public String getProximaCita() {
		return proximaCita;
	}

	public void setProximaCita(String proximaCita) {
		this.proximaCita = proximaCita;
	}
	
	
	
	

}