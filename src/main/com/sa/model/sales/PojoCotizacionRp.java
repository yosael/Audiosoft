package com.sa.model.sales;

public class PojoCotizacionRp {
	
	private String nombre;
	private Long total;
	private Long totalCotizado;
	private Long totalVendido;
	private Long totalCxc;
	private String sucursal;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public Long getTotalCotizado() {
		return totalCotizado;
	}
	public void setTotalCotizado(Long totalCotizado) {
		this.totalCotizado = totalCotizado;
	}
	public Long getTotalVendido() {
		return totalVendido;
	}
	public void setTotalVendido(Long totalVendido) {
		this.totalVendido = totalVendido;
	}
	public Long getTotalCxc() {
		return totalCxc;
	}
	public void setTotalCxc(Long totalCxc) {
		this.totalCxc = totalCxc;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	
	
	
	

}
