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

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "cond_aparato_rep")
public class CondAparatoRep implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private CondicionAparato condAparato;
	private ReparacionCliente repCliente;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cndarep_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cndapa_id", nullable = false)
	@ForeignKey(name = "fk_cnd_apa_rep")
	public CondicionAparato getCondAparato() {
		return condAparato;
	}

	public void setCondAparato(CondicionAparato condAparato) {
		this.condAparato = condAparato;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repcli_id", nullable = false)
	@ForeignKey(name = "fk_rep_cli_cnd")
	public ReparacionCliente getRepCliente() {
		return repCliente;
	}

	public void setRepCliente(ReparacionCliente repCliente) {
		this.repCliente = repCliente;
	}
	
}
