package com.sa.model.medical;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

import com.sa.model.security.Usuario;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "doctor")
public class Doctor implements Serializable /* extends Internal*/ {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombres;
	private String apellidos;
	private String email; 
	private String extraInfo;
	private String profesionalCard;
	private String profesionalInfo;
	private Usuario usuario;
	private Set<Specialty> specialties = new HashSet<Specialty>(0);
	private Set<MedicalAppointment> medicalAppointments = new HashSet<MedicalAppointment>(0);
	private Set<ClinicalHistory> clinicalHistories = new HashSet<ClinicalHistory>(0);
	
	//private List<Specialty> listaSp;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombres", nullable = false, length = 50)
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Column(name = "apellidos", nullable = false, length = 50)
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Column(name = "email", nullable = true, length = 100)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "doctor_specialty", joinColumns = {
			@JoinColumn(name = "doctor_id", nullable = false)}, 
			inverseJoinColumns = { @JoinColumn(name = "specialty_id", referencedColumnName = "id") })
	@ForeignKey(name = "doctor_especialidad_doctor_fk", inverseName = "doctor_especialidad_especialidad_fk")
	public Set<Specialty> getSpecialties() {
		return specialties;
	}

	public void setSpecialties(Set<Specialty> specialties) {
		this.specialties = specialties;
	}

	@OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
	public Set<MedicalAppointment> getMedicalAppointments() {
		return medicalAppointments;
	}

	public void setMedicalAppointments(
			Set<MedicalAppointment> medicalAppointments) {
		this.medicalAppointments = medicalAppointments;
	}

	@OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
	public Set<ClinicalHistory> getClinicalHistories() {
		return clinicalHistories;
	}

	public void setClinicalHistories(Set<ClinicalHistory> clinicalHistories) {
		this.clinicalHistories = clinicalHistories;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id", nullable = false)
	@ForeignKey(name = "fk_usr_doc")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Transient
	public String getFullName(){
		return getNombres()+" " +getApellidos();
	}
	
	@Column(name = "extra_info", length = 300, nullable = true)	 
	@Length(max = 300)
	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	@Column(name = "profesional_card", length = 100, nullable = true)
	@Length(max = 100)
	public String getProfesionalCard() {
		return profesionalCard;
	}

	public void setProfesionalCard(String profesionalCard) {
		this.profesionalCard = profesionalCard;
	}

	@Column(name = "profesional_info", length = 200, nullable = true)
	@Length(max = 200)
	public String getProfesionalInfo() {
		return profesionalInfo;
	}

	public void setProfesionalInfo(String profesionalInfo) {
		this.profesionalInfo = profesionalInfo;
	}

	@Transient
	public String getNombreCompleto() {
		String res = "";
		if(nombres != null)
			res.concat(nombres);
		if(apellidos != null)
			res.concat(apellidos);
		
			return res;
	}
	
	
	/*@OneToMany(fetch = FetchType.LAZY, mappedBy = "doctor", cascade = CascadeType.REMOVE)
	public List<Specialty> getListaSp() {
		return listaSp;
	}

	public void setListaSp(List<Specialty> listaSp) {
		this.listaSp = listaSp;
	}
	
	*/
	
	
}