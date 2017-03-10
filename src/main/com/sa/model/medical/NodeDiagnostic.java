package com.sa.model.medical;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

@Entity
@Table(name = "node_diagnostic")
public class NodeDiagnostic {

	private Integer id;
	private String code;
	private String name;
	private NodeDiagnostic fathChapter;
	private List<NodeDiagnostic> chapters = new ArrayList<NodeDiagnostic>();
	private NodeDiagnostic fathCategory;
	private List<NodeDiagnostic> categories = new ArrayList<NodeDiagnostic>();
	private NodeDiagnostic fathSubCategory;
	private List<NodeDiagnostic> subCategories = new ArrayList<NodeDiagnostic>();

	public NodeDiagnostic() {
	}

	public NodeDiagnostic(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "code", nullable = false, length = 10)
	@Length(max = 10)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name", nullable = false, length = 500)
	@Length(max = 500)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fath_chapter", nullable = true)
	@ForeignKey(name = "node_fath_chapter_fk")
	public NodeDiagnostic getFathChapter() {
		return fathChapter;
	}

	public void setFathChapter(NodeDiagnostic fathChapter) {
		this.fathChapter = fathChapter;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "fathChapter")
	public List<NodeDiagnostic> getChapters() {
		return chapters;
	}

	public void setChapters(List<NodeDiagnostic> chapters) {
		this.chapters = chapters;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fath_category", nullable = true)
	@ForeignKey(name = "node_fath_category_fk")
	public NodeDiagnostic getFathCategory() {
		return fathCategory;
	}

	public void setFathCategory(NodeDiagnostic fathCategory) {
		this.fathCategory = fathCategory;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "fathCategory")
	public List<NodeDiagnostic> getCategories() {
		return categories;
	}

	public void setCategories(List<NodeDiagnostic> categories) {
		this.categories = categories;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fath_subcategory", nullable = true)
	@ForeignKey(name = "node_fath_subcategory_fk")
	public NodeDiagnostic getFathSubCategory() {
		return fathSubCategory;
	}

	public void setFathSubCategory(NodeDiagnostic fathSubCategory) {
		this.fathSubCategory = fathSubCategory;
	}

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "fathSubCategory")
	public List<NodeDiagnostic> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<NodeDiagnostic> subCategories) {
		this.subCategories = subCategories;
	}

}