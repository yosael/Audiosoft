package com.sa.model.workshop;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "requisicion_etapa_rep")
public class RequisicionEtapaRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private EtapaRepCliente etapaRepCli;
	private Date fechaIngreso;
	private Date fechaAprobacion;
	private String descripcion;
	private Usuario usrAprueba;
	private Sucursal sucursalSol;
	private String estado;
	private Set<ItemRequisicionEta> itemsRequisicion = new HashSet<ItemRequisicionEta>();
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reqeta_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "etarepcli_id", nullable = false)
	@ForeignKey(name = "fk_etarep_req")
	public EtapaRepCliente getEtapaRepCli() {
		return etapaRepCli;
	}
	public void setEtapaRepCli(EtapaRepCliente etapaRepCli) {
		this.etapaRepCli = etapaRepCli;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ingreso", nullable = false)
	public Date getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_aprobacion", nullable = true)
	public Date getFechaAprobacion() {
		return fechaAprobacion;
	}
	
	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}
	
	@Column(name = "descripcion", nullable = true, length = 150)
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
		
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reqEtapa", cascade = CascadeType.REMOVE)
	public Set<ItemRequisicionEta> getItemsRequisicion() {
		return itemsRequisicion;
	}
	
	public void setItemsRequisicion(Set<ItemRequisicionEta> itemsRequisicion) {
		this.itemsRequisicion = itemsRequisicion;
	}
	
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucreq_id", nullable = false)
	@ForeignKey(name = "fk_req_suc")
	public Sucursal getSucursalSol() {
		return sucursalSol;
	}
	public void setSucursalSol(Sucursal sucursalSol) {
		this.sucursalSol = sucursalSol;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usr_aprueba_id", nullable = true)
	@ForeignKey(name = "fk_reqrep_usu")
	public Usuario getUsrAprueba() {
		return usrAprueba;
	}
	public void setUsrAprueba(Usuario usrAprueba) {
		this.usrAprueba = usrAprueba;
	}
	
	
	
	
	
	
	

}
