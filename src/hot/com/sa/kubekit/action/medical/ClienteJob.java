package com.sa.kubekit.action.medical;

public class ClienteJob {
	private String nombre;
	
	public ClienteJob(String nombre){
		this.setNombre(nombre);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
