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
	private String antecedentesFamiliares;
	private String antecedentesNoPatologicos;
	private String antecedentesPatologicos;
	
	
	//EXPLORACION FISICA. 07/08/2017
	private String inspeccionGeneral;
	
	private String pulso;
	private String talla;
	
	
	//Nuevo el 14/06/2017
	//Nueva seccion OIDO-NARIZ-GARGANTA
	private String oidos;
	private String narizFosasNasales;
	private String bocaFaringe;
	private String laringe;
	private String cabezaCuello;
	
	//nuevo el 07/08/2017
	private String sistemaNervioso;
	
	//EXAMENES COMPLEMENTARIOS. 07/08/2017
	private String exaAudiologia;
	private String exaOtoneurologia;
	private String labClinico;
	private String radioImagenologia; //radiograficas, imagenologia USG-DUPLIER
	
	private byte[] imagenExaAudiologia;
	
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
	
	//nuevo

	public String getEnfermedadActual() {
		return enfermedadActual;
	}

	public void setEnfermedadActual(String enfermedadActual) {
		this.enfermedadActual = enfermedadActual;
	}

	public String getAntecedentesFamiliares() {
		return antecedentesFamiliares;
	}

	public void setAntecedentesFamiliares(String antecedentesFamiliares) {
		this.antecedentesFamiliares = antecedentesFamiliares;
	}

	public String getAntecedentesNoPatologicos() {
		return antecedentesNoPatologicos;
	}

	public void setAntecedentesNoPatologicos(String antecedentesNoPatologicos) {
		this.antecedentesNoPatologicos = antecedentesNoPatologicos;
	}

	public String getAntecedentesPatologicos() {
		return antecedentesPatologicos;
	}

	public void setAntecedentesPatologicos(String antecedentesPatologicos) {
		this.antecedentesPatologicos = antecedentesPatologicos;
	}

	public String getSistemaNervioso() {
		return sistemaNervioso;
	}

	public void setSistemaNervioso(String sistemaNervioso) {
		this.sistemaNervioso = sistemaNervioso;
	}

	public String getInspeccionGeneral() {
		return inspeccionGeneral;
	}

	public void setInspeccionGeneral(String inspeccionGeneral) {
		this.inspeccionGeneral = inspeccionGeneral;
	}

	public String getPulso() {
		return pulso;
	}

	public void setPulso(String pulso) {
		this.pulso = pulso;
	}

	public String getTalla() {
		return talla;
	}

	public void setTalla(String talla) {
		this.talla = talla;
	}

	public String getExaAudiologia() {
		return exaAudiologia;
	}

	public void setExaAudiologia(String exaAudiologia) {
		this.exaAudiologia = exaAudiologia;
	}

	public String getExaOtoneurologia() {
		return exaOtoneurologia;
	}

	public void setExaOtoneurologia(String exaOtoneurologia) {
		this.exaOtoneurologia = exaOtoneurologia;
	}

	public String getLabClinico() {
		return labClinico;
	}

	public void setLabClinico(String labClinico) {
		this.labClinico = labClinico;
	}

	public String getRadioImagenologia() {
		return radioImagenologia;
	}

	public void setRadioImagenologia(String radioImagenologia) {
		this.radioImagenologia = radioImagenologia;
	}

	
	@Column(name= "imagen_exa_audiologia", nullable = true)
	public byte[] getImagenExaAudiologia() {
		return imagenExaAudiologia;
	}

	public void setImagenExaAudiologia(byte[] imagenExaAudiologia) {
		this.imagenExaAudiologia = imagenExaAudiologia;
	}
	
	
	
	
	////
	
	
	

}