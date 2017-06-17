package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Cliente;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.medical.id.MedicalAppointmentServiceId;
import com.sa.model.sales.Service;
import com.sa.model.security.Sucursal;

@Name("medicalAppointmentDAO")
@Scope(ScopeType.CONVERSATION)
public class MedicalAppointmentDAO extends KubeDAO<MedicalAppointment> {

	private static final long serialVersionUID = 1L;

	private Integer appointmentId;
	private List<MedicalAppointmentService> appointmentItems = new ArrayList<MedicalAppointmentService>();
	private List<MedicalAppointmentService> itemsNoBill = new ArrayList<MedicalAppointmentService>();
	private List<Service> servicios = new ArrayList<Service>();
	private List<Service> avaliableServs = new ArrayList<Service>();
	private List<MedicalAppointment> selMedAps = new ArrayList<MedicalAppointment>();
	private Sucursal selectedSuc;
	private String search ="";
	private String comentStatus;
	
	
	//Nuevo prueba 21/12/2016
	
	
	
	
	@In
	private LoginUser loginUser;

	@Override
	public void create() {  
		setCreatedMessage(createValueExpression(sainv_messages
				.get("medicalAppointmentDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages	
				.get("medicalAppointmentDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("medicalAppointmentDAO_deleted")));
	}

	public void load() {
		try {
			cleanForNew();
			selMedAps.clear();
			MedicalAppointment medicalAppointment = getEntityManager().find(
			MedicalAppointment.class, appointmentId);
			select(medicalAppointment);
			//diagnosticoMedHome.setInstance(diagnosticoMedHome); nuevo 
			
		} catch (Exception e) {
			System.out.println("catch: clearInstance");
			clearInstance();
		}
	}
	
	public void loadSucursalDefault()
	{
		instance.setSucursal(loginUser.getUser().getSucursal().getSucursalSuperior());
	}
	
	public void load2() {
		try {
			selMedAps.clear();
			MedicalAppointment medicalAppointment = getEntityManager().find(
			MedicalAppointment.class, appointmentId);
			select(medicalAppointment);
		} catch (Exception e) {
			System.out.println("catch: clearInstance");
			clearInstance();
		}
	}
	
	public void loadAppointment()
	{
		System.out.println("entro a cargar");
		MedicalAppointment medicalAppointment = getEntityManager().find(
				MedicalAppointment.class, appointmentId);
				select(medicalAppointment);
				
		System.out.println("Tamanio servicio consulta "+medicalAppointment.getServiciosMedicos().size());
		List<Service> listaServicios= new ArrayList<Service>();
		
		listaServicios=getEntityManager().createQuery("SELECT m.service FROM MedicalAppointmentService m where m.medicalAppointment.id="+medicalAppointment.getId()+"").getResultList();
		/*for(MedicalAppointmentService serv:medicalAppointment.getServiciosMedicos())
		{
			listaServicios.add(serv.getService());
		}
		*/
		
		setServicios(listaServicios);
				
		
		System.out.println("tam servicios "+servicios.size());	
		System.out.println("cliente "+medicalAppointment.getCliente());
		System.out.println("cliente "+medicalAppointment.getStatus());
	}
	
	
	public boolean validateSucursal(){
		try {
			Sucursal selSuc= getEntityManager().find(
			Sucursal.class, instance.getSucursal().getId());
			selectedSuc = selSuc;
			return true;
		}
		catch (Exception e) {
			System.out.println("No encontré la sucursal");
			return false;
		}
	}
	
	public void associateClient(Cliente cliente){
		instance.setCliente(cliente);
	}
	
	
	public void loadSucursal(){
		System.out.println("loginUser.getUser().getSucursal().getSucursalSuperior() " + loginUser.getUser().getSucursal().getSucursalSuperior().getNombre());
		System.out.println("loginUser.getUser().getSucursal() " + loginUser.getUser().getSucursal().getNombre());
		System.out.println("Instance: " + instance.getStatus() +"  "+ instance.getDateTime());
		
		if (loginUser.getUser().getSucursal().getSucursalSuperior()!= null)
			this.setSelectedSuc(loginUser.getUser().getSucursal().getSucursalSuperior());
		else if (loginUser.getUser().getSucursal()!=null)
			this.setSelectedSuc(loginUser.getUser().getSucursal());
		
		System.out.println("sucursal after "+ instance.getSucursal());
	}
	
	public void addServicio(Service serv) {
		servicios.add(serv);
	}
	
	public void removerServicio(Service serv) {
		servicios.remove(serv);
	}
	
	public void enviarCorreo()
	{
		
		String contenido="<h1>PRUEBA</h1></br>" +
				"<table>" +
				"<tr>" +
				"	<td>columna1</td> " +
				"	<td>columna2</td> " +
				"	<td>columna3</td> " +
				"	<td>columna4</td> " +
				"</tr>" +
				"<tr>" +
				"	<td>contenido1</td>" +
				"	<td>contenido2</td>" +
				"	<td>contenido3</td>" +
				"	<td>contenido4</td>" +
				"</tr>";
		
		CorreoAgenda correo= new CorreoAgenda("yosael.gutierrez@gmail.com", "Asunto prueba simple", contenido);
		correo.enviarCorreoSimple();
	}
	
	
	//public void addServicioEdit

	@Override
	public void select(MedicalAppointment medicalAppointment) {
		super.select(medicalAppointment);
		if (getInstance().getMedicalAppointmentServices()!=null){
		setAppointmentItems(new ArrayList<MedicalAppointmentService>(
				getInstance().getMedicalAppointmentServices()));
		} else {
			setAppointmentItems(new ArrayList<MedicalAppointmentService>());
		}
	}

	@Override
	public void posDelete() {

	}

	@Override
	public void posModify() {
		
		//saveModifyService();
		setComentStatus("");

	}

	@Override
	public void posSave() {
		saveServices();
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public boolean preModify() {
		
		if(comentStatus!="")
		{
			instance.setComment(comentStatus);
		}
		
		
		return true;
	}

	@Override
	public boolean preSave() {
		getInstance().setStatus(0);
		System.out.println("INFORMACION INSTANCE");
		System.out.println(instance.getSucursal() != null ? instance.getSucursal().getNombre() : "Suc nula");
		System.out.println(instance.getComment() != null ? instance.getComment() : "Comment nula");
		System.out.println("FIN INFORMACION INSTANCE");
		// valida campos vacios
		if (getInstance().getDoctor() == null) {
			FacesMessages.instance().add(
					sainv_messages.get("medicalAppointmentDAO_error1"));
			return false;
		}
		else if (getInstance().getSucursal() == null){
			FacesMessages.instance().add("No ha seleccionado una sucursal");
			return false;
		}
		
		System.out.println("Este es el cliente de la cita antes de guardar"+instance.getCliente().getNombres());
		
		return validateDate();
	}
	
	//Método que verifica si el paciente ya ha pasado consulta anteriormente.
	public String verificarSubsecuente() {
		String subsecuencia="";
		try{
			Object res = getEntityManager()
					.createQuery(
							"select COUNT(s.cliente.id) from MedicalAppointment s where s.cliente.id = :cliente")
					.setParameter("cliente", getInstance().getCliente().getId())
					.getSingleResult();
		if (res != null && (Long)res > 1) {
			subsecuencia = sainv_messages.get("medicalAppointmentDAO_subsecuente")  + res + ". "
		+ sainv_messages.get("medicalAppointmentDAO_fecCreac")+ " " + getInstance().getCliente().getFechaCreacion().toString().substring(0, 10);
			
			return subsecuencia;
		} else
			subsecuencia=sainv_messages.get("medicalAppointmentDAO_nuevo") + ". "
					+ sainv_messages.get("medicalAppointmentDAO_fecCreac")+ " " + getInstance().getCliente().getFechaCreacion().toString().substring(0, 10);
			return subsecuencia;
		}
		catch(Exception E){
			System.out.println(E.getMessage());
			subsecuencia=sainv_messages.get("medicalAppointmentDAO_error6");
			return subsecuencia;
		}
	}
	
	//Método que verifica si el paciente ya ha pasado consulta anteriormente, recibe como parametro la cita medica.
	public String verificarSubsecuentes(MedicalAppointment med) {
		String subsecuencia="";
		try{
			Object res = getEntityManager()
					.createQuery(
							"select COUNT(s.cliente.id) from MedicalAppointment s where s.cliente.id = :cliente")
					.setParameter("cliente", med.getCliente().getId())
					.getSingleResult();
		if (res != null && (Long)res > 1) {
			subsecuencia = sainv_messages.get("medicalAppointmentDAO_subsecuente")  + res + ". "
		+ sainv_messages.get("medicalAppointmentDAO_fecCreac")+ " " + med.getCliente().getFechaCreacion().toString().substring(0, 10);
			
			return subsecuencia;
		} else
			subsecuencia=sainv_messages.get("medicalAppointmentDAO_nuevo") + ". "
					+ sainv_messages.get("medicalAppointmentDAO_fecCreac")+ " " + med.getCliente().getFechaCreacion().toString().substring(0, 10);
			return subsecuencia;
		}
		catch(Exception E){
			System.out.println(E.getMessage());
			subsecuencia=sainv_messages.get("medicalAppointmentDAO_error6");
			return subsecuencia;
		}
	}
	
	public boolean pacienteSubsecuente(Cliente cliente)
	{
		
		List<Integer> lsNm = new ArrayList<Integer>();
		lsNm = (List<Integer>) getEntityManager()
				.createQuery(
						"select COUNT(s.cliente.id) from MedicalAppointment s where s.cliente.id = :cliente")
				.setParameter("cliente", cliente.getId())
				.getSingleResult();
		
		if(lsNm.size()>0 && lsNm.get(0)>1)
		{
			System.out.println("Es subsecuente");
			return true;
			
		}
		else
		{
			System.out.println("NOO es subsecuente");
			return false;
		}
		
	}
	
	public Date ultimaConsultaCliente(Cliente cliente)
	{
		
		Date ultima=null;
		/*Integer ids;
		
		//try
			ids=(Integer)getEntityManager().createQuery("SELECT me.id FROM MedicalAppointment me where me.cliente.id="+cliente.getId()+" order by me.id desc").getResultList().get(1);
		System.out.println("Id consulta "+ ids);
		ultima=(Date) getEntityManager().createQuery("SELECT m.dateTime FROM MedicalAppointment m where m.id="+ids+" ").getSingleResult();*/
		try
		{
			ultima=(Date) getEntityManager().createQuery("SELECT m.dateTime FROM MedicalAppointment m where m.id=(SELECT MAX(me.id) FROM MedicalAppointment me where me.status=1 and me.cliente.id="+cliente.getId()+") ").getSingleResult();
		}
		catch(Exception e)
		{
			
		}
		
		return ultima;
	}
	
	public void agendarBloque(){
		try{
		System.out.println("Size de selMedAps "+selMedAps.size());
		
		for (MedicalAppointment med : selMedAps){
			if (!getEntityManager()
					.createQuery(
							"select s from MedicalAppointment s where s.dateTime = :date1 and s.doctor = :doctor")
					.setParameter("date1", med.getDateTime())
					.setParameter("doctor", getInstance().getDoctor())
					.getResultList().isEmpty()) {
				System.out.println("Entré a existen citas en las horas sel");
				FacesMessages.instance().add("No se puede crear bloque, existen citas en las horas seleccionadas");
				return;
			}
		}
		for (MedicalAppointment med : selMedAps){
			if (instance.getComment()!=null){
				med.setComment(instance.getComment());
				med.setSucursal(loginUser.getUser().getSucursal());
				med.setSucursal(loginUser.getUser().getSucursal());
				med.setStatus(5); // Bloque reservado
				getEntityManager().merge(med);
				getEntityManager().flush();
			}
		}
		
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void saveServices() {
	try{
		appointmentItems.clear();
		for (Service serv : servicios) {
			
			System.out.println("Tam servicios al guardar "+getInstance().getServiciosMedicos().size());
			
			MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
					getInstance().getId(), serv.getId());
			MedicalAppointmentService med = new MedicalAppointmentService();
			med.setMedicalAppointmentServiceId(id);
			med.setMedicalAppointment(getInstance());
			med.setService(serv);
			instance.getMedicalAppointmentServices().add(med);
			getEntityManager().merge(med);
		}
		getEntityManager().flush();
		
		//instance.setCliente(null); // Nuevo el 20/02/2017
		//setInstance(null); //nuevo 20/12/2016   comentado para prueba

		servicios.removeAll(servicios);  // nuevo el 20/02/2017
		servicios.clear(); //21/02/2017
		
	} catch (Exception e){
		System.out.println("Entré al catch de saveServices()");
		e.printStackTrace();
	}
	}
	
	public boolean saveModifyService()
	{
		try{
			appointmentItems.clear();
			
			List<Service> listaServicios;
			listaServicios=getEntityManager().createQuery("SELECT m.service FROM MedicalAppointmentService m where m.medicalAppointment.id="+getInstance().getId()+"").getResultList();
			boolean entro=false;
			
			
			for (Service serv : servicios) { //Lista de servicios modificados, entre ellos los que ya estaban  . --Electro 
				//1,2
				
				for(Service servAp: listaServicios) // Lista de servicios que ya se encontraban en la consulta --Electro, consulta /1
				{
					
					if(serv.getId().equals(servAp.getId()))
					{
						
						 entro=true;
						
					}
				}
				
				if(!entro)
				{
					MedicalAppointmentServiceId id = new MedicalAppointmentServiceId(
							getInstance().getId(), serv.getId());
					
					MedicalAppointmentService med = new MedicalAppointmentService();
					med.setMedicalAppointmentServiceId(id);
					med.setMedicalAppointment(getInstance());
					med.setService(serv);
					instance.getMedicalAppointmentServices().add(med);
					getEntityManager().merge(med);
					getEntityManager().flush();
					
				}
				entro=false;
			}
			
			
			entro=false;
			int indice=0;
			for (Service servAp: listaServicios) { //lista de servicios de la consulta
				
				//System.out.println("Entro a eliminar");

				for(Service serv : servicios) // lista de servicios seleccionados
				{
					
					if(serv.getId().equals(servAp.getId()))
					{
						
						entro=true; //Si se encuentra, asi que no hay que eliminar
						
					}
				}
				
				if(!entro)
				{
					
					MedicalAppointmentService servDelete=instance.getMedicalAppointmentServices().get(indice);
					instance.getMedicalAppointmentServices().remove(servDelete);
					//getEntityManager().merge(instance);
					//servicios.remove(servAp);
					//System.out.println("Servicio eliminado id "+ instance.getMedicalAppointmentServices().get(indice).getMedicalAppointmentServiceId());
					getEntityManager().remove(servDelete);
					getEntityManager().merge(instance);
					//getEntityManager().merge(instance);
					getEntityManager().flush();
				}
				
				indice++;
				entro=false;
			}
			
			//getEntityManager().flush();
			
			servicios.removeAll(servicios);
			//servicios.clear();
			setServicios(new ArrayList<Service>()); //nuevo el 23/02/2017
			
		} catch (Exception e){
			System.out.println("Entré al catch de saveModifyServices()");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void clearServices() {
		try{

				instance.setCliente(new Cliente()); // nuevo el 20/02/2017
				//instance.setCliente(null);//nuevo el 30/11/2016 // comentado el 20/02/2017  este null ya debio haberse realizado al finalizar la transaccion
				//servicios.removeAll(servicios);
				setServicios(new ArrayList<Service>()); //nuevo el 23/02/2017
				//getEntityManager().flush(); // comentado el 20/02/2017
				
				System.out.println("Entro al try vacio***"); // es posible dejar de utlizar esta funcion
				
			} catch (Exception e){
				System.out.println("No hay servicios cargados");
				e.printStackTrace();
			}
		}

	public boolean validateDate() {// valida que el paciente no tenga otra cita
		// en esa fecha
		if (!getEntityManager()
				.createQuery(
						"select s from MedicalAppointment s where s.dateTime = :date1 and s.cliente = :cliente")
				.setParameter("date1", getInstance().getDateTime())
				.setParameter("cliente", getInstance().getCliente())
				.getResultList().isEmpty()) {
			FacesMessages.instance().add(
					sainv_messages.get("medicalAppointmentDAO_error4"));
			return false;
		}
		return true;
	}

	public void cleanForNew() {
		System.out.println("Pasé por cleanForNew() de MedicalAppointmentDAO");
		selMedAps.clear();
		this.servicios.clear();
		getInstance().setCliente(null);
		getInstance().setClinicalHistory(null);
		getInstance().setComment(null);
		getInstance().setStatus(null);
		getInstance().setMedicalAppointmentServices(new ArrayList<MedicalAppointmentService>(0));
		//Cargamos los servicios medicos y examenes
		avaliableServs = getEntityManager().createQuery("SELECT s FROM Service s WHERE s.tipoServicio IN ('EXA', 'MED') AND s.estado = 'ACT' ").getResultList();
		
	}

	public Integer getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
	}

	public List<MedicalAppointmentService> getAppointmentItems() {
		return appointmentItems;
	}

	public void setAppointmentItems(
			List<MedicalAppointmentService> appointmentItems) {
		this.appointmentItems = appointmentItems;
	}

	public List<MedicalAppointmentService> getItemsNoBill() {
		return itemsNoBill;
	}

	public void setItemsNoBill(List<MedicalAppointmentService> itemsNoBill) {
		this.itemsNoBill = itemsNoBill;
	}

	public List<Service> getServicios() {
		return servicios;
	}

	public void setServicios(List<Service> servicios) {
		this.servicios = servicios;
	}
	
	public LoginUser getLoginUser() {
		return this.loginUser;
	}

	public List<Service> getAvaliableServs() {
		return avaliableServs;
	}

	public List<MedicalAppointment> getSelMedAps() {
		return selMedAps;
	}

	public void setSelMedAps(List<MedicalAppointment> selMedAps) {
		this.selMedAps = selMedAps;
	}

	public void setAvaliableServs(List<Service> avaliableServs) {
		this.avaliableServs = avaliableServs;
	}


	public Sucursal getSelectedSuc() {
		return selectedSuc;
	}

	public void setSelectedSuc(Sucursal selectedSuc) {
		this.selectedSuc = selectedSuc;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getComentStatus() {
		return comentStatus;
	}

	public void setComentStatus(String comentStatus) {
		this.comentStatus = comentStatus;
	}

	

	
	
	
	
	
	
}