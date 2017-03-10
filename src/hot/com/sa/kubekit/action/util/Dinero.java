package com.sa.kubekit.action.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Dinero implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long valor;
	
	public Dinero(Float n) {
		valor = new Long(Math.round(n*100));
	}
	
	public Dinero(Double n) {
		valor = new Long(Math.round(n*100));
	}
	
	public Dinero(BigDecimal n) {
		valor = new Long(Math.round(n.doubleValue()*100));
	}
	
	public void setVal(Float n) {
		valor = new Long(Math.round(n*100));
	}
	
	public void setVal(Double n) {
		valor = new Long((long)(n*100));
	}
	
	public void setVal(BigDecimal n) {
		valor = new Long(Math.round(n.doubleValue()*100));
	}
	
	public Double getVal() {
		return (double) (valor/100);
	}
	
	@Override
	public String toString() {
		return new DecimalFormat("##########0.00").format(valor/100.0).trim();
    }
	
	public String getValStr() {
		return new DecimalFormat("##########0.00").format(valor/100.0).trim();
	}
	
	public static void main(String[] args) {
		Calendar fch1 = new GregorianCalendar(), fch2 = new GregorianCalendar();
		//fch1.add(Calendar.DATE, 2);		
		fch1.add(Calendar.DATE, (-1)*(fch1.get(Calendar.DAY_OF_WEEK) - 2));
		System.out.println( fch1.getTime() );
		fch2.add(Calendar.DATE, 8 - fch2.get(Calendar.DAY_OF_WEEK));
		System.out.println( fch2.getTime() );
				
	}
}
