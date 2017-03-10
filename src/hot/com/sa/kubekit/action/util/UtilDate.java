package com.sa.kubekit.action.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

//version 1
@Name("utilDate")
@Scope(ScopeType.CONVERSATION)
public class UtilDate {
	private String dateFormat = "yyyy-MM-dd";
	private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private String timeFormat = "HH:mm:ss";
	private static final long msDay = 24 * 60 * 60 * 1000;

	// calcular dias/a�os:
	public long cDias(int sDay, int sMonth, int sYear, int fDay, int fMonth,
			int fYear) {
		Calendar calendarini = new GregorianCalendar(sYear, sMonth - 1, sDay);
		Calendar calendarfin = new GregorianCalendar(fYear, fMonth - 1, fDay);
		java.sql.Date fechaini = new java.sql.Date(calendarini
				.getTimeInMillis());
		java.sql.Date fechafin = new java.sql.Date(calendarfin
				.getTimeInMillis());
		long days = (fechafin.getTime() - fechaini.getTime()) / msDay;
		return days;
	}

	// convierte fecha
	public String convertDate(Date dat) {
		if (dat == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(dat);
	}

	// convierte fecha y hora
	public String convertDateTime(Date dat) {
		if (dat == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
		return format.format(dat);
	}

	// convierte hora
	public String convertTime(Date dat) {
		if (dat == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat(timeFormat);
		return format.format(dat);
	}

	@SuppressWarnings("deprecation")
	public static String cronFormat(Date fechaReserva) {
		String cron = "";
		cron += fechaReserva.getSeconds() + " ";
		cron += fechaReserva.getMinutes() + " ";
		cron += fechaReserva.getHours() + " ";
		cron += (fechaReserva.getDate()) + " ";
		cron += (fechaReserva.getMonth() + 1) + " ";
		cron += "?";
		return cron;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

}
