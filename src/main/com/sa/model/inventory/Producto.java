package com.sa.model.inventory;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Max;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotEmpty;

import com.sa.model.crm.Pais;
import com.sa.model.sales.CotizacionPrdSvcAdicionales;
import com.sa.model.security.Empresa;

@Entity
@Table(name = "producto", uniqueConstraints = @UniqueConstraint(columnNames = "referencia"))
public class Producto implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String referencia;
	private String nombre;
	private String descripcion;
	private Float costo;
	private Float prcNormal;
	private Float prcMinimo;
	private Float prcOferta;
	private Integer cantidadMinima;
	private Integer cantidadMaxima;
	private Marca marca;
	private String modelo;
	private Categoria categoria;
	private UnidadMedida unidadMedida;
	private Empresa empresa;
	private String tipo;
	private ClaseProducto claseProducto;
	private Proveedor proveedor;
	private Currency moneda;
	private Short percentImport;
	private Short percentTaxes;
	private Integer anio;
	private Integer totalPrds;
	private Integer totalGlobal;
	private Boolean activo;
	private byte[] image;
	private Pais origen;
	private Short tiempoEnvio;
	private List<Inventario> inventarios = new ArrayList<Inventario>();
	private List<CotizacionPrdSvcAdicionales> cotizPrd= new ArrayList<CotizacionPrdSvcAdicionales>();
	public Producto(){
		this.costo = 0f;
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
	
	@Column(name = "referencia", nullable = false, length=20, unique = true)
	public String getReferencia() {
		return referencia;
	}
	
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	@Column(name = "nombre", nullable = false, length=30)
	@NotEmpty
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "costo", nullable = false)
	@Min(value = 0)
	public Float getCosto() {
		return costo;
	}
	
	public void setCosto(Float costo) {
		this.costo = costo;
	}
	
	@Column(name = "cantidad_minima", nullable = false)
	@Min(value=0)
	public Integer getCantidadMinima() {
		return cantidadMinima;
	}
	
	public void setCantidadMinima(Integer cantidadMinima) {
		this.cantidadMinima = cantidadMinima;
	}
	
	@Column(name = "cantidad_maxima", nullable = true)
	@Min(value=1)
	public Integer getCantidadMaxima() {
		return cantidadMaxima;
	}
	
	public void setCantidadMaxima(Integer cantidadMaxima) {
		this.cantidadMaxima = cantidadMaxima;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "marca_id", nullable = true)
	@ForeignKey(name = "fk_producto_marca")
	public Marca getMarca() {
		return marca;
	}
	public void setMarca(Marca marca) {
		this.marca = marca;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = false)
	@ForeignKey(name = "fk_producto_categoria")
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidad_medida_id", nullable = true)
	@ForeignKey(name = "fk_producto_unidad_medida")
	public UnidadMedida getUnidadMedida() {
		return unidadMedida;
	}
	public void setUnidadMedida(UnidadMedida unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_producto_empresa")
	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "producto", cascade = CascadeType.REMOVE)
	public List<Inventario> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<Inventario> inventarios) {
		this.inventarios = inventarios;
	}

	@Column(name = "modelo", nullable = true, length=30)
	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	@Column(name = "percent_import", nullable = false)
	@Min(value=0)
	@Max(value=100)
	public Short getPercentImport() {
		return percentImport;
	}

	public void setPercentImport(Short percentImport) {
		this.percentImport = percentImport;
	}

	@Column(name = "percent_taxes", nullable = false)
	@Min(value=0)
	@Max(value=100)
	public Short getPercentTaxes() {
		return percentTaxes;
	}

	public void setPercentTaxes(Short percentTaxes) {
		this.percentTaxes = percentTaxes;
	}

	@Column(name = "tiempo_envio", nullable = false)
	@Min(value=1)
	public Short getTiempoEnvio() {
		return tiempoEnvio;
	}

	public void setTiempoEnvio(Short tiempoEnvio) {
		this.tiempoEnvio = tiempoEnvio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proveedor_id", nullable = false)
	@ForeignKey(name = "fk_prod_prov")
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moneda_id", nullable = false)
	@ForeignKey(name = "fk_prod_curr")
	public Currency getMoneda() {
		return moneda;
	}

	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}

	@Column(name = "prc_normal", nullable = false)
	public Float getPrcNormal() {
		return prcNormal;
	}

	public void setPrcNormal(Float prcNormal) {
		this.prcNormal = prcNormal;
	}

	@Column(name = "prc_minimo", nullable = false)
	public Float getPrcMinimo() {
		return prcMinimo;
	}

	public void setPrcMinimo(Float prcMinimo) {
		this.prcMinimo = prcMinimo;
	}

	@Column(name = "prc_oferta", nullable = false)
	public Float getPrcOferta() {
		return prcOferta;
	}

	public void setPrcOferta(Float prcOferta) {
		this.prcOferta = prcOferta;
	}

	@Column(name = "tipo", nullable = false, length=3)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clsprd_id", nullable = false)
	@ForeignKey(name = "fk_prod_clpr")
	public ClaseProducto getClaseProducto() {
		return claseProducto;
	}

	public void setClaseProducto(ClaseProducto claseProducto) {
		this.claseProducto = claseProducto;
	}

	@Column(name = "anio_precio", nullable = true, length=4)
	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}
	
	@Transient
	public Integer getTotalPrds() {
		return totalPrds;
	}

	public void setTotalPrds(Integer totalPrds) {
		this.totalPrds = totalPrds;
	}
	
	@Transient
	public Integer getTotalGlobal() {
		return totalGlobal;
	}

	public void setTotalGlobal(Integer totalGlobal) {
		this.totalGlobal = totalGlobal;
	}

	@Column(name = "descripcion", nullable = true, length=200)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name= "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name= "imagen", nullable = true)
	public byte[] getImage() {
		return image;
	}
	
	public void setImage(byte[] image) {
		this.image = image;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pais_id", nullable = true)
	@ForeignKey(name = "fk_producto_pais")
	public Pais getOrigen() {
		return origen;
	}

	public void setOrigen(Pais origen) {
		this.origen = origen;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "producto", cascade = CascadeType.REMOVE)
	public List<CotizacionPrdSvcAdicionales> getCotizPrd() {
		return cotizPrd;
	}

	public void setCotizPrd(List<CotizacionPrdSvcAdicionales> cotizPrd) {
		this.cotizPrd = cotizPrd;
	}

}
