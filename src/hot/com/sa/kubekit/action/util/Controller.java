package com.sa.kubekit.action.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;

@Name("controller")
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Synchronized
public class Controller implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<String> archivosTemp;
	
	
	public void init(){
		this.eliminarArchivosTemporales();
	}
	
	private void eliminarArchivosTemporales() {
		this.cargarArchivosTemporales();
		if (!archivosTemp.isEmpty()) {
			File f;
			for (String a : archivosTemp) {
				f = new File(a);
				if (f.exists()) {
					f.delete();
				}
			}
			archivosTemp.clear();
			this.guardarListaArchivosTemporales();
		}
	}

	@SuppressWarnings("unchecked")
	private void cargarArchivosTemporales() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream("archivosTemporales.bin");
			try {
				ObjectInputStream in = new ObjectInputStream(fis);
				this.archivosTemp = (ArrayList<String>) in.readObject();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException e) {
				this.archivosTemp = new ArrayList<String>();
			}
		} catch (FileNotFoundException ex) {
			try {
				File f = new File("archivosTemporales.bin");
				f.createNewFile();
				this.archivosTemp = new ArrayList<String>();
			} catch (IOException ex1) {
			}
		}

	}

	private void guardarListaArchivosTemporales() {
		try {
			FileOutputStream fos = new FileOutputStream(
					"archivosTemporales.bin");
			try {
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(this.archivosTemp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	public void agregarArchivoTemp(String archivoTemp) {
		this.archivosTemp.add(archivoTemp);
		this.guardarListaArchivosTemporales();
	}
	
	public void eliminarArchivoTemp(String archivoTemp) {
		this.archivosTemp.remove(archivoTemp);
		this.guardarListaArchivosTemporales();
	}

}
