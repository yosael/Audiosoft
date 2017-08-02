package com.sa.model.medical;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "general_medical")
public class GeneralMedical extends ClinicalHistory {

	private Double height;
	private Double weight;
	private Double temperature;
	private Double heartRate;
	private Double respiratoryRate;
	private Double bloodPressureSystole;
	private Double bloodPressureDiastole;
	private String regHead;
	private String regNeck;
	private String regThorax;
	private String regAbdomen;
	private String regTips;
	private String regGenitals;
	
	
	//agredo el 02/08/2017  //Historia Clinica actual
	private String enfermedadActual;
	private String antecedentesMedicos;
	private String antecedentesNoPatologicos;
	private String antecedentesPatologicos;
	
	
	//Nuevo el 14/06/2017
	//Nueva seccion OIDO-NARIZ-GARGANTA
	private String oidos;
	private String narizFosasNasales;
	private String bocaFaringe;
	private String laringe;
	private String cabezaCuello;
	
	
	public GeneralMedical() {

	}

	@Column(name = "height", nullable = true)
	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	@Column(name = "weight", nullable = true)
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Column(name = "temperature", nullable = true)
	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	@Column(name = "heart_rate", nullable = true)
	public Double getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Double heartRate) {
		this.heartRate = heartRate;
	}

	@Column(name = "respiratory_rate", nullable = true)
	public Double getRespiratoryRate() {
		return respiratoryRate;
	}

	public void setRespiratoryRate(Double respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}

	@Column(name = "blood_pressure_systole", nullable = true)
	public Double getBloodPressureSystole() {
		return bloodPressureSystole;
	}

	public void setBloodPressureSystole(Double bloodPressureSystole) {
		this.bloodPressureSystole = bloodPressureSystole;
	}

	@Column(name = "blood_pressure_diastole", nullable = true)
	public Double getBloodPressureDiastole() {
		return bloodPressureDiastole;
	}

	public void setBloodPressureDiastole(Double bloodPressureDiastole) {
		this.bloodPressureDiastole = bloodPressureDiastole;
	}

	@Column(name = "reg_head", nullable = true)
	public String getRegHead() {
		return regHead;
	}

	public void setRegHead(String regHead) {
		this.regHead = regHead;
	}

	@Column(name = "reg_neck", nullable = true)
	public String getRegNeck() {
		return regNeck;
	}

	public void setRegNeck(String regNeck) {
		this.regNeck = regNeck;
	}

	@Column(name = "reg_thorax", nullable = true)
	public String getRegThorax() {
		return regThorax;
	}

	public void setRegThorax(String regThorax) {
		this.regThorax = regThorax;
	}

	@Column(name = "reg_abdomen", nullable = true)
	public String getRegAbdomen() {
		return regAbdomen;
	}

	public void setRegAbdomen(String regAbdomen) {
		this.regAbdomen = regAbdomen;
	}

	@Column(name = "reg_tips", nullable = true)
	public String getRegTips() {
		return regTips;
	}

	public void setRegTips(String regTips) {
		this.regTips = regTips;
	}

	@Column(name = "reg_genitals", nullable = true)
	public String getRegGenitals() {
		return regGenitals;
	}

	public void setRegGenitals(String regGenitals) {
		this.regGenitals = regGenitals;
	}

	public String getOidos() {
		return oidos;
	}

	public void setOidos(String oidos) {
		this.oidos = oidos;
	}

	public String getNarizFosasNasales() {
		return narizFosasNasales;
	}

	public void setNarizFosasNasales(String narizFosasNasales) {
		this.narizFosasNasales = narizFosasNasales;
	}

	public String getBocaFaringe() {
		return bocaFaringe;
	}

	public void setBocaFaringe(String bocaFaringe) {
		this.bocaFaringe = bocaFaringe;
	}

	public String getLaringe() {
		return laringe;
	}

	public void setLaringe(String laringe) {
		this.laringe = laringe;
	}

	public String getCabezaCuello() {
		return cabezaCuello;
	}

	public void setCabezaCuello(String cabezaCuello) {
		this.cabezaCuello = cabezaCuello;
	}
	
	
	

}