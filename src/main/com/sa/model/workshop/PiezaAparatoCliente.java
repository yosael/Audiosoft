package com.sa.model.workshop;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "pieza_aparato_cliente")
public class PiezaAparatoCliente implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombre;
	private String marca;
	private String modelo;
	private String numSerie;
	private String numLote;
	private Integer idPrd;
	private AparatoCliente aparatoCliente;
	private boolean principal;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "piapcli_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = true, length = 200)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "marca", nullable = true, length = 50)
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	@Column(name = "modelo", nullable = true, length = 95)
	public String getModelo() {
		return modelo;
	}
	
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	
	@Column(name = "numSerie", nullable = true, length = 200)
	public String getNumSerie() {
		return numSerie;
	}
	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}
	
	@Column(name = "num_lote", nullable = true, length = 80)
	public String getNumLote() {
		return numLote;
	}
	
	public void setNumLote(String numLote) {
		this.numLote = numLote;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apacli_id", nullable = false)
	@ForeignKey(name = "fk_apc_prt")
	public AparatoCliente getAparatoCliente() {
		return aparatoCliente;
	}
	public void setAparatoCliente(AparatoCliente aparatoCliente) {
		this.aparatoCliente = aparatoCliente;
	}
	
	@Column(name = "id_prd", nullable = true)
	public Integer getIdPrd() {
		return idPrd;
	}
	public void setIdPrd(Integer idPrd) {
		this.idPrd = idPrd;
	}
	
	@Transient
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	
}
