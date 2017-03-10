package com.sa.model.acct;

import java.io.Serializable;
import java.util.Date;
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

import com.sa.model.crm.Cliente;
import com.sa.model.medical.ClienteCorporativo;
import com.sa.model.security.Sucursal;
import com.sa.model.security.Usuario;

@Entity
@Table(name = "asiento_contable")
public class AsientoContable implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private Sucursal sucursal;
	private Cliente cliente;
	private ClienteCorporativo cliCorp;
	private Double monto;
	private ConceptoMov concepto;
	private Date fechaAsiento;
	private Usuario usuario;
	private String comentario;
	private boolean soloLectura;
	private List<AsientoContDet> detalleAsiento;
	private List<AsientoContDet> detalleActivo;
	private List<AsientoContDet>detallePasivo;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cnm_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "codigo", nullable = false, length = 16)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_suc_asi")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", nullable = true)
	@ForeignKey(name = "fk_cli_asi")
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	@Column(name = "monto", nullable = false)
	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concepto_id", nullable = false)
	@ForeignKey(name = "fk_cnc_asi")
	public ConceptoMov getConcepto() {
		return concepto;
	}

	public void setConcepto(ConceptoMov concepto) {
		this.concepto = concepto;
	}

	@Column(name = "nombre", nullable = false)
	public Date getFechaAsiento() {
		return fechaAsiento;
	}

	public void setFechaAsiento(Date fechaAsiento) {
		this.fechaAsiento = fechaAsiento;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "asiento", cascade = CascadeType.REMOVE)
	public List<AsientoContDet> getDetalleAsiento() {
		return detalleAsiento;
	}

	public void setDetalleAsiento(List<AsientoContDet> detalleAsiento) {
		this.detalleAsiento = detalleAsiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	@ForeignKey(name = "fk_usu_asi")
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Column(name = "comentario", nullable = true, length = 300)
	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clicorp_id", nullable = true)
	@ForeignKey(name = "fk_clic_asc")
	public ClienteCorporativo getCliCorp() {
		return cliCorp;
	}

	public void setCliCorp(ClienteCorporativo cliCorp) {
		this.cliCorp = cliCorp;
	}

	@Transient
	public List<AsientoContDet> getDetalleActivo() {
		return detalleActivo;
	}

	public void setDetalleActivo(List<AsientoContDet> detalleActivo) {
		this.detalleActivo = detalleActivo;
	}

	@Transient
	public List<AsientoContDet> getDetallePasivo() {
		return detallePasivo;
	}

	public void setDetallePasivo(List<AsientoContDet> detallePasivo) {
		this.detallePasivo = detallePasivo;
	}

	@Column(name = "solo_lectura", nullable = true)
	public boolean isSoloLectura() {
		return soloLectura;
	}

	public void setSoloLectura(boolean soloLectura) {
		this.soloLectura = soloLectura;
	}
	
}