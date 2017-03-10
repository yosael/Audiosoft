package com.sa.model.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

import com.sa.model.medical.Prescription;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "movimiento")
@Inheritance(strategy = InheritanceType.JOINED)
public class Movimiento implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String observacion;
	private String tipoMovimiento; // E=Entrada; S=Salida; 
	private String razon; // T=Tranferencia; V = Venta; C=Compra; P=Pedido; R=Receta m�dica
	private Date fecha;
	private Sucursal sucursal;
	private List<Item> items = new  ArrayList<Item>();
	private Usuario usuario;
	private Transferencia transferenciaEntrada;
	private Prescription prescription;
	private String desde;
	private String hacia;
	

	public Movimiento(){
		this.fecha = new Date();
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

	@Column(name = "observacion", nullable = true, length = 455)
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "tipo_movimiento", nullable = false, length = 1)
	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}
	
	//@Column(name = "razon", nullable = false, length = 455)
	@Column(name = "razon", nullable = false, length = 5)
	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", nullable = false)
	@NotNull
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_movimiento_sucursal")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "movimiento", cascade = CascadeType.REMOVE)
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = true)
	@ForeignKey(name = "fk_movimiento_usuario")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "entrada", cascade = CascadeType.REMOVE)
	public Transferencia getTransferenciaEntrada() {
		return transferenciaEntrada;
	}

	public void setTransferenciaEntrada(Transferencia transferenciaEntrada) {
		this.transferenciaEntrada = transferenciaEntrada;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "movimiento", cascade = CascadeType.REMOVE)
	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	
	@Column(name="desde",nullable=true,length=30)
	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}

	@Column(name="hacia",nullable=true,length=30)
	public String getHacia() {
		return hacia;
	}

	public void setHacia(String hacia) {
		this.hacia = hacia;
	}
	
	
	

}
