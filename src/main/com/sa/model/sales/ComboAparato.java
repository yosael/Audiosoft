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
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import com.sa.model.security.Empresa;

@Entity
@Table(name = "combo_aparato", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class ComboAparato implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String codigo;
	private String nombre;
	private String descripcion;
	private Empresa empresa;
	private String estado;
	private Integer periodoGarantia;
	private float total;
	private List<ItemComboApa> itemsCombo = new ArrayList<ItemComboApa>();
	private List<CostoServicio> costosCombo = new ArrayList<CostoServicio>();
	private List<CotizacionCombos> cmbCotizados = new ArrayList<CotizacionCombos>(); 
	
	private List<ComboAparatoAdaptacion> adaptaciones = new ArrayList<ComboAparatoAdaptacion>();
	
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cmbapa_id", nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "nombre", nullable = false, length=40)
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "descripcion", nullable = true, length=150)
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@ForeignKey(name = "fk_emp_cmbap")
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	@Column(name = "estado", nullable = true, length=3)
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "combo", cascade = CascadeType.REMOVE)
	public List<ItemComboApa> getItemsCombo() {
		return itemsCombo;
	}
	public void setItemsCombo(List<ItemComboApa> itemsCombo) {
		this.itemsCombo = itemsCombo;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "combo", cascade = CascadeType.REMOVE)
	public List<CostoServicio> getCostosCombo() {
		return costosCombo;
	}
	public void setCostosCombo(List<CostoServicio> costosCombo) {
		this.costosCombo = costosCombo;
	}
	
	@Column(name = "codigo", nullable = false, length=10)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "periodo_garantia", nullable = true)
	public Integer getPeriodoGarantia() {
		return periodoGarantia;
	}
	public void setPeriodoGarantia(Integer periodoGarantia) {
		this.periodoGarantia = periodoGarantia;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "combo", cascade = CascadeType.REMOVE)
	public List<CotizacionCombos> getCmbCotizados() {
		return cmbCotizados;
	}
	public void setCmbCotizados(List<CotizacionCombos> cmbCotizados) {
		this.cmbCotizados = cmbCotizados;
	}
	
	@Transient
	public float getTotal(){//NOTA: SE AGREGO LA MULTIMPLICACION POR LA CANTIDAD. 07/07/2017
		// obtener los costos de los diferentes items del combo
		try{
			if (total == 0){
				float subtotal = 0.0f;	
				for (ItemComboApa tmpItem: this.getItemsCombo()){
					if (tmpItem != null && tmpItem.getPrecioCotizado() > 0)
						subtotal += (tmpItem.getPrecioCotizado());
						else if (tmpItem != null){
						if (tmpItem.getTipoPrecio().equals("NRM"))
							subtotal += (tmpItem.getProducto().getPrcNormal()*tmpItem.getCantidad());
						else if (tmpItem.getTipoPrecio().equals("MIN"))
							subtotal += (tmpItem.getProducto().getPrcMinimo()*tmpItem.getCantidad());
						else if (tmpItem.getTipoPrecio().equals("OFE"))
							subtotal += (tmpItem.getProducto().getPrcOferta()*tmpItem.getCantidad()); 
					}
				}
				total += subtotal;
				//obtener los costos de los diferentes servicios del combo
				for (CostoServicio tmpCst : this.getCostosCombo()) {
					total += tmpCst.getServicio().getCosto().floatValue();
				}
			}
		} catch (Exception e){
			total = 0.0f;
		}
		return total;
	}
	
	public void setTotal(float total){
		this.total = total;
	}
	
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "comboAparato")
	public List<ComboAparatoAdaptacion> getAdaptaciones() {
		return adaptaciones;
	}
	public void setAdaptaciones(List<ComboAparatoAdaptacion> adaptaciones) {
		this.adaptaciones = adaptaciones;
	}
	
	
	
	
	
	
	
	
	
}
