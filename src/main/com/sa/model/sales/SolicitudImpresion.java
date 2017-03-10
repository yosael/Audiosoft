package com.sa.model.sales;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.crm.ChequeDoc;
import com.sa.model.security.Usuario;
import com.sa.model.vta.VentaDoc;

@Entity
@Table(name = "solicitud_impresion")
public class SolicitudImpresion implements Serializable{
	 
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Usuario usuario;
	private VentaProdServ venta;
	private VentaDoc ventaDoc;
	private Date fecha;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sol_print_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = true)
	@ForeignKey(name = "fk_solprint_usr")
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venta_id",referencedColumnName = "vtaprsv_id" ,nullable = true)
	@ForeignKey(name = "fk_solprint_vta")
	public VentaProdServ getVenta() {
		return venta;
	}
	public void setVenta(VentaProdServ venta) {
		this.venta = venta;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = true)
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ventadoc_id",referencedColumnName = "vtadoc_id" ,nullable = true)
	@ForeignKey(name = "fk_solprint_vtadoc")
	public VentaDoc getVentaDoc() {
		return ventaDoc;
	}
	public void setVentaDoc(VentaDoc ventaDoc) {
		this.ventaDoc = ventaDoc;
	}
	
	
	
	
}