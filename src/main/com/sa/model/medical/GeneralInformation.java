package com.sa.model.medical;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.crm.Cliente;

/**
 * @author Jesus Duarte
 * @version 1.0
 * @created 18-ene-2011 8:12:39
 */
@Entity
@Table(name = "general_information")
public class GeneralInformation {

	private Long id;
	private Cliente cliente;
	private String familyHeritage;
	private String personalPathologies;
	private String nopatAlcoholism;
	private String nopatSmoking;
	private String nopatAddiction;
	private String nopatAllergies;
	private String femMenarche;
	private String femSexualDevelopment;
	private String femMenstrualRhythm;
	private String femLastMenstruation;
	private String femSexualLife;
	private String femCesarean;
	private String femBirth;
	private String femAbortions;
	private String femContraceptiveMethod;
	private String femMenopause;
	
	public GeneralInformation() {

	}

	public GeneralInformation(Cliente cliente) {
		this.cliente = cliente;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cliente_id", nullable = false)
	@ForeignKey(name = "informacion_general_cliente_fk")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Column(name = "family_heritage", nullable = true)
	public String getFamilyHeritage() {
		return familyHeritage;
	}

	public void setFamilyHeritage(String familyHeritage) {
		this.familyHeritage = familyHeritage;
	}

	@Column(name = "personal_pathologies", nullable = true)
	public String getPersonalPathologies() {
		return personalPathologies;
	}

	public void setPersonalPathologies(String personalPathologies) {
		this.personalPathologies = personalPathologies;
	}

	@Column(name = "nopat_alcholism", nullable = true)
	public String getNopatAlcoholism() {
		return nopatAlcoholism;
	}

	public void setNopatAlcoholism(String nopatAlcoholism) {
		this.nopatAlcoholism = nopatAlcoholism;
	}

	@Column(name = "nopat_smoking", nullable = true)
	public String getNopatSmoking() {
		return nopatSmoking;
	}

	public void setNopatSmoking(String nopatSmoking) {
		this.nopatSmoking = nopatSmoking;
	}

	@Column(name = "nopat_addiction", nullable = true)
	public String getNopatAddiction() {
		return nopatAddiction;
	}

	public void setNopatAddiction(String nopatAddiction) {
		this.nopatAddiction = nopatAddiction;
	}

	@Column(name = "nopat_allergies", nullable = true)
	public String getNopatAllergies() {
		return nopatAllergies;
	}

	public void setNopatAllergies(String nopatAllergies) {
		this.nopatAllergies = nopatAllergies;
	}

	@Column(name = "fem_menarche", nullable = true)
	public String getFemMenarche() {
		return femMenarche;
	}

	public void setFemMenarche(String femMenarche) {
		this.femMenarche = femMenarche;
	}

	@Column(name = "fem_sexual_development", nullable = true)
	public String getFemSexualDevelopment() {
		return femSexualDevelopment;
	}

	public void setFemSexualDevelopment(String femSexualDevelopment) {
		this.femSexualDevelopment = femSexualDevelopment;
	}

	@Column(name = "fem_menstrual_rhythm", nullable = true)
	public String getFemMenstrualRhythm() {
		return femMenstrualRhythm;
	}

	public void setFemMenstrualRhythm(String femMenstrualRhythm) {
		this.femMenstrualRhythm = femMenstrualRhythm;
	}

	@Column(name = "fem_last_menstruation", nullable = true)
	public String getFemLastMenstruation() {
		return femLastMenstruation;
	}

	public void setFemLastMenstruation(String femLastMenstruation) {
		this.femLastMenstruation = femLastMenstruation;
	}

	@Column(name = "fem_sexual_life", nullable = true)
	public String getFemSexualLife() {
		return femSexualLife;
	}

	public void setFemSexualLife(String femSexualLife) {
		this.femSexualLife = femSexualLife;
	}

	@Column(name = "fem_cesarean", nullable = true)
	public String getFemCesarean() {
		return femCesarean;
	}

	public void setFemCesarean(String femCesarean) {
		this.femCesarean = femCesarean;
	}

	@Column(name = "fem_birth", nullable = true)
	public String getFemBirth() {
		return femBirth;
	}

	public void setFemBirth(String femBirth) {
		this.femBirth = femBirth;
	}

	@Column(name = "fem_abortions", nullable = true)
	public String getFemAbortions() {
		return femAbortions;
	}

	public void setFemAbortions(String femAbortions) {
		this.femAbortions = femAbortions;
	}

	@Column(name = "fem_contraceptive_method", nullable = true)
	public String getFemContraceptiveMethod() {
		return femContraceptiveMethod;
	}

	public void setFemContraceptiveMethod(String femContraceptiveMethod) {
		this.femContraceptiveMethod = femContraceptiveMethod;
	}

	@Column(name = "fem_menopause", nullable = true)
	public String getFemMenopause() {
		return femMenopause;
	}

	public void setFemMenopause(String femMenopause) {
		this.femMenopause = femMenopause;
	}

}