package com.sa.model.sales;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Producto;

@Entity
@Table(name = "item_combo_apa")
public class ItemComboApa implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private ComboAparato combo;
	private Categoria categoria;
	private Producto producto;
	private Short cantidad;
	private String descripcion;
	private boolean principal;
	private CodProducto codProducto;
	private Inventario inventario;
	private String tipoPrecio = "NRM";
	//private List<CotCmbsItems> itemsCotizados = new ArrayList<CotCmbsItems>();
	private float precioCotizado= 0f;
	private String tipPreCotizado;
	
	private int idRd;
	
	//nuevo el 29/06/2017
	private boolean generaRequisicion;
	
	private String selCategoria;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "itcbap_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "combo_id", nullable = true)
	@ForeignKey(name = "fk_cmb_itmcmb")
	public ComboAparato getCombo() {
		return combo;
	}
	public void setCombo(ComboAparato combo) {
		this.combo = combo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = true)
	@ForeignKey(name = "fk_cat_cmbap")
	public Categoria getCategoria() {
		return categoria;
	}
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = true)
	@ForeignKey(name = "fk_prd_cmbap")
	public Producto getProducto() {
		return producto;
	}
	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@Column(name = "cantidad", nullable = false)
	@Min(1)
	public Short getCantidad() {
		return cantidad;
	}
	public void setCantidad(Short cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "descripcion", nullable = true, length = 100)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "principal", nullable = false)
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
	
	@Transient
	public CodProducto getCodProducto() {
		return codProducto;
	}
	public void setCodProducto(CodProducto codProducto) {
		this.codProducto = codProducto;
	}
	
	@Transient
	public Inventario getInventario() {
		return inventario;
	}
	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}
	
	@Transient
	public String getTipoPrecio() {
		return tipoPrecio;
	}
	public void setTipoPrecio(String tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}
	
/*	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item", cascade = CascadeType.REMOVE)//
	public List<CotCmbsItems> getItemsCotizados() {
		return itemsCotizados;
	}
	public void setItemsCotizados(List<CotCmbsItems> itemsCotizados) {
		this.itemsCotizados = itemsCotizados;
	}*/
	
	@Transient
	public float getPrecioCotizado() {
		return precioCotizado;
	}
	public void setPrecioCotizado(float precioCotizado) {
		this.precioCotizado = precioCotizado;
	}
	
	@Transient
	public String getTipPreCotizado() {
		return tipPreCotizado;
	}
	public void setTipPreCotizado(String tipPreCotizado) {
		this.tipPreCotizado = tipPreCotizado;
	}
	
	@Transient
	public int getIdRd() {
		return idRd;
	}
	public void setIdRd(int idRd) {
		this.idRd = idRd;
	}
	
	@Column(name = "genera_requisicion", nullable = false)
	public boolean isGeneraRequisicion() {
		return generaRequisicion;
	}
	public void setGeneraRequisicion(boolean generaRequisicion) {
		this.generaRequisicion = generaRequisicion;
	}
	
	@Column(name = "sel_categoria", nullable = true)
	public String getSelCategoria() {
		return selCategoria;
	}
	public void setSelCategoria(String selCategoria) {
		this.selCategoria = selCategoria;
	}
	
	
	
	
	
	
	
	
	
		
}
