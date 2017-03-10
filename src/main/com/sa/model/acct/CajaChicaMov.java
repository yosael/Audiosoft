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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.security.Sucursal;

@Entity
@Table(name = "caja_chica_mov")
public class CajaChicaMov implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date fecha;
	private Double monto;
	private Double ingresos;
	private Double gastos;
	private String estado;
	private Sucursal sucursal;
	private List<CajaChicaDet> detalleMovimientos;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cjch_id", nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = false)
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "monto", nullable = false)
	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "movPrincipal", cascade = CascadeType.REMOVE)
	@OrderBy("id ASC")
	public List<CajaChicaDet> getDetalleMovimientos() {
		return detalleMovimientos;
	}

	public void setDetalleMovimientos(List<CajaChicaDet> detalleMovimientos) {
		this.detalleMovimientos = detalleMovimientos;
	}

	//ABI = Abierto, CER = Cerrado
	@Column(name = "estado", nullable = false, length = 3)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sucursal_id", nullable = false)
	@ForeignKey(name = "fk_suc_cjc")
	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	@Column(name = "ingresos", nullable = true)
	public Double getIngresos() {
		if(ingresos == null)
			return 0d;
		return ingresos;
	}

	public void setIngresos(Double ingresos) {
		this.ingresos = ingresos;
	}

	@Column(name = "gastos", nullable = true)
	public Double getGastos() {
		if(gastos == null)
			return 0d;
		return gastos;
	}

	public void setGastos(Double gastos) {
		this.gastos = gastos;
	}
	
	

}