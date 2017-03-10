package com.sa.model.security;

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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@Entity
@Table(name = "parametro_sistema", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "sistema_id"}))
public class ParametroSistema implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private String nombre;
	private String descripcion;
	private Sistema sistema;
	private String valorStr;
	private Float valorNum;
	private Object selObj;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "codigo", nullable = false, length = 10)
	@NotEmpty
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nombre", nullable = false, length = 50)
	@Length(max = 50)
	@NotEmpty
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "descripcion", nullable = true, length = 200)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sistema_id", nullable = false)
	@ForeignKey(name = "fk_sis_prm")
	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	@Column(name = "valor_str", nullable = true, length = 50)
	public String getValorStr() {
		return valorStr;
	}

	public void setValorStr(String valorStr) {
		this.valorStr = valorStr;
	}

	@Column(name = "valor_num", nullable = true)
	public Float getValorNum() {
		return valorNum;
	}

	public void setValorNum(Float valorNum) {
		this.valorNum = valorNum;
	}

	@Transient
	public Object getSelObj() {
		return selObj;
	}

	public void setSelObj(Object selObj) {
		this.selObj = selObj;
	}

}
