package com.sa.model.sales;

import java.util.Date;

import com.sa.model.crm.Cliente;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Marca;
import com.sa.model.security.Usuario;

public class PojoVentasApa {
	
	private Date fechaVenta;
	private Cliente cliente;
	
	private Categoria categoriaD;
	private Marca marcaD;
	private String modeloD;
	private String numSerieD;
	private String bateriaD;
	private Double precioD;
	
	private Categoria categoriaI;
	private Marca marcaI;
	private String modeloI;
	private String numSerieI;
	private String bateriaI;
	private Double precioI;
	
	private Double precioTotal;
	private Usuario usEntrego;
	
	
	
	public Date getFechaVenta() {
		return fechaVenta;
	}
	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Categoria getCategoriaD() {
		return categoriaD;
	}
	public void setCategoriaD(Categoria categoriaD) {
		this.categoriaD = categoriaD;
	}
	public Marca getMarcaD() {
		return marcaD;
	}
	public void setMarcaD(Marca marcaD) {
		this.marcaD = marcaD;
	}
	public String getModeloD() {
		return modeloD;
	}
	public void setModeloD(String modeloD) {
		this.modeloD = modeloD;
	}
	public String getNumSerieD() {
		return numSerieD;
	}
	public void setNumSerieD(String numSerieD) {
		this.numSerieD = numSerieD;
	}
	public String getBateriaD() {
		return bateriaD;
	}
	public void setBateriaD(String bateriaD) {
		this.bateriaD = bateriaD;
	}
	public Double getPrecioD() {
		return precioD;
	}
	public void setPrecioD(Double precioD) {
		this.precioD = precioD;
	}
	public Categoria getCategoriaI() {
		return categoriaI;
	}
	public void setCategoriaI(Categoria categoriaI) {
		this.categoriaI = categoriaI;
	}
	public Marca getMarcaI() {
		return marcaI;
	}
	public void setMarcaI(Marca marcaI) {
		this.marcaI = marcaI;
	}
	public String getModeloI() {
		return modeloI;
	}
	public void setModeloI(String modeloI) {
		this.modeloI = modeloI;
	}
	public String getNumSerieI() {
		return numSerieI;
	}
	public void setNumSerieI(String numSerieI) {
		this.numSerieI = numSerieI;
	}
	public String getBateriaI() {
		return bateriaI;
	}
	public void setBateriaI(String bateriaI) {
		this.bateriaI = bateriaI;
	}
	public Double getPrecioI() {
		return precioI;
	}
	public void setPrecioI(Double precioI) {
		this.precioI = precioI;
	}
	
	public Double getPrecioTotal() {
		return precioTotal;
	}
	public void setPrecioTotal(Double precioTotal) {
		this.precioTotal = precioTotal;
	}
	public Usuario getUsEntrego() {
		return usEntrego;
	}
	public void setUsEntrego(Usuario usEntrego) {
		this.usEntrego = usEntrego;
	}
	
	

}
