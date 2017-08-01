package com.sa.model.inventory;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Min;

import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.AparatoCliente;

@Entity
@Table(name = "item")
public class Item implements Serializable, Cloneable{

	private static final long serialVersionUID = 1L;
	private ItemId itemId;
	private Integer cantidad;
	private Float costoUnitario;
	private Inventario inventario;
	private Movimiento movimiento;
	private CodProducto codProducto;
	private List<CodProducto> codsProducto;
	private String tipoPrecio = "NRM";
	private boolean principal;
	private float precioCotizado= 0f;
	private String tipPreCotizado;
	private Sucursal sucursalOri;
	private Sucursal sucursalDest;
	private String codsSerie;
	private Integer cotItmId;
	private AparatoCliente aparato;
	
	
	private Float precioVenta;
	
	private Boolean registrado;
	private Boolean modoEdicion;
	
	@EmbeddedId
	public ItemId getItemId() {
		return itemId;
	}
	
	public Item clone(){
		Item clone = null;
		try{
			clone = (Item) super.clone();
			
		}catch (CloneNotSupportedException e) {
			
		}
		return clone;
	}
	
	public void setItemId(ItemId itemId) {
		this.itemId = itemId;
	}
	
	@Column(name = "cantidad", nullable = false)
	@Min(value=1)
	public Integer getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	@Column(name = "costo_unitario", nullable = true)
	@Min(value=0)
	public Float getCostoUnitario() {
		return costoUnitario;
	}
	
	public void setCostoUnitario(Float costoUnitario) {
		this.costoUnitario = costoUnitario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "movimiento_id", nullable = true, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_movimiento")
	public Movimiento getMovimiento() {
		return movimiento;
	}
	
	public void setMovimiento(Movimiento movimiento) {
		this.movimiento = movimiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventario_id", nullable = false, insertable = false, updatable = false)
	@ForeignKey(name = "fk_item_inventario")
	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	@Transient
	public CodProducto getCodProducto() {
		return codProducto;
	}
	public void setCodProducto(CodProducto codProducto) {
		this.codProducto = codProducto;
	}

	@Transient
	public String getTipoPrecio() {
		return tipoPrecio;
	}

	public void setTipoPrecio(String tipoPrecio) {
		this.tipoPrecio = tipoPrecio;
	}

	@Transient
	public List<CodProducto> getCodsProducto() {
		return codsProducto;
	}

	public void setCodsProducto(List<CodProducto> codsProducto) {
		this.codsProducto = codsProducto;
	}

	@Transient
	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	@Transient
	public Sucursal getSucursalOri() {
		return sucursalOri;
	}

	public void setSucursalOri(Sucursal sucursalOri) {
		this.sucursalOri = sucursalOri;
	}

	@Transient
	public Sucursal getSucursalDest() {
		return sucursalDest;
	}

	public void setSucursalDest(Sucursal sucursalDest) {
		this.sucursalDest = sucursalDest;
	}

	@Column(name = "cods_serie", nullable = true, length = 4000)
	public String getCodsSerie() {
		if(codsSerie == null)
			return "";
		return codsSerie;
	}

	public void setCodsSerie(String codsSerie) {
		this.codsSerie = codsSerie;
	}
	
	@Transient
	public String getTipPreCotizado() {
		return tipPreCotizado;
	}

	public void setTipPreCotizado(String tipPreCotizado) {
		this.tipPreCotizado = tipPreCotizado;
	}
	
	@Transient
	public float getPrecioCotizado() {
		return precioCotizado;
	}

	public void setPrecioCotizado(float precioCotizado) {
		this.precioCotizado = precioCotizado;
	}

	@Transient
	public Integer getCotItmId() {
		return cotItmId;
	}

	public void setCotItmId(Integer cotItmId) {
		this.cotItmId = cotItmId;
	}

	@Transient
	public AparatoCliente getAparato() {
		return aparato;
	}

	public void setAparato(AparatoCliente aparato) {
		this.aparato = aparato;
	}

	
	@Column(name="precio_venta",nullable=true)
	public Float getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(Float precioVenta) {
		this.precioVenta = precioVenta;
	}

	@Column(name="registrado",nullable=true)
	public Boolean getRegistrado() {
		return registrado;
	}

	public void setRegistrado(Boolean registrado) {
		this.registrado = registrado;
	}

	@Transient
	public Boolean getModoEdicion() {
		return modoEdicion;
	}

	public void setModoEdicion(Boolean modoEdicion) {
		this.modoEdicion = modoEdicion;
	}

	
	
	
	
	
}
