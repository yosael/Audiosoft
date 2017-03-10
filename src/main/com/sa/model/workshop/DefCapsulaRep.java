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
@Table(name = "def_capsula_rep")
public class DefCapsulaRep implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private DefectoCapsula defCapsula;
	private ReparacionCliente repCliente;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dfcarep_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "defcap_id", nullable = false)
	@ForeignKey(name = "fk_def_cap_rep")
	
	public DefectoCapsula getDefCapsula() {
		return defCapsula;
	}

	public void setDefCapsula(DefectoCapsula defCapsula) {
		this.defCapsula = defCapsula;
	}



	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "repcli_id", nullable = false)
	@ForeignKey(name = "fk_rep_cli_dfc")
	public ReparacionCliente getRepCliente() {
		return repCliente;
	}

	public void setRepCliente(ReparacionCliente repCliente) {
		this.repCliente = repCliente;
	}
	
}
