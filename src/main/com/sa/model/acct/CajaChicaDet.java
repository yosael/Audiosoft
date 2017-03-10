package com.sa.model.acct;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "caja_chica_det")
public class CajaChicaDet implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fecha;
	private CajaChicaMov movPrincipal;
	private ConceptoMov concepto;
	private String comentario;
	private Double monto;
	private String tipo;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cjcd_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = false)
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concepto_id", nullable = false)
	@ForeignKey(name = "fk_cnc_cjch")
	public ConceptoMov getConcepto() {
		return concepto;
	}

	public void setConcepto(ConceptoMov concepto) {
		this.concepto = concepto;
	}

	@Column(name = "monto", nullable = false)
	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	@Column(name = "tipo", nullable = false, length = 3)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cjachimov_id", nullable = false)
	@ForeignKey(name = "fk_cjc_cjcd")
	public CajaChicaMov getMovPrincipal() {
		return movPrincipal;
	}

	public void setMovPrincipal(CajaChicaMov movPrincipal) {
		this.movPrincipal = movPrincipal;
	}

	@Column(name = "comentario", nullable = true, length = 200)
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

}