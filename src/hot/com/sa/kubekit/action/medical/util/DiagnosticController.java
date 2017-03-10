package com.sa.kubekit.action.medical.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.UtilString;
import com.sa.model.medical.NodeDiagnostic;
import com.sa.model.medical.ServiceClinicalHistory;

@Name("diagnosticController")
@Scope(ScopeType.CONVERSATION)
public class DiagnosticController {

	private ServiceClinicalHistory serviceClinicalHistory;
	private Integer mode;
	private NodeDiagnostic sourceSel;
	private NodeDiagnostic chapterSel;
	private NodeDiagnostic categorySel;
	private NodeDiagnostic subcategorySel;
	private List<NodeDiagnostic> sources = new ArrayList<NodeDiagnostic>();
	private List<NodeDiagnostic> chapters = new ArrayList<NodeDiagnostic>();
	private List<NodeDiagnostic> categories = new ArrayList<NodeDiagnostic>();
	private List<NodeDiagnostic> subcategories = new ArrayList<NodeDiagnostic>();
	private String searchField;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Create
	public void init() {
		sources = entityManager
				.createQuery(
						"select n from NodeDiagnostic n where n.fathChapter is null and fathCategory is null and fathSubCategory is null")
				.getResultList();
		if (sources.size() == 1) {
			sourceSel = sources.get(0);
			loadChapters();
		}
	}

	public void loadChapters() {
		if (sourceSel != null)
			chapters = new ArrayList<NodeDiagnostic>(sourceSel.getChapters());
		else
			chapters = new ArrayList<NodeDiagnostic>();
	}

	public void loadCategories() {
		if (chapterSel != null)
			categories = new ArrayList<NodeDiagnostic>(chapterSel
					.getCategories());
		else
			categories = new ArrayList<NodeDiagnostic>();
	}

	public void loadSubCategories() {
		if (categorySel != null)
			subcategories = new ArrayList<NodeDiagnostic>(categorySel
					.getSubCategories());
		else
			subcategories = new ArrayList<NodeDiagnostic>();
	}

	public void selectService(ServiceClinicalHistory service, Integer mode) {
		this.serviceClinicalHistory = service;
		this.mode = mode;
	}

	public void assignCode() {
		if (subcategorySel == null)
			return;
		switch (mode) {
		case 0:
			this.serviceClinicalHistory.setPrincipalDiagnostic(subcategorySel
					.getCode());
			break;
		case 1:
			this.serviceClinicalHistory.setRelatedDiagnostic1(subcategorySel
					.getCode());
			break;
		case 2:
			this.serviceClinicalHistory.setRelatedDiagnostic2(subcategorySel
					.getCode());
			break;
		case 3:
			this.serviceClinicalHistory.setRelatedDiagnostic3(subcategorySel
					.getCode());
			break;
		case 4:
			this.serviceClinicalHistory.setComplication(subcategorySel
					.getCode());
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void search() {
		if (searchField != null && !searchField.isEmpty()) {
			UtilString utilString = new UtilString();
			String jpql = "select n from NodeDiagnostic n where n.fathChapter is null and fathCategory is null and fathSubCategory is not null";
			jpql += " and " + utilString.addOrToQuery(searchField, "n", "name");
			System.out.println("****JPQL => " + jpql);
			subcategories = entityManager.createQuery(jpql).getResultList();
		}
	}

	/* get and set */
	public ServiceClinicalHistory getServiceClinicalHistory() {
		return serviceClinicalHistory;
	}

	public void setServiceClinicalHistory(
			ServiceClinicalHistory serviceClinicalHistory) {
		this.serviceClinicalHistory = serviceClinicalHistory;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public NodeDiagnostic getSourceSel() {
		return sourceSel;
	}

	public void setSourceSel(NodeDiagnostic sourceSel) {
		this.sourceSel = sourceSel;
	}

	public NodeDiagnostic getChapterSel() {
		return chapterSel;
	}

	public void setChapterSel(NodeDiagnostic chapterSel) {
		this.chapterSel = chapterSel;
	}

	public NodeDiagnostic getCategorySel() {
		return categorySel;
	}

	public void setCategorySel(NodeDiagnostic categorySel) {
		this.categorySel = categorySel;
	}

	public NodeDiagnostic getSubcategorySel() {
		return subcategorySel;
	}

	public void setSubcategorySel(NodeDiagnostic subcategorySel) {
		this.subcategorySel = subcategorySel;
	}

	public List<NodeDiagnostic> getSources() {
		return sources;
	}

	public void setSources(List<NodeDiagnostic> sources) {
		this.sources = sources;
	}

	public List<NodeDiagnostic> getChapters() {
		return chapters;
	}

	public void setChapters(List<NodeDiagnostic> chapters) {
		this.chapters = chapters;
	}

	public List<NodeDiagnostic> getCategories() {
		return categories;
	}

	public void setCategories(List<NodeDiagnostic> categories) {
		this.categories = categories;
	}

	public List<NodeDiagnostic> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<NodeDiagnostic> subcategories) {
		this.subcategories = subcategories;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

}