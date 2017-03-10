package com.sa.model.sales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.ServiceClinicalHistory;
import com.sa.model.medical.Specialty;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:39
 */
@Entity
@Table(name = "service")
public class Service {

	private Integer id;
	private String codigo;
	private String name;
	private Double costo;
	private Integer cantidad;
	private String tipoServicio;
	private String description;
	private String estado="ACT";
	private Set<MedicalAppointmentService> medicalAppointmentServices = new HashSet<MedicalAppointmentService>(0);
	private Set<ServiceClinicalHistory> serviceClinicalHistories = new HashSet<ServiceClinicalHistory>(0);
	private Set<Specialty> specialties = new HashSet<Specialty>(0);
	private List<CotizacionPrdSvcAdicionales> cotizSvcAdi = new ArrayList<CotizacionPrdSvcAdicionales>();
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

	@Column(name = "name", nullable = false, length = 255)
	@Length(max = 255)
	@NotEmpty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", nullable = true, length = 255)
	@Length(max = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
	public Set<MedicalAppointmentService> getMedicalAppointmentServices() {
		return medicalAppointmentServices;
	}

	public void setMedicalAppointmentServices(
			Set<MedicalAppointmentService> medicalAppointmentServices) {
		this.medicalAppointmentServices = medicalAppointmentServices;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "service")
	@Cascade({CascadeType.REMOVE})
	public Set<ServiceClinicalHistory> getServiceClinicalHistories() {
		return serviceClinicalHistories;
	}

	public void setServiceClinicalHistories(
			Set<ServiceClinicalHistory> serviceClinicalHistories) {
		this.serviceClinicalHistories = serviceClinicalHistories;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "services")
	public Set<Specialty> getSpecialties() {
		return specialties;
	}

	public void setSpecialties(Set<Specialty> specialties) {
		this.specialties = specialties;
	}

	@Column(name = "costo", nullable = false)
	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	//TIpo de servicio: TLL = Taller, MED = Medico, CMB = Combo, EXA = Examen
	@Column(name = "tipo_servicio", nullable = false, length = 3)
	public String getTipoServicio() {
		return tipoServicio;
	}

	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	@Column(name = "codigo", nullable = false, length = 10)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Transient
	public Integer getCantidad() {
		if(cantidad != null)
			return cantidad;
		else
			return 0;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "servicio")
	@Cascade({CascadeType.REMOVE})
	public List<CotizacionPrdSvcAdicionales> getCotizSvcAdi() {
		return cotizSvcAdi;
	}

	public void setCotizSvcAdi(List<CotizacionPrdSvcAdicionales> cotizSvcAdi) {
		this.cotizSvcAdi = cotizSvcAdi;
	}

	@Transient
	public boolean isAsociado() {
		return asociado;
	}

	public void setAsociado(boolean asociado) {
		this.asociado = asociado;
	}

	
	
	
	
	
	
	
}