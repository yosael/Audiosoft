package com.sa.kubekit.action.util;

import java.util.Calendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("version")
@Scope(ScopeType.CONVERSATION)
public class Version {
	private int curYear; // Año actual para la línea de copyright
	/*
	1.2.3 (11BCF) <- Build number, should correspond with a revision in source control
	^ ^ ^
	| | |
	| | +--- Minor bugs, spelling mistakes, etc.
	| +----- Minor features, major bug fixes, etc.
	+------- Major version, UX changes, file format changes, etc."
	*/
	private String nomSistema = "Sistema de gestión de clínicas";
	private int majorVersion  = 3;   // Cambios en libreríaas, interfaz gráfica, cambios en formato de archivos etc. Cambios significativos que afectan compatibilidad.
	private int minorFeatures = 0;   // Nuevas funcionalidades, módulos, mejoras, errores grandes.
	private int minorBugs     = 5;   // Errores pequeños, cambios en etiquetas etc.

	
	private String version =  "Version: " + getMajorVersion() + "." + getMinorFeatures()+ "."+getMinorBugs(); /*+ getMinorFeatures() + "." + getMinorBugs()*/;
	
	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public int getMinorFeatures() {
		return minorFeatures;
	}

	public void setMinorFeatures(int minorFeatures) {
		this.minorFeatures = minorFeatures;
	}

	public int getMinorBugs() {
		return minorBugs;
	}

	public void setMinorBugs(int minorBugs) {
		this.minorBugs = minorBugs;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNomSistema() {
		return nomSistema;
	}

	public void setNomSistema(String nomSistema) {
		this.nomSistema = nomSistema;
	}

	public int getCurYear() {
		Calendar now = Calendar.getInstance(); 
		curYear = now.get(Calendar.YEAR);
		return curYear;
	}
}
