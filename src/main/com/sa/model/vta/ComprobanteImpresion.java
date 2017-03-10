package com.sa.model.vta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity 
@Table(name = "comprobante_impresion")
public class ComprobanteImpresion {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 	
	private String nombre;
	private String impresor;
	private String tipo;
	private String pagina;
	private EmpresaDoc empresaDoc;
	private String codigo;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "nombre", nullable = false, length = 40)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "impresor", nullable = false, length = 100)
	public String getImpresor() {
		return impresor;
	}

	public void setImpresor(String impresor) {
		this.impresor = impresor;
	}

	@Column(name = "pagina", nullable = false, length = 50)
	public String getPagina() {
		return pagina;
	}

	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empdoc_id", nullable = true)
	@ForeignKey(name = "fk_compimp_empdoc")
	public EmpresaDoc getEmpresaDoc() {
		return empresaDoc;
	}

	public void setEmpresaDoc(EmpresaDoc empresaDoc) {
		this.empresaDoc = empresaDoc;
	}

	@Column(name = "codigo", nullable = false, length = 20)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	//FAC = Factura, CCF = Credito Fiscal, OTR = otro
	@Column(name = "tipo", nullable = false, length = 3)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
