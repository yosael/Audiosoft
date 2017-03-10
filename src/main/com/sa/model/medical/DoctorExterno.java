 package com.sa.model.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.sa.model.crm.Cliente;

/**
 * @author kubekaspa
 * @version 1.0
 * @created 18-ene-2011 8:12:40
 */
@Entity
@Table(name = "doctor_externo")
public class DoctorExterno implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String nombres;
	private String apellidos;
	private String titulo;
	private String email; 
	private String telefono1;
	private String extension1;
	private String institucion;
	private String cargo;
	private String comentario;
	List<Cliente> clientesReferidos = new ArrayList<Cliente>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "email", nullable = true, length = 80)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "telefono1", nullable = true, length = 20)
	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	@Column(name = "nombres", nullable = false, length = 50)
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Column(name = "apellidos", nullable = false, length = 50)
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Column(name = "comentario", nullable = true, length = 200)
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Column(name = "titulo", nullable = true, length = 30)
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "extension1", nullable = true, length = 5)
	public String getExtension1() {
		return extension1;
	}

	public void setExtension1(String extension1) {
		this.extension1 = extension1;
	}

	@Column(name = "institucion", nullable = true, length = 60)
	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	@Column(name = "cargo", nullable = true, length = 60)
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "doctorRef", cascade = CascadeType.REFRESH)
	public List<Cliente> getClientesReferidos() {
		return clientesReferidos;
	}

	public void setClientesReferidos(List<Cliente> clientesReferidos) {
		this.clientesReferidos = clientesReferidos;
	}
	
	@Transient
	public String getNombreCompleto() {
		String res = "";
		if(nombres != null)
			res.concat(nombres);
		if(apellidos != null)
			res.concat(apellidos);
		
			return res;
	}
	
}