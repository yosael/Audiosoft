package com.sa.model.medical;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="contador_recomendacion_audio")
public class ContadorRecomendacionAudio implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer conteo;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Column(name="conteo",nullable=false)
	public Integer getConteo() {
		return conteo;
	}
	public void setConteo(Integer conteo) {
		this.conteo = conteo;
	}
	
	
	

}
