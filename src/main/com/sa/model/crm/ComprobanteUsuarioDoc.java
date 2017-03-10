package com.sa.model.crm;
 
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

import com.sa.model.security.Usuario;
import com.sa.model.vta.ComprobanteImpresion;

@Entity 
@Table(name = "comprobante_usuario_doc")
public class ComprobanteUsuarioDoc implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Usuario usuario;
	private ComprobanteImpresion comprobante;
	 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cpusrdc_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = true)
	@ForeignKey(name = "fk_usr_cusrd")
	public Usuario getUsuario() {
		return usuario;
	} 

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cprimp_id", nullable = true)
	@ForeignKey(name = "fk_ci_cusrd")
	public ComprobanteImpresion getComprobante() {
		return comprobante;
	}

	public void setComprobante(ComprobanteImpresion comprobante) {
		this.comprobante = comprobante;
	}
}
