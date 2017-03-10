package com.sa.kubekit.action.medical;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;
import org.richfaces.model.selection.Selection;
import org.richfaces.model.selection.SimpleSelection;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.util.KubeSearcher;
import com.sa.model.medical.Doctor;
import com.sa.model.medical.MedicalAppointment;

@Name("medicalAppointmentSearch")
@Scope(ScopeType.CONVERSATION)
public class MedicalAppointmentSearch extends KubeSearcher<MedicalAppointment> {

	private Date date1;
	private Date date2;
	private Integer status;
	private Doctor doctor;
	private Selection selection = new SimpleSelection();

	@In(create = true)
	private MedicalAppointmentDAO medicalAppointmentDAO;

	@In(create = true)
	private DoctorDAO doctorDAO;

	@In(required = true)
	private KubeBundle sainv_messages;
	
	@Create
	@Begin(join=true)
	public void init(){
		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void searchImpl() {
		String jpql = "select distinct m from MedicalAppointment m ";
		String order = " order by m.dateTime asc";
		boolean aux = true;

		if (status != null) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
			jpql += "m.status = " + status + " ";
		}

		/* Si el usuario es doctor solo podra consultar sobre sus citas */
		if (Identity.instance().hasRole("doctor")) {
			doctor = doctorDAO.doctorInSession();
		}

		if (doctor != null) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
			jpql += "(m.doctor.id = "
					+ doctor.getId()
					+ ") ";
		}

		if (date1 == null && date2 == null) {
			setResultList(entityManager.createQuery(jpql + order)
					.getResultList());
		} else {

			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";

			jpql += "m.dateTime between :date1 and :date2 ";

			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();

			if (date1 == null) {
				cal1.set(Calendar.YEAR, 1900);
				cal1.set(Calendar.MONTH, 1);
				cal1.set(Calendar.DAY_OF_MONTH, 1);
			} else
				cal1.setTime(date1);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);

			if (date2 == null)
				cal2.setTime(new Date());
			else
				cal2.setTime(date2);

			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);

			setResultList(entityManager.createQuery(jpql + order).setParameter(
					"date1", cal1.getTime()).setParameter("date2",
					cal2.getTime()).getResultList());
		}


		if (getResultList().isEmpty()) {
			FacesMessages.instance().add(
					sainv_messages.get("sched_search_msg1"));
		}

	}

	@Override
	protected boolean validateParams() {
		return true;
	}

	public void takeSelection() {
		Iterator<Object> it = selection.getKeys();
		if (it.hasNext()) {
			Integer num = (Integer) it.next();
			MedicalAppointment med = getResultList().get(num);
			medicalAppointmentDAO.select(med);
		}
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Selection getSelection() {
		return selection;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

}