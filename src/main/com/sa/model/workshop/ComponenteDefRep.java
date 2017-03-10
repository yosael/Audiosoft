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
@Table(name = "componente_def_rep")
public class ComponenteDefRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private ComponenteAparato cmpAparato;
	private ReparacionCliente repCliente;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cmdfrep_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmpapa_id", nullable = false)
	@ForeignKey(name = "fk_cmp_apa")
	public ComponenteAparato getCmpAparato() {
		return cmpAparato;
	}

	public void setCmpAparato(ComponenteAparato cmpAparato) {
		this.cmpAparato = cmpAparato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repcli_id", nullable = false)
	@ForeignKey(name = "fk_rep_cli_cmp")
	public ReparacionCliente getRepCliente() {
		return repCliente;
	}

	public void setRepCliente(ReparacionCliente repCliente) {
		this.repCliente = repCliente;
	}
	
	
}
