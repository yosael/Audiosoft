package com.sa.model.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.sa.model.sales.CotizacionPrdSvcAdicionales;
import com.sa.model.security.Sucursal;

@Entity
@Table(name = "inventario")
public class Inventario implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer cantidadActual;
	private Sucursal sucursal;
	private Producto producto;
	private UbicacionPrd codUbicacion;
	private Set<Item> items = new HashSet<Item>();
	private Set<ItemPedido> itemsPedidos = new HashSet<ItemPedido>();
	private Set<ItemPrescription> itemsPrescriptions = new HashSet<ItemPrescription>();
	private Set<CodProducto> codigosProds = new HashSet<CodProducto>();
	
	public Inventario(){
		this.cantidadActual = 0;
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
	
	@Column(name = "cantidad_actual", nullable = false)
	public Integer getCantidadActual() {
		return cantidadActual;
	}
	
	public void setCantidadActual(Integer cantidadActual) {
		this.cantidadActual = cantidadActual;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_inventario_sucursal")
	public Sucursal getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = false)
	@ForeignKey(name = "fk_inventario_producto")
	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inventario", cascade = CascadeType.REMOVE)
	public Set<Item> getItems() {
		return items;
	}
	public void setItems(Set<Item> items) {
		this.items = items;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inventario", cascade = CascadeType.REMOVE)
	public Set<ItemPedido> getItemsPedidos() {
		return itemsPedidos;
	}

	public void setItemsPedidos(Set<ItemPedido> itemsPedidos) {
		this.itemsPedidos = itemsPedidos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inventario", cascade = CascadeType.REMOVE)
	public Set<ItemPrescription> getItemsPrescriptions() {
		return itemsPrescriptions;
	}

	public void setItemsPrescriptions(Set<ItemPrescription> itemsPrescriptions) {
		this.itemsPrescriptions = itemsPrescriptions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inventario", cascade = CascadeType.REMOVE)
	public Set<CodProducto> getCodigosProds() {
		return codigosProds;
	}

	public void setCodigosProds(Set<CodProducto> codigosProds) {
		this.codigosProds = codigosProds;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ubicacion_id", nullable = true)
	@ForeignKey(name = "fk_inv_ubprd")
	public UbicacionPrd getCodUbicacion() {
		return codUbicacion;
	}

	public void setCodUbicacion(UbicacionPrd codUbicacion) {
		this.codUbicacion = codUbicacion;
	}
	
}
