package com.sa.model.medical;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.validator.Length;

import com.sa.model.security.Usuario;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "internal")
@Inheritance(strategy = InheritanceType.JOINED)
public class Internal extends Usuario {

	private static final long serialVersionUID = 1L;
	private String extraInfo;
	private String profesionalCard;
	private String profesionalInfo;
	private boolean doctor;

	public Internal() {
		super();
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

	@Column(name = "doctor", nullable = false)
	public boolean isDoctor() {
		return doctor;
	}

	public void setDoctor(boolean doctor) {
		this.doctor = doctor;
	}

}