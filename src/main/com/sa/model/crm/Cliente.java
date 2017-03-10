package com.sa.model.crm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import com.sa.model.acct.CuentaCobrar;
import com.sa.model.medical.ClinicalHistory;
import com.sa.model.medical.DoctorExterno;
import com.sa.model.medical.GeneralInformation;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Usuario;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.ReparacionCliente;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String tipoDoc;
	private String docId;
	private String nombres;
	private String apellidos;
	private String nombresEncargado;
	private String apellidosEncargado;
	private Cliente referidoPor;
	private DoctorExterno doctorRef;
	private Date fechaCreacion;
	private String telefono1;
	private String telefono2;
	private String direccion;
	private Date fechaNacimiento; 
	private Integer genero = 1;
	private Integer grupoSanguineo;
	private String email;
	private Date diagnosSordera;
	private GeneralInformation generalInformation;
	private List<ClinicalHistory> historiasClinicas = new ArrayList<ClinicalHistory>(0);
	private List<MedicalAppointment> citasMedicas = new ArrayList<MedicalAppointment>(0);
	private List<AparatoCliente> aparatosAuditivos = new ArrayList<AparatoCliente>();
	private List<ReparacionCliente> reparacionesTaller = new ArrayList<ReparacionCliente>();
	private List<VentaProdServ> ventas = new ArrayList<VentaProdServ>();
	private List<CuentaCobrar> cuentasPagar = new ArrayList<CuentaCobrar>();
	private boolean selected;
	private String medioReferido;
	private MedioDifusion mdif;
	private Pais pais;
	private Departamento depto;
	private Municipio municipio;
	private String ocupacion;
	private Integer usuarioRegistro;
	
	private Set<ReparacionCliente> reparaciones =  new HashSet<ReparacionCliente>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente", cascade = CascadeType.REMOVE)
	public Set<ReparacionCliente> getReparaciones() {
		return reparaciones;
	}

	public void setReparaciones(Set<ReparacionCliente> reparaciones) {
		this.reparaciones = reparaciones;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cliente_id", nullable = false)
	public Integer getId() {
		return id;		
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "tipo_doc", nullable = false, length = 50)
	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	
	@Column(name = "doc_id", nullable = true, length = 50)
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	@Column(name = "nombres", nullable = false, length = 50)
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Column(name = "apellidos", nullable = false, length = 50)
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Column(name = "telefono1", nullable = false, length = 30)
	public String getTelefono1() {
		return telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}

	@Column(name = "telefono2", nullable = true, length = 30)
	public String getTelefono2() {
		return telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}
	
	@Column(name = "email", nullable = true, length = 50)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "direccion", nullable = true, length = 100)
	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "fecha_nacimiento", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	@Column(name = "genero", nullable = false)
	public Integer getGenero() {
		return genero;
	}

	public void setGenero(Integer genero) {
		this.genero = genero;
	}

	@Column(name = "grupo_" +
			"nguineo", nullable = true)
	public Integer getGrupoSanguineo() {
		return grupoSanguineo;
	}

	public void setGrupoSanguineo(Integer grupoSanguineo) {
		this.grupoSanguineo = grupoSanguineo;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "cliente", cascade = CascadeType.REMOVE)
	public GeneralInformation getGeneralInformation() {
		return generalInformation;
	}

	public void setGeneralInformation(GeneralInformation generalInformation) {
		this.generalInformation = generalInformation;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente")
	@OrderBy("creationDate DESC")
	public List<ClinicalHistory> getHistoriasClinicas() {
		return historiasClinicas;
	}

	public void setHistoriasClinicas(List<ClinicalHistory> historiasClinicas) {
		this.historiasClinicas = historiasClinicas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente")
	@OrderBy("dateTime DESC")
	public List<MedicalAppointment> getCitasMedicas() {
		return citasMedicas;
	}

	public void setCitasMedicas(List<MedicalAppointment> citasMedicas) {
		this.citasMedicas = citasMedicas;
	}
	
	@Transient
	public String getFullName(){
		return getNombres()+" " +getApellidos();
	}

	@Transient
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Column(name = "nombres_enc", nullable = true, length = 50)
	public String getNombresEncargado() {
		return nombresEncargado;
	}

	public void setNombresEncargado(String nombresEncargado) {
		this.nombresEncargado = nombresEncargado;
	}

	@Column(name = "apellidos_enc", nullable = true, length = 50)
	public String getApellidosEncargado() {
		return apellidosEncargado;
	}

	public void setApellidosEncargado(String apellidosEncargado) {
		this.apellidosEncargado = apellidosEncargado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clienteref_id", nullable = true)
	@ForeignKey(name = "fk_cli_refcl")
	public Cliente getReferidoPor() {
		return referidoPor;
	}

	public void setReferidoPor(Cliente referidoPor) {
		this.referidoPor = referidoPor;
	}

	@Column(name = "fecha_creacion", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column(name = "medio_referido", nullable = true, length = 20)
	public String getMedioReferido() {
		return medioReferido;
	}

	public void setMedioReferido(String medioReferido) {
		this.medioReferido = medioReferido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "docref_id", nullable = true)
	@ForeignKey(name = "fk_cli_refdr")
	public DoctorExterno getDoctorRef() {
		return doctorRef;
	}

	public void setDoctorRef(DoctorExterno doctorRef) {
		this.doctorRef = doctorRef;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente")
	@OrderBy("fechaAdquisicion DESC")
	public List<AparatoCliente> getAparatosAuditivos() {
		return aparatosAuditivos;
	}

	public void setAparatosAuditivos(List<AparatoCliente> aparatosAuditivos) {
		this.aparatosAuditivos = aparatosAuditivos;
	}
	
	@Column(name = "diagnos_sordera", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getDiagnosSordera() {
		return diagnosSordera;
	}

	public void setDiagnosSordera(Date diagnosSordera) {
		this.diagnosSordera = diagnosSordera;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente", cascade = CascadeType.REMOVE)
	@OrderBy("fechaIngreso DESC")
	public List<ReparacionCliente> getReparacionesTaller() {
		return reparacionesTaller;
	}

	public void setReparacionesTaller(List<ReparacionCliente> reparacionesTaller) {
		this.reparacionesTaller = reparacionesTaller;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente", cascade = CascadeType.REMOVE)
	@OrderBy("fechaVenta DESC")
	public List<VentaProdServ> getVentas() {
		return ventas;
	}

	public void setVentas(List<VentaProdServ> ventas) {
		this.ventas = ventas;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cliente", cascade = CascadeType.REMOVE)
	@OrderBy("fechaIngreso DESC")
	public List<CuentaCobrar> getCuentasPagar() {
		return cuentasPagar;
	}

	public void setCuentasPagar(List<CuentaCobrar> cuentasPagar) {
		this.cuentasPagar = cuentasPagar;
	}

	@Transient
	public String getNombreCompleto() {
		String res = "";
		if(nombres != null)
			res = res.concat(nombres);
		if(apellidos != null)
			res = res.concat(" " + apellidos);
		
		return res;
	}
	
	@Transient
	public String getNombreCompletoEnc() {
		String res = "";
		if(nombresEncargado != null)
			res = res.concat(nombresEncargado);
		if(apellidosEncargado != null)
			res = res.concat(" " + apellidosEncargado);
		
		return res;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public MedioDifusion getMdif() {
		return mdif;
	}

	public void setMdif(MedioDifusion mdif) {
		this.mdif = mdif;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Departamento getDepto() {
		return depto;
	}

	public void setDepto(Departamento depto) {
		this.depto = depto;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}
	
	@Column(name = "ocupacion", nullable = true)
	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	
	@Column(name="usuario_registro",nullable=true)
	public Integer getUsuarioRegistro() {
		return usuarioRegistro;
	}

	public void setUsuarioRegistro(Integer usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}

	
	
	
	
	
	
	
	
	
}
