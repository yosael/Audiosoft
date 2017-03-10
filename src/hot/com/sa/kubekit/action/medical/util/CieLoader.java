package com.sa.kubekit.action.medical.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.model.medical.NodeDiagnostic;

//Esta clase se encarga de cargar los codigos CIE-10
@Name("cieLoader")
@Scope(ScopeType.CONVERSATION)
public class CieLoader {

	@In
	private EntityManager entityManager;

	private String rootDir = "C:/Program Files (x86)/Guia de Codigos de Diagnostico/cie/";
	private String separator = System.getProperty("file.separator");
	private NodeDiagnostic source;

	public void loader() {
		// cargamos la fuente
		source = entityManager.find(NodeDiagnostic.class, 0);
		saveChapters(source);
	}

	private void saveChapters(NodeDiagnostic source) {
		File f = new File(rootDir + "capitulos.txt");
		try {
			TreeMap<String, String> map = obtainMap(f);
			Set<Entry<String, String>> entries = map.entrySet();
			List<NodeDiagnostic> chapters = new ArrayList<NodeDiagnostic>();
			for (Entry<String, String> entry : entries) {
				NodeDiagnostic newNode = new NodeDiagnostic(entry.getKey(),
						entry.getValue().toUpperCase());
				newNode.setFathChapter(source);
				chapters.add(newNode);
				entityManager.persist(newNode);
			}
			entityManager.flush();
			int i = 1;
			for (NodeDiagnostic chapter : chapters) {
				saveCategories(chapter, i);
				i++;
			}
		} catch (IOException e) {
			FacesMessages.instance().add("***ERROR");
			e.printStackTrace();
		}
	}

	private void saveCategories(NodeDiagnostic chapter, int num) {
		File f = new File(rootDir + "cap-" + num + separator
				+ "principales.txt");
		try {
			TreeMap<String, String> map = obtainMap(f);
			Set<Entry<String, String>> entries = map.entrySet();
			List<NodeDiagnostic> categories = new ArrayList<NodeDiagnostic>();
			for (Entry<String, String> entry : entries) {
				NodeDiagnostic newNode = new NodeDiagnostic(entry.getKey(),
						entry.getValue().toUpperCase());
				newNode.setFathCategory(chapter);
				entityManager.persist(newNode);
				categories.add(newNode);

			}
			entityManager.flush();
			for (NodeDiagnostic category : categories) {
				saveSubcategories(category);
			}
		} catch (IOException e) {
			FacesMessages.instance().add("***ERROR");
			e.printStackTrace();
		}
	}

	private void saveSubcategories(NodeDiagnostic category) {
		Character letter = ((category.getCode().toCharArray())[0]);
		File f = new File(rootDir + Character.toLowerCase(letter) + separator
				+ category.getCode().toLowerCase() + ".txt");
		try {
			TreeMap<String, String> map = obtainMap(f);
			Set<Entry<String, String>> entries = map.entrySet();
			for (Entry<String, String> entry : entries) {
				NodeDiagnostic newNode = new NodeDiagnostic(entry.getKey(),
						entry.getValue().toUpperCase());
				newNode.setFathSubCategory(category);
				entityManager.persist(newNode);

			}
			entityManager.flush();
		} catch (IOException e) {
			FacesMessages.instance().add("***ERROR");
			e.printStackTrace();
		}
	}

	private TreeMap<String, String> obtainMap(File file) throws IOException {
		System.out.print("***File = " + file.getAbsolutePath());
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String cad = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		while ((cad = bf.readLine()) != null) {
			String[] line = cad.split("-");
			map.put(line[0].trim(), line[1].trim());
			System.out.print("***Valor en mapa = " + line[0].trim() + " - "
					+ line[1].trim());
		}
		return map;
	}

}