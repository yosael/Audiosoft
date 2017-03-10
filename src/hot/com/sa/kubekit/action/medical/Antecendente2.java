package com.sa.kubekit.action.medical;

import org.jboss.seam.annotations.Name;

@Name("antecendente")
public class Antecendente2 {
	private String nombre;
	private String descripcion;
	
	public Antecendente2(String nombre, String descripcion){
		this.nombre=nombre;
		this.descripcion=descripcion;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
