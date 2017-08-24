 package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.selection.Selection;
import org.richfaces.model.selection.SimpleSelection;
import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.model.medical.Doctor;
import com.sa.model.medical.MedicalAppointment;

@Name("appointmentGrid")
@Scope(ScopeType.CONVERSATION)//@Scope(ScopeType.SESSION)
public class AppointmentGrid {
	private Doctor doctorSel;
	private Date dateSel;
	private int initialHour;
	private int finalHour;
	private boolean estado;
	private boolean btnCargar=false;
	private Integer idDoctor=null;
	private boolean cerrarModal=false;

	@In(create = true)
	private MedicalAppointmentDAO medicalAppointmentDAO;

	@In(required = true)
	private KubeBundle sainv_messages;
	
	@In(required = true, create = false)
	private LoginUser loginUser;

	@In(create = true)
	private DoctorDAO doctorDAO;

	@In(create = true,required = false, value = "transferAppointment")
	@Out
	private TransferAppointment transferApp;
	
	private Selection selection = new SimpleSelection();

	private List<MedicalAppointment> listAppointments = new ArrayList<MedicalAppointment>();

	@In
	private EntityManager entityManager;
	
	
	
	
	/*@PostConstruct
	public void initialize()
	{
		conversation.setTimeout(1200000);
		System.out.println("TIMEOUT DENTRO DE INIT2 "+Conversation.instance().getTimeout());	
		System.out.println("TIMEOUT ELEMENTO CREADO "+this.conversation.getTimeout());
	}*/

	@Create
	@Begin(join=true)
	public void init() {
		System.out.println("Entro a init");
		dateSel = new Date();	
		initialHour = 8;
		finalHour = 20;
		
		//System.out.println("TIMEOUT DE CONVERSATION: "+Conversation.instance().getTimeout());
		
		
		//doctorSel = loginUser.getUser().getDoctorReg();
		/*
		if (Identity.instance().hasRole("doctor")) {
			doctorSel = doctorDAO.doctorInSession();
		} else {
			DoctorQuery doctorQuery = (DoctorQuery) Component
					.getInstance(DoctorQuery.class);
			if (!doctorQuery.getResultList().isEmpty()) {
				doctorSel = doctorQuery.getResultList().get(0);
			}
		}
		createGridDay();
		*/
	}
	
	public void setDoctorSesion() {
		if (loginUser.getUser().getDoctorReg() != null) {
			doctorDAO.setNumId(loginUser.getUser().getDoctorReg().getId());
			doctorDAO.load();
			doctorSel = doctorDAO.getInstance();
			createGridDay();
		}
	}
	
	public void loadDoctorSched(Doctor selDoc) {
		doctorDAO.setNumId(selDoc.getId());
		doctorDAO.load();
		doctorSel = doctorDAO.getInstance();
		createGridDay();
	}
	
	public void loadDoctorSchedIdDoc() {
		
		btnCargar=true;
		
		System.out.println("idDoctor");
		//entityManager.createQuery("SELECT doc FROM Doctor where doc.id=:idDoc").setParameter("idDoc", selDoc).get;
		
		doctorDAO.setNumId(idDoctor);
		doctorDAO.load();
		doctorSel = doctorDAO.getInstance();
		createGridDay();
		
		idDoctor=null;
	}

	@SuppressWarnings("unchecked")
	public void createGridDay() 
	{
		System.out.println("Entre a createGridDay");
		
		if (dateSel == null || doctorSel == null)
			return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateSel);
		cal.set(Calendar.HOUR_OF_DAY, initialHour);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, -1);
		Date date1 = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, finalHour);
		cal.add(Calendar.MINUTE, 4);
		Date date2 = cal.getTime();
		listAppointments = new ArrayList<MedicalAppointment>();
		List<MedicalAppointment> listTemp = entityManager
				.createQuery(
						"select s from MedicalAppointment s where s.dateTime between :date1 and :date2 and s.doctor = :doctor order by s.dateTime ASC, s.status DESC")
				.setParameter("date1", date1).setParameter("date2", date2)
				.setParameter("doctor", doctorSel).getResultList();
		
		// creando la grilla
		int cont = initialHour;
		while (cont < finalHour) {
			// recorremos cada 30 minutos
			for (int i = 0; i < 4; i++) {
				searchAddAppointments(cont, i * 15, listTemp);
			}
			// incrementa la hora
			cont++;
		}
		
		//idDoctor=null; //nuevo el 30/01/2017
	}
	
	public String rediRect()
	{
		medicalAppointmentDAO.getInstance().setCliente(null);  //Probar dps
		//medicalAppointmentDAO.setInstance(null);
		medicalAppointmentDAO.clearInstance();
		System.out.println("Redieccionar");
		return "/medical/medicalAppointment/scheduler.xhtml";
	}
	
	public String rediRect2()
	{
		medicalAppointmentDAO.getInstance().setCliente(null);  //Probar dps
		//medicalAppointmentDAO.setInstance(null);
		medicalAppointmentDAO.clearInstance();
		System.out.println("Redieccionar");
		return "/medical/scheddoc/scheduler.xhtml";
	}

	// agrega la cita medica encontrada para esa hora en caso de no encontrarla
	// crea una vacia para esa hora
	private void searchAddAppointments(int hour, int minute, List<MedicalAppointment> listTemp) {
		Calendar cal = Calendar.getInstance();
		for (MedicalAppointment med : listTemp) {
			cal.setTime(med.getDateTime());
			if (cal.get(Calendar.HOUR_OF_DAY) == hour
					&& cal.get(Calendar.MINUTE) == minute) {
				listAppointments.add(med);
				if (med.getStatus()!=null && med.getStatus() != 2) {
					return;
				} /*else {
					break;
				}*/
			}
		}
		// crea nuevo
		MedicalAppointment med = new MedicalAppointment();
		med.setDoctor(doctorSel);
		med.setSucursal(null);
		med.setCliente(null);
		med.setStatus(-1);
		cal.setTime(dateSel);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		med.setDateTime(cal.getTime());
		listAppointments.add(med);
	}
	public void transferAppointment(){
		try{
			takeSelection();
			Iterator<Object> it = transferApp.getSelection().getKeys();
			List <MedicalAppointment> med = new ArrayList<MedicalAppointment>();
			if (it.hasNext())
			{
				med.add(transferApp.getListAppointments().get((Integer)it.next()));
				
				//System.out.println("Doctor a transferir: "+ med.get(0).getDoctor() + " Sucursal: "+med.get(0).getSucursal() +  " Fecha: "+ med.get(0).getDateTime());
				
				medicalAppointmentDAO.getInstance().setDoctor(med.get(0).getDoctor());
				medicalAppointmentDAO.getInstance().setSucursal(transferApp.getSucSel());
				medicalAppointmentDAO.getInstance().setDateTime(med.get(0).getDateTime());
				
				//System.out.println("(MedicalAppointmentDAO) Doctor a transferir: "+ medicalAppointmentDAO.getInstance().getDoctor() + " Sucursal: "+ medicalAppointmentDAO.getInstance().getSucursal() +  " Fecha: "+ medicalAppointmentDAO.getInstance().getDateTime() );
				
				medicalAppointmentDAO.modify();
				createGridDay();
				transferApp.createGridDay();
				FacesMessages.instance().add(Severity.INFO,"Se transfirió la cita correctamente");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void revertirCambios(){
		Iterator<Object> it = selection.getKeys();
		List <MedicalAppointment> med = new ArrayList<MedicalAppointment>();
		while (it.hasNext()) {
			Integer num = (Integer)it.next();
			med.add(listAppointments.get(num));	
		}
		for (MedicalAppointment tmpMed : med){
			tmpMed.setCliente(null);
			tmpMed.setComment(null);
			tmpMed.setMedicalAppointmentServices(null);
			tmpMed.setStatus(-1);
			tmpMed.setSucursal(null);
			
		}
		medicalAppointmentDAO.getInstance().setCliente(null);
		medicalAppointmentDAO.getInstance().setSucursal(null);
		medicalAppointmentDAO.getInstance().setComment(null);
	}
	
	public void takeSelection() {
		
		//System.out.println("Selection size: "+ selection.size() );
	
		Iterator<Object> it = selection.getKeys();
		List <MedicalAppointment> med = new ArrayList<MedicalAppointment>();
		
		while (it.hasNext()) {
			Integer num = (Integer)it.next();
			//System.out.println("*** num = " + num + " \n");
			med.add(listAppointments.get(num));	
		}
		
		if (med.get(0).getId() == null){
			
			medicalAppointmentDAO.select(med.get(0));
			//System.out.println("id de la cita medica if"+med.get(0).getId());
		}
		else
		{
			medicalAppointmentDAO.select(med.get(0));
			medicalAppointmentDAO.setAppointmentId(med.get(0).getId());
			//System.out.println("id de la cita medica else "+med.get(0).getId());
			
		}
		
		medicalAppointmentDAO.setSelMedAps(med); 
	}

	public void registerNew() {
		
		try{
			/*
			if (!medicalAppointmentDAO.validateSucursal()){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(sainv_messages.get("medicalAppointmentDAO_nosuc"));
				//revertirCambios();
				return;	
			} else {
				medicalAppointmentDAO.getInstance().setSucursal(
				medicalAppointmentDAO.getSelectedSuc());
			}
			*/
			
			if(doctorSel!=null)
				medicalAppointmentDAO.getInstance().setDoctor(doctorSel);
			
			
			if (!medicalAppointmentDAO.validateSucursal() && medicalAppointmentDAO.getInstance().getSucursal() == null){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(sainv_messages.get("medicalAppointmentDAO_nosuc"));
				//revertirCambios();
				return;	
			} else {				
				medicalAppointmentDAO.getInstance().setSucursal(
				medicalAppointmentDAO.getSelectedSuc());
			}
			if (medicalAppointmentDAO.getInstance().getCliente() == null){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(Severity.WARN,sainv_messages.get("medicalAppointmentDAO_nopat"));
				//revertirCambios(); 
				return;
			}
			if (medicalAppointmentDAO.getServicios().isEmpty()){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un servicio");
				//revertirCambios(); 
				return;
			}
			
			if (medicalAppointmentDAO.save()) {
				//medicalAppointmentDAO.getInstance().setCliente(null);// nuevo el 20/02/2017 riesgo de nullpointerexception  /probar sino da error, sino quitar
				createGridDay();
				//medicalAppointmentDAO.clearServices();	
			}	
			
			
			medicalAppointmentDAO.clean();
			cerrarModal=true;
			
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("No guardo cita");
		}
	}
	
	public void editAppointment() {
		
		try{
			/*
			if (!medicalAppointmentDAO.validateSucursal()){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(sainv_messages.get("medicalAppointmentDAO_nosuc"));
				//revertirCambios();
				return;	
			} else {
				medicalAppointmentDAO.getInstance().setSucursal(
				medicalAppointmentDAO.getSelectedSuc());
			}
			*/
			if (!medicalAppointmentDAO.validateSucursal() && medicalAppointmentDAO.getInstance().getSucursal() == null){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(sainv_messages.get("medicalAppointmentDAO_nosuc"));
				//revertirCambios();
				return;	
			} else {				
				medicalAppointmentDAO.getInstance().setSucursal(
				medicalAppointmentDAO.getSelectedSuc());
			}
			if (medicalAppointmentDAO.getInstance().getCliente() == null){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(sainv_messages.get("medicalAppointmentDAO_nopat"));
				//revertirCambios(); 
				return;
			}
			
			if (medicalAppointmentDAO.getServicios().isEmpty()){
				FacesMessages.instance().clear();
				FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un servicio");
				//revertirCambios(); 
				return;
			}
			
			if (medicalAppointmentDAO.saveModifyService()) {
				
				
				
				createGridDay();
				
				//medicalAppointmentDAO.clearServices();	
				medicalAppointmentDAO.clearInstance(); //nuevo el 21/02/2017
				
				cerrarModal=true;
			}
			cerrarModal=true;
			
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("No guardo cita");
		}
	}

	public void modifyAppointment() {
		
		medicalAppointmentDAO.modify();
		if (medicalAppointmentDAO.getInstance().getStatus() == 2) {
			medicalAppointmentDAO.delete();
			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("medicalAppointmentDAO_msg_cancel"));
		}
		System.out.println("*** entro a modificar el estado");
		createGridDay();
	}

	// valida si se puede agendar acorde a la fecha del sistema (muestra o no el
	// boton)
	public boolean validateDate() {
		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.set(Calendar.MINUTE, 0);
		calToday.add(Calendar.MINUTE, -1);
		Calendar calSel = Calendar.getInstance();
		calSel.setTime(dateSel);
		return calSel.after(calToday);
	}
	
	
	//Necesario crear correo para enviar las notificaciones
	public Properties definirPropiedadesCorreo()
	{
		Properties props = new Properties();
		 
		props.put("mail.smtp.host","mail.lineadecodigo.com");
		props.put("mail.transport.protocol","smtp");
		props.put("mail.smtp.auth", "true");
		props.setProperty("mail.user", "myuser");
		props.setProperty("mail.password", "mypwd");
		
		return props;
	}
	
	public void enviarAgendaCorreo()
	{
		//Autent pwd = new Autentificacion();
		Session emailSession =Session.getInstance(definirPropiedadesCorreo(), null);
		Message msg = new MimeMessage(emailSession);
		
		
		try {
			
			msg.setSubject("Agenda");
			msg.setFrom(new InternetAddress("web@lineadecodigo.com","Línea de Código"));
			msg.addRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("miamigo@gmail.com") });
			
			// Asignamos el contenido HTML, tan grande como nosotros queramos 
			msg.setContent("<h1>El mensaje de nuestro primer correo HTML</h1>","text/html" );
			
			DataHandler dh = new DataHandler("Texto del mensaje","text/plain");
			msg.setDataHandler(dh);
			
			javax.mail.Transport.send(msg);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	public Selection getSelection() {
		return selection;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public Doctor getDoctorSel() {
		return doctorSel;
	}

	public void setDoctorSel(Doctor doctorSel) {
		this.doctorSel = doctorSel;
	}

	public Date getDateSel() {
		return dateSel;
	}

	public void setDateSel(Date dateSel) {
		this.dateSel = dateSel;
	}

	public List<MedicalAppointment> getListAppointments() {
		return listAppointments;
	}

	public void setListAppointments(List<MedicalAppointment> listAppointments) {
		this.listAppointments = listAppointments;
	}

	public int getInitialHour() {
		return initialHour;
	}

	public void setInitialHour(int initialHour) {
		this.initialHour = initialHour;
	}

	public int getFinalHour() {
		return finalHour;
	}

	public void setFinalHour(int finalHour) {
		this.finalHour = finalHour;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public boolean isBtnCargar() {
		return btnCargar;
	}

	public void setBtnCargar(boolean btnCargar) {
		this.btnCargar = btnCargar;
	}

	public Integer getIdDoctor() {
		return idDoctor;
	}

	public void setIdDoctor(Integer idDoctor) {
		this.idDoctor = idDoctor;
	}

	public boolean isCerrarModal() {
		return cerrarModal;
	}

	public void setCerrarModal(boolean cerrarModal) {
		this.cerrarModal = cerrarModal;
	}


		
	
	
	
	
}