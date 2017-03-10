package com.sa.kubekit.action.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.PersistenceException;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.i18n.KubeBundle;

public abstract class KubeDAO<E> extends EntityHome<E> {

	private static final long serialVersionUID = 1L;
	

	private ValueExpression<String> deletedLoggerMessage;
	private ValueExpression<String> createdLoggerMessage;
	private ValueExpression<String> updatedLoggerMessage;
	SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy");
	SimpleDateFormat hf = new SimpleDateFormat("h:m");
	
	private boolean enableAutoLogger = true;
	private boolean enableMessages = true;
	private String fechaFlt1;
	private String fechaFlt2;
	private String fechaFlt3;
	private Date fechaPFlt1;
	private Date fechaPFlt2;
	private String valCmb1 = "";
	private String valCmb2 = "";
	private String valCmb3 = "";
	private Calendar calendario = new GregorianCalendar();
	
	@In(required = true)
	protected KubeBundle kubekit_messages;

	@In(required = true)
	protected KubeBundle sainv_messages;
	
	public boolean comparaFechas(String fechaFlt, Date fechaComp) {
		boolean res = false;
		if(fechaFlt == null || fechaFlt.equals("") || new SimpleDateFormat("dd/MM/yyyy").format(fechaComp).contains(fechaFlt))
			res = true;
		return res;		
	}
	
	public void setFiltrosFecha() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		setFechaPFlt1(cal.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setFechaPFlt2(cal.getTime());
	}
	
	protected Date truncDate(Date dateTrunc, boolean endOfDay) {
		if(dateTrunc == null)
			return null;
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateTrunc);
		if(!endOfDay) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal.getTime();
	}
	
	public void setRangoMes() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setFechaPFlt1(truncDate(cal.getTime(), false));
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setFechaPFlt2(truncDate(cal.getTime(), true));
	}
	
	public void setRangoUlt30dias() {
		Calendar cal = new GregorianCalendar();
		setFechaPFlt2(truncDate(cal.getTime(), true));
		cal.add(Calendar.DATE, -30);
		setFechaPFlt1(truncDate(cal.getTime(), false));
	}
	
	public void setRangoUlt14dias() {
		Calendar cal = new GregorianCalendar();
		setFechaPFlt2(truncDate(cal.getTime(), true));
		cal.add(Calendar.DATE, -14);
		setFechaPFlt1(truncDate(cal.getTime(), false));
	}
	
	public void setRangoUlt7dias() {
		Calendar cal = new GregorianCalendar();
		setFechaPFlt2(truncDate(cal.getTime(), true));
		cal.add(Calendar.DATE, -7);
		setFechaPFlt1(truncDate(cal.getTime(), false));
	}
	
	public void setDiaActual() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, 0);
		setFechaPFlt2(truncDate(cal.getTime(), true));		
		setFechaPFlt1(truncDate(cal.getTime(), false));
		calendario= new GregorianCalendar();
		calendario.add(Calendar.DATE, 0);
	}
	
	public void setDiaAtras() {		
		calendario.add(Calendar.DATE, ( -1));
		setFechaPFlt2(truncDate(calendario.getTime(), true));	
		setFechaPFlt1(truncDate(calendario.getTime(), false));
	}
	
	public void setDiaAdelante() {
		calendario.add(Calendar.DATE, 1);
		setFechaPFlt2(truncDate(calendario.getTime(), true));		
		setFechaPFlt1(truncDate(calendario.getTime(), false));
	}
	public String filtrarFecha(Date fecha) {
		if(fecha != null)
			return df.format(fecha);
		else
			return "";
	}
	
	public String filtrarHora(Date fecha) {
		if(fecha != null)
			return hf.format(fecha);
		else
			return "";
	}
	
	public boolean comparaStrs(String cadenaIng, String cadenaComp) {
		return cadenaComp.toLowerCase().contains(cadenaIng.toLowerCase());
	}
	
	public boolean comparaFechas(Date fechaFlt, Date fechaComp) {
		boolean res = false;
		if(fechaFlt == null ||  
				new SimpleDateFormat("dd/MM/yyyy").format(fechaComp).equals(new SimpleDateFormat("dd/MM/yyyy").format(fechaFlt)))
			res = true;
		return res;		
	}
	
	public Double moneyDecimal(Float num) {
		return new Long(Math.round(num*100))/100.0;
	}
	
	public Double moneyDecimal(Double num) {
		return moneyDecimal(num.floatValue());
	}

	@Transactional
	public boolean save() {
		if (preSave())
			try {
				if (super.persist().equals("persisted")) {
					posSave();
					// Ingresa el log en la bitacora
				}
				if (!enableMessages) {
					FacesMessages.instance().clear();
				}
				return true;
			}catch (PersistenceException e) {
				FacesMessages.instance().add(Severity.ERROR,
						kubekit_messages.get("dao_error"));
				if(e.getMessage().contains("could not insert")){
					FacesMessages.instance().add(Severity.ERROR,
							kubekit_messages.get("dao_error_unique_constraint"));
				}
				e.printStackTrace();
			} catch (InvalidStateException eiv) {
				System.out.println("ENTRO AL CATCH CUANDO GUARDA LA INSTANCIA");
				InvalidValue[] iv = eiv.getInvalidValues();
				System.out.println("IV ES DIFERENTE DE NULL");
				if (iv.length > 0) {
					for (int i = 0; i < iv.length; i++) {
						System.out.println(iv[i].getPropertyName());
						System.out.println(iv[i].getValue());
						System.out.println(iv[i].getMessage());
					}
				}
			}
		return false;
	}

	@Transactional
	public boolean modify() {
		try {
			if (preModify())
				if (super.update().equals("updated")) {
					posModify();
					if (!enableMessages) {
						FacesMessages.instance().clear();
					}
					return true;
				}
		} catch (PersistenceException e) {
			debug("catch persistence ");
			FacesMessages.instance().add(Severity.ERROR,
					kubekit_messages.get("dao_error"));
			e.printStackTrace();
		} catch (InvalidStateException eiv) {
			System.out.println("ENTRO AL CATCH CUANDO GUARDA LA INSTANCIA");
			InvalidValue[] iv = eiv.getInvalidValues();
			System.out.println("IV ES DIFERENTE DE NULL");
			if (iv.length > 0) {
				for (int i = 0; i < iv.length; i++) {
					System.out.println(iv[i].getPropertyName());
					System.out.println(iv[i].getValue());
					System.out.println(iv[i].getMessage());
				}
			}
		}
		return false;
	}

	@Transactional
	public boolean delete() {
		try {
			if (preDelete())
				if (super.remove().equals("removed")) {
					posDelete();
					if (!enableMessages) {
						FacesMessages.instance().clear();
					}
					return true;
				}
		} catch (Exception e) {
			debug("catch exception ");
			FacesMessages.instance().add(Severity.ERROR,
					kubekit_messages.get("dao_error"));
			e.printStackTrace();
		}
		return false;
	}

	public void select(E instance) {
		this.setInstance(instance);
	}

	public void inspect(String o) {
		System.out.println(o);
	}

	public abstract boolean preSave();

	public abstract boolean preModify();

	public abstract boolean preDelete();

	public abstract void posSave();

	public abstract void posModify();

	public abstract void posDelete();

	public void clean() {
		clearInstance();
		setInstance(createInstance());
	}

	public ValueExpression<String> getDeletedLoggerMessage() {
		return deletedLoggerMessage;
	}

	public void setDeletedLoggerMessage(
			ValueExpression<String> deletedLoggerMessage) {
		this.deletedLoggerMessage = deletedLoggerMessage;
	}

	public ValueExpression<String> getCreatedLoggerMessage() {
		return createdLoggerMessage;
	}

	public void setCreatedLoggerMessage(
			ValueExpression<String> createdLoggerMessage) {
		this.createdLoggerMessage = createdLoggerMessage;
	}

	public ValueExpression<String> getUpdatedLoggerMessage() {
		return updatedLoggerMessage;
	}

	public void setUpdatedLoggerMessage(
			ValueExpression<String> updatedLoggerMessage) {
		this.updatedLoggerMessage = updatedLoggerMessage;
	}

	public boolean isEnableAutoLogger() {
		return enableAutoLogger;
	}

	public void setEnableAutoLogger(boolean enableAutoLogger) {
		this.enableAutoLogger = enableAutoLogger;
	}

	public boolean isEnableMessages() {
		return enableMessages;
	}

	public void setEnableMessages(boolean enableMessages) {
		this.enableMessages = enableMessages;
	}

	public String getFechaFlt1() {
		return fechaFlt1;
	}

	public void setFechaFlt1(String fechaFlt1) {
		this.fechaFlt1 = fechaFlt1;
	}

	public String getFechaFlt2() {
		return fechaFlt2;
	}

	public void setFechaFlt2(String fechaFlt2) {
		this.fechaFlt2 = fechaFlt2;
	}

	public String getFechaFlt3() {
		return fechaFlt3;
	}

	public void setFechaFlt3(String fechaFlt3) {
		this.fechaFlt3 = fechaFlt3;
	}

	public Date getFechaPFlt1() {
		return fechaPFlt1;
	}

	public void setFechaPFlt1(Date fechaPFlt1) {
		this.fechaPFlt1 = fechaPFlt1;
	}

	public Date getFechaPFlt2() {
		return fechaPFlt2;
	}

	public void setFechaPFlt2(Date fechaPFlt2) {
		this.fechaPFlt2 = fechaPFlt2;
	}

	public String getValCmb1() {
		return valCmb1;
	}

	public void setValCmb1(String valCmb1) {
		this.valCmb1 = valCmb1;
	}

	public String getValCmb2() {
		return valCmb2;
	}

	public void setValCmb2(String valCmb2) {
		this.valCmb2 = valCmb2;
	}

	public String getValCmb3() {
		return valCmb3;
	}

	public void setValCmb3(String valCmb3) {
		this.valCmb3 = valCmb3;
	}

	public Calendar getCalendario() {
		return calendario;
	}

	public void setCalendario(Calendar calendario) {
		this.calendario = calendario;
	}
	
}