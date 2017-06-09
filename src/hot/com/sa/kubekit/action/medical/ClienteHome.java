package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;

import org.jboss.seam.Instance;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.component.html.HtmlTabPanel;

import com.sa.kubekit.action.crm.DeptoHome;
import com.sa.kubekit.action.crm.PaisHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.AsientoContable;
import com.sa.model.crm.Cliente;
import com.sa.model.crm.Departamento;
import com.sa.model.crm.Municipio;
import com.sa.model.crm.Pais;
import com.sa.model.medical.ClinicalHistory;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.workshop.AparatoCliente;
import com.sa.model.workshop.ReparacionCliente;

@Name("clienteHome")
@Scope(ScopeType.CONVERSATION)
public class ClienteHome extends KubeDAO<Cliente>{

	private static final long serialVersionUID = 1L;
	
	private String numId;
	private int codCli;
	private String nombre;
	private String Apellido;
	private String Email;
	private String Telefono;
	private String Direccion;
	
	private String nomCoinci;
	private String apellCoinci;
	
	private String tipoBusqueda;
	private String cadena;
	private Pais paisDefault;
	private List<MedicalAppointment> medicalAppointmentList = new ArrayList<MedicalAppointment>(0);
	private List<ClinicalHistory> clinicalHistoryList = new ArrayList<ClinicalHistory>(0);
	private List<MedicalAppointmentService> servicesAttended = new ArrayList<MedicalAppointmentService>(0);
	private List<MedicalAppointmentService> servicesPending = new ArrayList<MedicalAppointmentService>(0);
	private List<VentaProdServ> ventasEfectuadas = new ArrayList<VentaProdServ>(0);
	private List<Cliente> resultList  = new ArrayList<Cliente>(0);
	private List<Antecendente2> antecedentes;
	private List<ClienteJob> ocupacionLst;
	private Antecendente2 ant;
	private ClienteJob cj;
	private boolean esDependiente;
	private boolean esInfante;
	private boolean valtel = true;
	private String otroMedioRef;
	private String tab = "tab1"; //variable que contiene el valor de la tab seleccionada. Sirve para parametrizar con qué tab cargar el expediente del cliente.	
	private List<Departamento> departamentos = new ArrayList<Departamento>();
	private List<Municipio> municipios = new ArrayList<Municipio>();
	private boolean anteceDenteSl;
	private float sumaVentasCliente;
	private Date fechaVtasUs1;
	private Date fechaVtasUs2;
	
	private List<AparatoCliente> listaAparatosCliente;
	
	@In(required=false, create=true)
	MedicalAppointmentDAO medicalAppointmentDAO;
	
	@In(required=false, create=true)
	LoginUser loginUser;
	
	@In(required=false, create=true)
	DeptoHome deptoHome;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages.get("patientDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages.get("patientDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages.get("patientDAO_deleted")));
	}
	
	@Override
	public Cliente createInstance(){
		Cliente cliente = super.createInstance();
		return cliente;
	}
	
	public void newPatient(boolean a, String id){
		System.out.println("Entro a newPatiente ***************");
		setNumId(id);
		load(a);
		System.out.println("termino newPatient");
	}
	
	public void load(boolean detail) {
		
		loadAntecendentes();
		loadOcupaciones();
		try {
			Cliente cliente = (Cliente) getEntityManager().createQuery(
							"select c from Cliente c where c.id = :numId")
							.setParameter("numId", Integer.valueOf(getNumId()))
							.getSingleResult();		
			setInstance(cliente);
			updateMunicipios();
			
			//Si tiene los datos del encargado es un infante
			if(instance.getNombresEncargado() != null && !instance.getNombresEncargado().trim().equals(""))
				setEsDependiente(true);
			//Ventas de productos hechas al paciente
			ventasEfectuadas = getEntityManager()
					.createQuery("SELECT v FROM VentaProdServ v WHERE v.cliente = :cli")
					.setParameter("cli", instance)
					.getResultList();
			
			
			sumarVentascliente();
			
			cargarAparatosCliente(cliente);//Nuevo el 02/06/2017
			
			// cargamos historiales y citas medicas y los servicios
			System.out.println("Entrando en load: " + detail);
			if (detail) {
				clinicalHistoryList.clear();
				medicalAppointmentList.clear();
				servicesAttended.clear();
				servicesPending.clear();
				clinicalHistoryList.addAll(instance.getHistoriasClinicas());
				medicalAppointmentList.addAll(instance.getCitasMedicas());
				for (MedicalAppointment med : medicalAppointmentList) {
					for (MedicalAppointmentService serv : med
							.getMedicalAppointmentServices()) {
						if (serv.getServiceClinicalHistory() != null)
							servicesAttended.add(serv);
						else
							servicesPending.add(serv);
					}
				}				
			}
			System.out.println("Terminando en load: " + detail);
		} catch (Exception e) {
			e.printStackTrace();
			setInstance(new Cliente());
			loadPaisDefault();
			instance.setTipoDoc("DUI");
			System.out.println("Genero nueva instancia de cliente");
		}
	}
	
	
	public void loadNewPatientRecep()
	{
		
		setInstance(new Cliente());
		instance.setTipoDoc("DUI");
		loadAntecendentes();
		loadOcupaciones();
		loadPaisDefault();
		deptoHome.loadResultList();
		System.out.println("Entro al nuevo load");
		System.out.println("idPais instancia"+instance.getPais().getId());
		//updateMunicipios();
	}
	
	public void cargarAparatosCliente(Cliente cliente)
	{
		listaAparatosCliente = new ArrayList<AparatoCliente>();
		try {
			listaAparatosCliente = getEntityManager().createQuery("select a from AparatoCliente a where activo=true").getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void loadPatientById(int idPaciente)
	{
		//try{
			Cliente cliente = (Cliente) getEntityManager().createQuery(
					"select c from Cliente c where c.id = :numId")
					.setParameter("numId", idPaciente)
					.getSingleResult();		
			setInstance(cliente);
			loadOcupaciones();
		/*}
		catch (Exception e) {
			FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema cargando el paciente");
			return;
		}*/
	}
	
	
	public void loadHistory(boolean detail) {
		//Conversation.instance().begin();
		
		loadAntecendentes();
		loadOcupaciones();
		try {
			Cliente cliente = (Cliente) getEntityManager().createQuery(
							"select c from Cliente c where c.id = :numId")
							.setParameter("numId", Integer.valueOf(getNumId()))
							.getSingleResult();		
			setInstance(cliente);
			loadPaisDefault();
			updateMunicipios();
			
			//Si tiene los datos del encargado es un infante
			if(instance.getNombresEncargado() != null && !instance.getNombresEncargado().trim().equals(""))
				setEsDependiente(true);
			//Ventas de productos hechas al paciente
			ventasEfectuadas = getEntityManager()
					.createQuery("SELECT v FROM VentaProdServ v WHERE v.cliente = :cli")
					.setParameter("cli", instance)
					.getResultList();
			
			
			sumarVentascliente();
			
			
			
			// cargamos historiales y citas medicas y los servicios
			System.out.println("Entrando en load: " + detail);
			if (detail) {
				clinicalHistoryList.clear();
				medicalAppointmentList.clear();
				servicesAttended.clear();
				servicesPending.clear();
				clinicalHistoryList.addAll(instance.getHistoriasClinicas());
				medicalAppointmentList.addAll(instance.getCitasMedicas());
				for (MedicalAppointment med : medicalAppointmentList) {
					for (MedicalAppointmentService serv : med
							.getMedicalAppointmentServices()) {
						if (serv.getServiceClinicalHistory() != null)
							servicesAttended.add(serv);
						else
							servicesPending.add(serv);
					}
				}				
			}
			System.out.println("Terminando en load: " + detail);
		} catch (Exception e) {
			e.printStackTrace();
			setInstance(new Cliente());
			loadPaisDefault();
			instance.setTipoDoc("DUI");
		}
	}
	
	
	//Metodo para sumar el total de ventas de un cliente 
	public void sumarVentascliente()
	{
		sumaVentasCliente=0f;
		
		if(ventasEfectuadas.size()>0)
		{
			for(VentaProdServ vta: ventasEfectuadas)
			{
				sumaVentasCliente+=vta.getMonto();
			}
		}
	}
	
	public void buscarRangoVentas()
	{
		
		ventasEfectuadas.clear();
		
		String fltFch="";
		fltFch = " AND v.fechaVenta BETWEEN :fch1 AND :fch2 ";
		
		setFechaVtasUs1(truncDate(getFechaVtasUs1(), false));
		setFechaVtasUs2(truncDate(getFechaVtasUs2(), true));
		
		System.out.println("fecha1 "+ getFechaVtasUs1());
		System.out.println("fecha2" + getFechaVtasUs2());
		
		try {
			
			ventasEfectuadas = getEntityManager()
					.createQuery("SELECT v FROM VentaProdServ v WHERE v.cliente = :cli"
							+ fltFch + " ORDER BY v.fechaVenta DESC ")
					.setParameter("cli", instance)
					.setParameter("fch1", getFechaVtasUs1())
					.setParameter("fch2", getFechaVtasUs2())
					.getResultList();	
			
		} catch (Exception e) {
			
			e.printStackTrace();	
		}
		
		
		sumarVentascliente();
		//sumaVentasCliente=0f;
		/*for(VentaProdServ vta: ventasEfectuadas)
		{
			sumaVentasCliente+=vta.getMonto();
		}*/
		
		//System.out.println("*****tam ventas "+ ventasEfectuadas.size());
		
	}
	
	public void cargarPaciente(Cliente cl)
	{
		System.out.println("Entro a metodo cargar paciente *******");
		setInstance(cl);
	}
	
	/*public void limpiarPaciente()
	{
		setInstance(null);
		System.out.println("se limpio el paciente paciente *******");
	}*/
	
	public Integer calcularEdad(){
		if (instance != null && instance.getFechaNacimiento() != null){
		Calendar dob = Calendar.getInstance();  
		dob.setTime(instance.getFechaNacimiento());  
		Calendar today = Calendar.getInstance();  
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);  
		if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
		  age--;  
		} else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
		    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
		  age--;  
		}
		if (age < 0 ){
			FacesMessages.instance().add(
					sainv_messages.get("clienteHome_fecNa_invalida"));
					return 0;
		}else 			
			return age;
		}
		return 0;
	}
	
	public void loadAntecendentes() {
		//metodo de prueba, tendria que cargar de la base de datos
		antecedentes = new ArrayList<Antecendente2>();
		ant= new Antecendente2("alcoholismo", "");
		antecedentes.add(ant);
		ant= new Antecendente2("tabaquismo", "");
		antecedentes.add(ant);
		ant= new Antecendente2("supuracion", "");
		antecedentes.add(ant);
		ant= new Antecendente2("diabetes", "");
		antecedentes.add(ant);
		ant= new Antecendente2("triglicelidos", "");
		antecedentes.add(ant);
		ant= new Antecendente2("rinnitis", "");
		antecedentes.add(ant);
		ant= new Antecendente2("otro", "");
		antecedentes.add(ant);
		
	}
	
	public void addAntecedente(Antecendente2 ant){		
		if(instance.getGeneralInformation().getFamilyHeritage()==null || instance.getGeneralInformation().getFamilyHeritage()==""){
			cadena= ant.getNombre();	
		}else{
			cadena= instance.getGeneralInformation().getFamilyHeritage() + ", " +ant.getNombre()  ;
		}		
		instance.getGeneralInformation().setFamilyHeritage(cadena);		
		System.out.println("*** Entro a metodo agregar antecedente");
	}
	
	public void loadPaisDefault(){		
		System.out.println("ACtualizo el pais por defecto");
		instance.setPais((Pais)getEntityManager().createQuery("SELECT p FROM Pais p WHERE p.id = 68").getSingleResult());
	}
	
	//debera ser desde una tabla de la base de datos. y cargar con un query;
	public void loadOcupaciones(){
		ocupacionLst = new ArrayList<ClienteJob>();
		cj = new ClienteJob("Comerciante");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Medico");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Trabajador Industrial");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Mecanico Automotriz");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Electricista");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Ama de casa");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Ingeniero");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Profesor");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Estudiante");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Carpintero");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Agricultor");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Motorista");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Albanil");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Militar");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Jubilado");
		ocupacionLst.add(cj);
		cj = new ClienteJob("Otro");
		ocupacionLst.add(cj);
	}
	
	
	public boolean isNumeric(String cadena){
		
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe){
			return false;
		}
		
	}
	
	public void addOcupacion(ClienteJob j){				
		instance.setOcupacion(j.getNombre());		
	}
	
	//guarda y limpia desde el modal para agregar nuevo paciente
	public void saveClear(boolean s){
		FacesMessages.instance().clear();
		
		if(s){
			valtel = false;
			if(instance.getNombres() == null){
				
				FacesMessages.instance().add(Severity.WARN,sainv_messages.get("error_nom"));
				return;
			}
			
			if(instance.getApellidos() == null){
				
				
				FacesMessages.instance().add(Severity.WARN,sainv_messages.get("error_ap"));
				return;
			} 
			
			if(instance.getTelefono1() == null){
				

				FacesMessages.instance().add(Severity.WARN,sainv_messages.get("error_tel"));
				return;
			}
			
			
			if(isNumeric(instance.getNombres()))
			{
				FacesMessages.instance().add(Severity.WARN,"Ingresar solo letras para nombres");
				return;
			}
			
			if(isNumeric(instance.getApellidos()))
			{
				FacesMessages.instance().add(Severity.WARN,"Ingresar solo letras para apellidos");
				return;
			}
			
			if(instance.getMdif()!=null)
			{
				if(instance.getMdif().getNombre().equals("Referido por doctor") && instance.getDoctorRef()==null)
				{
					FacesMessages.instance().add(Severity.WARN,"Especifique el doctor que refiere");
					return;
				}
				
				if(instance.getMdif().getNombre().equals("Referido por paciente") && instance.getReferidoPor()==null)
				{
					FacesMessages.instance().add(Severity.WARN,"Especifique el paciente que refiere");
					return;
				}
			}
			
			System.out.println("Entro a SaveClear");
			if(!save())
			{
				System.out.println("Ocurrio una validacion al guardar");
				return;
			}
			
			valtel = true;
			medicalAppointmentDAO.getInstance().setCliente(instance);
			//setInstance(new Cliente()); // cuando se puso esto??
			
			//setInstance(null); //nuevo el 20/02/2017<<---
			
			clearInstance();//nuevo el 20/02/2017<<---
			
			//loadPaisDefault(); //comentado el 20/02/2017
			//instance.setTipoDoc("DUI"); //comentado el 20/02/2017 
			System.out.println("paso por null cliente if ** ");
			
		}else{
			medicalAppointmentDAO.getInstance().setCliente(instance);
			//setInstance(new Cliente()); // comentado el 20/02/2017
			loadPaisDefault();
			setInstance(null);// nuevo 20/02/2017
			//  // comentado el 20/02/2017
			//instance.setTipoDoc("DUI");  // comentado el 20/02/2017
		}
	}
	
	public void updateMunicipios(){
		Departamento depto = instance.getDepto();
		System.out.println("Entré a getMunicipios");
		if (depto!=null){
			municipios.clear();
			municipios.addAll(depto.getMunicipios());
			System.out.println("Size de getMunicipios: " + depto.getMunicipios().size() + " size de municipios: "+ municipios.size());
		}
	}
	
	public void loadMunicipios()
	{
		municipios = getEntityManager().createQuery("SELECT m FROM Municipio m").getResultList();
	}
	
	public void selectMunicipio(Municipio municipio)
	{
		instance.setDepto(municipio.getDepartamento());
		instance.setMunicipio(municipio);
	}
	
	
	public void esInfante(){
		if (esInfante == true){
			setEsDependiente(true);
		} else setEsDependiente(false);
	}
	
	public void buscarPacientes(){
		/*resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :coinci " +
				"OR UPPER(c.apellidos) LIKE :coinci OR UPPER(c.docId) LIKE :coinci")
				.setParameter("coinci","%"+this.getNomCoinci().toUpperCase()+"%")
				.setMaxResults(60)
				.getResultList();*/
		
		//Si el nombre es diferente de nulo y diferente de vacio y el apellido es nulo o vacio
		if(getNomCoinci()!=null && getApellCoinci()==null)
		{
			
			resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :nomCoinci ")
					.setParameter("nomCoinci","%"+this.getNomCoinci().toUpperCase()+"%")
					.setMaxResults(35)
					.getResultList();
			
		}
		else if(getApellCoinci()!=null && getNomCoinci()==null)
		{
			
			resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.apellidos) LIKE :apellCoinci ")
					.setParameter("apellCoinci","%"+this.getApellCoinci().toUpperCase()+"%")
					.setMaxResults(35)
					.getResultList();
			
		}
		else if(getApellCoinci()!=null && getNomCoinci()!=null)
		{
			/*if(getTipoBusqueda()=="" || getTipoBusqueda()==null)
				setTipoBusqueda("or");
			
			if(getTipoBusqueda().equals("or"))
			{
				resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :nomCoinci " +
						"or UPPER(c.apellidos) LIKE :apellCoinci ")
						.setParameter("nomCoinci","%"+this.getNomCoinci().toUpperCase()+"%")
						.setParameter("apellCoinci","%"+this.getApellCoinci().toUpperCase()+"%")
						.setMaxResults(30)
						.getResultList();
			}
			else
			{*/
				resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :nomCoinci " +
						"and UPPER(c.apellidos) LIKE :apellCoinci ")
						.setParameter("nomCoinci","%"+this.getNomCoinci().toUpperCase()+"%")
						.setParameter("apellCoinci","%"+this.getApellCoinci().toUpperCase()+"%")
						.setMaxResults(30)
						.getResultList();
			//}
			
		}
		
		
		 		//getEntityManager().clear();
		 		
		 	System.out.println("num "+ resultList.size());	
		 		//				.setParameter("dui","%"+this.getNomCoinci().toUpperCase()+"%")
		 		//.setParameter("nom","%"+this.getNomCoinci().toUpperCase()+"%")
				//.setParameter("ape", "%"+this.getNomCoinci().toUpperCase()+"%")
	}
	
	
	
	public void buscarMasPacientes(){
		
		//Si el nombre es diferente de nulo y diferente de vacio y el apellido es nulo o vacio
		if(getNomCoinci()!=null && getApellCoinci()==null)
		{
			
			resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :nomCoinci ")
					.setParameter("nomCoinci","%"+this.getNomCoinci().toUpperCase()+"%")
					.getResultList();
			
		}
		else if(getApellCoinci()!=null && getNomCoinci()==null)
		{
			
			resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.apellidos) LIKE :apellCoinci ")
					.setParameter("apellCoinci","%"+this.getApellCoinci().toUpperCase()+"%")
					.getResultList();
			
		}
		else if(getApellCoinci()!=null && getNomCoinci()!=null)
		{
			resultList = getEntityManager().createQuery("SELECT c from Cliente c WHERE UPPER(c.nombres) LIKE :nomCoinci " +
			"and UPPER(c.apellidos) LIKE :apellCoinci ")
			.setParameter("nomCoinci","%"+this.getNomCoinci().toUpperCase()+"%")
			.setParameter("apellCoinci","%"+this.getApellCoinci().toUpperCase()+"%")
			.getResultList();
		}
		
		
	}
	
	
	 public List<Object[]> getPacientesByName(Object o) {
		/*return getEntityManager().createQuery("SELECT c.nombres, c.apellidos,c.telefono1, c.docId ,c from Cliente c WHERE UPPER(c.nombres) LIKE :nom " +
					"OR UPPER(c.apellidos) LIKE :ape  OR UPPER(c.docId) LIKE :dui")
					.setParameter("dui","%"+o.toString().toUpperCase()+"%")
					.setParameter("nom","%"+o.toString().toUpperCase()+"%")
					.setParameter("ape", "%"+o.toString().toUpperCase()+"%")
					.setMaxResults(30).getResultList();*/
		 
		 //System.out.println("busqueda cliente " + o.toString().toUpperCase().trim());
		 //Se mejoro la busqueda del cliente por nombre y/o apellido desde un solo campo de texto
		 
		 /*return getEntityManager().createQuery("SELECT c.nombres, c.apellidos,c.telefono1, c.docId ,c from Cliente c WHERE CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos))) LIKE :nom " +
					" OR UPPER(c.docId) LIKE :dui")
					.setParameter("dui","%"+ o.toString().toUpperCase()+"%")
					.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
					.setMaxResults(30).getResultList();*/
		 
		 // CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos)))
		 /*return getEntityManager().createQuery("SELECT c.nombres, c.apellidos,c.telefono1, c.docId ,c from Cliente c WHERE CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos))) IN (:nom) " +
					" OR UPPER(c.docId) like :dui")
					.setParameter("dui","%"+ o.toString().toUpperCase()+"%")
					.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
					.setMaxResults(30).getResultList();*/
		 
		 /*return getEntityManager().createQuery("SELECT c.nombres, c.apellidos,c.telefono1, c.docId ,c from Cliente c WHERE :nom MEMBER OF CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos)))"+
					" OR UPPER(c.docId) LIKE :dui")
					.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
					.setParameter("dui","%"+ o.toString().toUpperCase()+"%")
					.setMaxResults(30).getResultList();*/
		 
		 return getEntityManager().createQuery("SELECT c.nombres, c.apellidos,c.telefono1, c.docId ,c from Cliente c WHERE CONCAT(UPPER(TRIM(c.nombres)),' ',UPPER(TRIM(c.apellidos))) LIKE :nom " +
					" OR UPPER(c.docId) LIKE :dui")
					.setParameter("dui","%"+ o.toString().toUpperCase()+"%")
					.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
					.setMaxResults(30).getResultList();
		 
		 //.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
		 
		//.setParameter("ape", "%"+o.toString().toUpperCase()+"%")
	 }
	 
	@Override
	public boolean preSave() {
		FacesMessages.instance().clear();
		
		
		/*if(instance.getDocId()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar el numero de identificacion");
			return false;
		}*/
		
		if(instance.getNombres()==null || instance.getApellidos()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar el nombre y apellido");
			return false;
		}
		
		/*if(instance.getOcupacion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la ocupacion del paciente");
			return false;
		}
		
		if(instance.getFechaNacimiento()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la fecha de nacimiento del paciente");
			return false;
		}*/
		
		if(instance.getTelefono1()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar al menos un telefono");
			return false;
		}
		
		/*if(instance.getDireccion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la direccion del paciente");
			return false;
		}
		
		if(esInfante==true && (instance.getNombresEncargado()==null || instance.getApellidosEncargado()==null))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre y apellidos del encargado");
			return false;
		}
		*/
		/*if(instance.getPais()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el pais");
			return false;
		}
		
		if(instance.getMdif()==null && instance.getDoctorRef()==null && instance.getReferidoPor()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar un medio de referencia");
			return false;
		}
		
		if(instance.getMdif()!=null)
		{
			if(instance.getMdif().getNombre().equals("Referido por doctor") && instance.getDoctorRef()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el doctor que refiere");
				return false;
			}
			
			if(instance.getMdif().getNombre().equals("Referido por paciente") && instance.getReferidoPor()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el paciente que refiere");
				return false;
			}
		}*/
		
		
		if(instance.getFechaNacimiento()!=null)
		{
			
				int edad=0;
				edad=calcularEdad();
				System.out.println("EDAD ENCONTRADA: "+edad);
				
				if(edad==0)
				{
					FacesMessages.instance().add(Severity.WARN,"Formato de fecha incorrecto");
					return false;
				}
			
		}
		else
		{	
			System.out.println("Fecha ingresada: "+instance.getFechaNacimiento());
		}
		
		
		instance.setFechaCreacion(new Date());
		if(instance.getMedioReferido() != null && 
				instance.getMedioReferido().equals("OTRO") && 
				otroMedioRef != null && 
				!otroMedioRef.trim().equals(""))
			instance.setMedioReferido(otroMedioRef);
			instance.setUsuarioRegistro(loginUser.getUser().getId());System.out.println("usuario login: "+loginUser.getUser().getNombreUsuario());
		return true;
	}
	
	
	public boolean guardarDesdeCrm()
	{
		
		
		if(instance.getNombres()==null || instance.getApellidos()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar el nombre y apellido");
			return false;
		}
		
		if(instance.getOcupacion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la ocupacion del paciente");
			return false;
		}
		
		if(instance.getFechaNacimiento()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la fecha de nacimiento del paciente");
			return false;
		}
		
		if(instance.getTelefono1()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar al menos un telefono");
			return false;
		}
		
		if(instance.getDireccion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la direccion del paciente");
			return false;
		}
		
		if(esInfante==true && (instance.getNombresEncargado()==null || instance.getApellidosEncargado()==null))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre y apellidos del encargado");
			return false;
		}
		
		if(instance.getPais()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el pais");
			return false;
		}
		
		if(instance.getDepto()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el departamento");
			return false;
		}
		
		if(instance.getMunicipio()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el municipio");
			return false;
		}
		
		if(instance.getMdif()==null && instance.getDoctorRef()==null && instance.getReferidoPor()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar un medio de referencia");
			return false;
		}
		
		if(instance.getMdif()!=null)
		{
			if(instance.getMdif().getNombre().equals("Referido por doctor") && instance.getDoctorRef()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el doctor que refiere");
				return false;
			}
			
			if(instance.getMdif().getNombre().equals("Referido por paciente") && instance.getReferidoPor()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el paciente que refiere");
				return false;
			}
		}
		
		if(!esInfante)
		{
			if(instance.getDocId()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Debe ingresar el numero de identificacion");
				return false;
			}
			
		}
		
		
		
		
		return save();
		
		
	}
	
	public void validarReferencias()
	{
		
		if(instance.getMdif().getId()!=3 && instance.getDoctorRef()!=null)
		{
			instance.setDoctorRef(null);
		}
		
		if(instance.getMdif().getId()!=7 && instance.getReferidoPor()!=null)
		{
			instance.setReferidoPor(null);
		}
		
	}
	
	@Override
	public boolean preModify() {
		
		valtel = false;
		if(instance.getFechaCreacion() == null)
			instance.setFechaCreacion(new Date());
		if(instance.getMedioReferido() != null && 
				instance.getMedioReferido().equals("OTRO") && 
				otroMedioRef != null && !otroMedioRef.trim().equals(""))
			instance.setMedioReferido(otroMedioRef);
		
		
		/*if(instance.getDocId()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar el numero de identificacion");
			return false;
		}*/
		
		if(instance.getNombres()==null || instance.getApellidos()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar el nombre y apellido");
			return false;
		}
		
		if(instance.getOcupacion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la ocupacion del paciente");
			return false;
		}
		
		if(instance.getFechaNacimiento()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar la fecha de nacimiento del paciente");
			return false;
		}
		
		if(instance.getTelefono1()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Favor ingresar al menos un telefono");
			return false;
		}
		
		if(instance.getDireccion()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese la direccion del paciente");
			return false;
		}
		
		if(esInfante==true && (instance.getNombresEncargado()==null || instance.getApellidosEncargado()==null))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre y apellidos del encargado");
			return false;
		}
		
		if(instance.getPais()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el pais");
			return false;
		}
		
		
		if(instance.getDepto()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el departamento");
			return false;
		}
		
		
		if(instance.getMunicipio()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el municipio");
			return false;
		}
		
		if(instance.getMdif()==null && instance.getDoctorRef()==null && instance.getReferidoPor()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe ingresar un medio de referencia");
			return false;
		}
		
		if(instance.getMdif()!=null)
		{
			if(instance.getMdif().getNombre().equals("Referido por doctor") && instance.getDoctorRef()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el doctor que refiere");
				return false;
			}
			
			if(instance.getMdif().getNombre().equals("Referido por paciente") && instance.getReferidoPor()==null)
			{
				FacesMessages.instance().add(Severity.WARN,"Especifique el paciente que refiere");
				return false;
			}
		}
		
	
		return true;
	}

	@Override
	public boolean preDelete() {
		//Empezamos a borrar tooodo
		List tmpList = new ArrayList();
		for(ClinicalHistory ch : instance.getHistoriasClinicas())
			getEntityManager().remove(ch);
		
		for(MedicalAppointment ma : instance.getCitasMedicas())
			getEntityManager().remove(ma);
		
		tmpList = getEntityManager()
				.createQuery("SELECT g FROM GeneralInformation g WHERE g.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList)
			getEntityManager().remove(ob);
		
		tmpList = getEntityManager()
				.createQuery("SELECT a FROM AsientoContable a WHERE a.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList) {
			((AsientoContable)ob).setCliente(null);
			getEntityManager().merge(ob);
		}
		
		tmpList = getEntityManager()
				.createQuery("SELECT c FROM CuentaCobrar c WHERE c.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList)
			getEntityManager().remove(ob);
		
		tmpList = getEntityManager()
				.createQuery("SELECT c FROM CotizacionComboApa c WHERE c.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList)
			getEntityManager().remove(ob);
		
		tmpList = getEntityManager()
				.createQuery("SELECT v FROM VentaProdServ v WHERE v.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList) {
			((VentaProdServ)ob).setCliente(null);
			getEntityManager().merge(ob);
		}
		
		tmpList = getEntityManager()
				.createQuery("SELECT r FROM ReparacionCliente r WHERE r.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList)
			getEntityManager().remove(ob);
			
		tmpList = getEntityManager()
				.createQuery("SELECT a FROM AparatoCliente a WHERE a.cliente = :cli")
				.setParameter("cli", instance)
				.getResultList();
		if(tmpList != null)
		for(Object ob : tmpList)
			getEntityManager().remove(ob);
		
		getEntityManager().refresh(instance);
		return true;
	}
	
	public void modificarClientesNuevos()
	{
		modify();
		valtel=true;
	}

	@Override
	public void posSave() {
	}

	@Override
	public void posModify() {
		System.out.println("Entro al posModify");
		valtel=true;
		
	}

	@Override
	public void posDelete() {}
	
	
	public void borrarPaciente() {
		System.out.println("");
	}

	public String getNumId() {
		if(numId == null)
			return "0";
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public List<MedicalAppointment> getMedicalAppointmentList() {
		return medicalAppointmentList;
	}

	public void setMedicalAppointmentList(List<MedicalAppointment> medicalAppointmentList) {
		this.medicalAppointmentList = medicalAppointmentList;
	}

	public List<ClinicalHistory> getClinicalHistoryList() {
		return clinicalHistoryList;
	}

	public void setClinicalHistoryList(List<ClinicalHistory> clinicalHistoryList) {
		this.clinicalHistoryList = clinicalHistoryList;
	}

	public List<MedicalAppointmentService> getServicesAttended() {
		return servicesAttended;
	}

	public void setServicesAttended(List<MedicalAppointmentService> servicesAttended) {
		this.servicesAttended = servicesAttended;
	}

	public List<MedicalAppointmentService> getServicesPending() {
		return servicesPending;
	}

	public void setServicesPending(List<MedicalAppointmentService> servicesPending) {
		this.servicesPending = servicesPending;
	}

	public boolean isEsInfante() {
		return esInfante;
	}

	public void setEsInfante(boolean esInfante) {
		this.esInfante = esInfante;
	}

	public String getOtroMedioRef() {
		return otroMedioRef;
	}

	public void setOtroMedioRef(String otroMedioRef) {
		this.otroMedioRef = otroMedioRef;
	}

	public List<VentaProdServ> getVentasEfectuadas() {
		return ventasEfectuadas;
	}

	public void setVentasEfectuadas(List<VentaProdServ> ventasEfectuadas) {
		this.ventasEfectuadas = ventasEfectuadas;
	}

	public List<Cliente> getResultList() {
		return resultList;
	}

	public void setResultList(List<Cliente> resultList) {
		this.resultList = resultList;
	}

	public int getCodCli() {
		return codCli;
	}

	public void setCodCli(int codCli) {
		this.codCli = codCli;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return Apellido;
	}

	public void setApellido(String apellido) {
		Apellido = apellido;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getTelefono() {
		return Telefono;
	}

	public void setTelefono(String telefono) {
		Telefono = telefono;
	}

	public String getDireccion() {
		return Direccion;
	}

	public void setDireccion(String direccion) {
		Direccion = direccion;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public boolean isEsDependiente() {
		return esDependiente;
	}

	public void setEsDependiente(boolean esDependiente) {
		this.esDependiente = esDependiente;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public List<Departamento> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<Departamento> departamentos) {
		this.departamentos = departamentos;
	}

	public List<Municipio> getMunicipios() {
		return municipios;
	}

	public void setMunicipios(List<Municipio> municipios) {
		this.municipios = municipios;
	}

	public List<Antecendente2> getAntecedentes() {
		return antecedentes;
	}

	public void setAntecendentes(List<Antecendente2> antecedentes) {
		this.antecedentes = antecedentes;
	}

	public List<ClienteJob> getOcupacionLst() {
		return ocupacionLst;
	}

	public void setOcupacionLst(List<ClienteJob> ocupacionLst) {
		this.ocupacionLst = ocupacionLst;
	}

	public boolean isValtel() {
		return valtel;
	}

	public void setValtel(boolean valtel) {
		this.valtel = valtel;
	}

	public String getApellCoinci() {
		return apellCoinci;
	}

	public void setApellCoinci(String apellCoinci) {
		this.apellCoinci = apellCoinci;
	}

	public String getTipoBusqueda() {
		return tipoBusqueda;
	}

	public void setTipoBusqueda(String tipoBusqueda) {
		this.tipoBusqueda = tipoBusqueda;
	}

	public boolean isAnteceDenteSl() {
		return anteceDenteSl;
	}

	public void setAnteceDenteSl(boolean anteceDenteSl) {
		this.anteceDenteSl = anteceDenteSl;
	}

	public float getSumaVentasCliente() {
		return sumaVentasCliente;
	}

	public void setSumaVentasCliente(float sumaVentasCliente) {
		this.sumaVentasCliente = sumaVentasCliente;
	}

	public Date getFechaVtasUs1() {
		return fechaVtasUs1;
	}

	public void setFechaVtasUs1(Date fechaVtasUs1) {
		this.fechaVtasUs1 = fechaVtasUs1;
	}

	public Date getFechaVtasUs2() {
		return fechaVtasUs2;
	}

	public void setFechaVtasUs2(Date fechaVtasUs2) {
		this.fechaVtasUs2 = fechaVtasUs2;
	}

	public List<AparatoCliente> getListaAparatosCliente() {
		return listaAparatosCliente;
	}

	public void setListaAparatosCliente(List<AparatoCliente> listaAparatosCliente) {
		this.listaAparatosCliente = listaAparatosCliente;
	}
	
	
	

}
