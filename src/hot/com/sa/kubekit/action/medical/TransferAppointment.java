package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.selection.Selection;
import org.richfaces.model.selection.SimpleSelection;
import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.model.medical.Doctor;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.security.Sucursal;

@Name("transferAppointment")
@Scope(ScopeType.CONVERSATION)
public class TransferAppointment {
	private Doctor doctorSel;
	private Date dateSel;
	private Sucursal sucSel;
	private int initialHour;
	private int finalHour;

	@In(create = true)
	private MedicalAppointmentDAO medicalAppointmentDAO;

	@In(required = true)
	private KubeBundle sainv_messages;
	
	@In(required = true, create = false)
	private LoginUser loginUser;	

	@In(create = true)
	private DoctorDAO doctorDAO;

	private Selection selection = new SimpleSelection();

	private List<MedicalAppointment> listAppointments = new ArrayList<MedicalAppointment>(
			0);

	@In
	private EntityManager entityManager;

	@Create
	@Begin(join=true)
	public void init() {
		dateSel = new Date();
		initialHour = 8;
		finalHour = 19;
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

	@SuppressWarnings("unchecked")
	public void createGridDay() {
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
		cal.add(Calendar.MINUTE, 2);
		Date date2 = cal.getTime();
		listAppointments = new ArrayList<MedicalAppointment>(0);
		List<MedicalAppointment> listTemp = entityManager
				.createQuery(
						"select s from MedicalAppointment s where s.dateTime between :date1 and :date2 and s.doctor = :doctor order by s.dateTime ASC, s.status DESC")
				.setParameter("date1", date1).setParameter("date2", date2)
				.setParameter("doctor", doctorSel).getResultList();
		// creando la grilla
		int cont = initialHour;
		while (cont < finalHour) {
			// recorremos cada 30 minutos
			for (int i = 0; i < 2; i++) {
				searchAddAppointments(cont, i * 30, listTemp);
			}
			// incrementa la hora
			cont++;
		}
	}

	// agrega la cita medica encontrada para esa hora en caso de no encontrarla
	// crea una vacia para esa hora
	private void searchAddAppointments(int hour, int minute,
			List<MedicalAppointment> listTemp) {
		
		Calendar cal = Calendar.getInstance();
		for (MedicalAppointment med : listTemp) {
			cal.setTime(med.getDateTime());
			if (cal.get(Calendar.HOUR_OF_DAY) == hour
					&& cal.get(Calendar.MINUTE) == minute) {
				listAppointments.add(med);
				if (med.getStatus() != 2) {
					return;
				} /*else {
					break;
				}*/
			}
		}
		// crea nuevo
		MedicalAppointment med = new MedicalAppointment();
		med.setDoctor(doctorSel);
		med.setStatus(-1);
		cal.setTime(dateSel);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		med.setDateTime(cal.getTime());
		listAppointments.add(med);
	}

	// Método que valida que la hora seleccionada esté disponible para la transferencia
	public boolean validate(){
		Iterator<Object> it = selection.getKeys();
		if (it.hasNext()){
			MedicalAppointment tmpMed= listAppointments.get((Integer) it.next());
			if (tmpMed.getStatus() != -1 || getSucSel() == null){
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}
	
	public void takeSelection() {
		System.out.println("Selection size: "+ selection.size() );
		Iterator<Object> it = selection.getKeys();
		List <MedicalAppointment> med = new ArrayList<MedicalAppointment>();
		Integer prevNum = 0;
		Integer currNum= 0;
		while (it.hasNext()) {
			currNum= (Integer)it.next();
			//validamos que los slots sean consecutivos.
			if (prevNum!= 0 && prevNum!=(currNum - 1)){
				System.out.println("Entré a lista no consecutiva");
				FacesMessages.instance().add(Severity.WARN,"Los slots seleccionados no son consecutivos");
				return;
			}
			System.out.println("*** num = " + currNum + " \n");
			med.add(listAppointments.get(currNum));	
			prevNum = currNum;
		}
		if (med.get(0).getId() == null)
			medicalAppointmentDAO.select(med.get(0).clone());
		else
			medicalAppointmentDAO.select(med.get(0));
		medicalAppointmentDAO.setSelMedAps(med);
	}

	public void registerNew() {
		if (medicalAppointmentDAO.getInstance().getCliente() == null){
			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("medicalAppointmentDAO_nopat"));
			return;
		}
		if (medicalAppointmentDAO.save()) {
			createGridDay();
		}
	}

	public void modifyAppointment() {
		medicalAppointmentDAO.modify();
		/* if (medicalAppointmentDAO.getInstance().getStatus() == 2) {
			medicalAppointmentDAO.delete();
			FacesMessages.instance().clear();
			FacesMessages.instance().add(
					sainv_messages.get("medicalAppointmentDAO_msg_cancel"));
		}*/
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

	public Sucursal getSucSel() {
		return sucSel;
	}

	public void setSucSel(Sucursal sucSel) {
		this.sucSel = sucSel;
	}
}
