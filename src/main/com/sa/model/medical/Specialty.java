package com.sa.model.medical;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

import com.sa.model.sales.Service;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "specialty")
public class Specialty {

	private Integer id;
	private String code;
	private String name;
	private String description;
	private Set<Doctor> doctors = new HashSet<Doctor>(0);
	private Set<Service> services = new HashSet<Service>(0);
	
	public Specialty() {

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

	@Column(name = "code", length = 20, nullable = true)
	@Length(max = 20)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name", length = 150, nullable = false)
	@Length(max = 150)
	@NotEmpty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", length = 200, nullable = true)
	@Length(max = 200)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "specialties")
	public Set<Doctor> getDoctors() {
		return doctors;
	}

	public void setDoctors(Set<Doctor> doctors) {
		this.doctors = doctors;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "specialty_service", joinColumns = { @JoinColumn(name = "specialty_id", referencedColumnName = "id") }, 
		inverseJoinColumns = { @JoinColumn(name = "service_id", referencedColumnName = "id") })
	@ForeignKey(name = "specialty_service_specialty_fk", inverseName = "specialty_service_service_fk")
	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
	}

}