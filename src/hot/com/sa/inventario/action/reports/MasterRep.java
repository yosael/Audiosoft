package com.sa.inventario.action.reports;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class MasterRep {
	
	private final String appCtx = "/sainv/rep";
	protected Date fechaInicio;
	protected Date fechaFin;
	protected Date fechaCurrent;
	private Date fechaPFlt1;
	private Date fechaPFlt2;
	private Date fechaPFlt3;
	private Date fechaPFlt4;
	private String valCmb1 = "";
	private String valCmb2 = "";
	private String valCmb3 = "";
	private String valCmb4 = "";
	private String valCmb5 = "";
	private Object fltObj1 = null;
	private Object fltObj2 = null;
	private Integer totInt1;
	private Integer totInt2;
	private Float totDec1;
	private Float totDec2;
	private boolean flag1;
	private boolean flag2;
	protected final File imageLogo = new File("/movi/kubeImg/logoCliRep.jpg");
	SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy");
	SimpleDateFormat hf = new SimpleDateFormat("h:m");
	
	protected void resetMainClass() {
		fechaInicio = null;
		fechaFin = null;
		fechaCurrent = null;
		fechaPFlt1 = null;
		fechaPFlt2 = null;
		fechaPFlt3 = null;
		fechaPFlt4 = null;
		valCmb1 = "";
		valCmb2 = "";
		valCmb3 = "";
		valCmb4 = "";
		valCmb5 = "";
		fltObj1 = null;
		fltObj2 = null;
		totInt1 = 0;
		totInt2 = 0;
		totDec1 = 0f;
		totDec2 = 0f;
		flag1 = false;
		flag2 = false;
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
	
	public void setRangoHoy() {
		setFechaInicio(truncDate(new Date(), false));
		setFechaFin(truncDate(new Date(), true));
	}
	
	public void setRangoSemana() {
		setFechaInicio(new Date());
		setFechaFin(new Date());
	}
	
	public void setRangoMes() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setFechaInicio(truncDate(cal.getTime(), false));
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setFechaFin(truncDate(cal.getTime(), true));
	}
	
	protected String mes;
	protected Integer anio;

	public boolean comparaFechas(String fechaFlt, Date fechaComp) {
		boolean res = false;
		if(fechaFlt == null || fechaFlt.equals("") || new SimpleDateFormat("dd/MM/yyyy").format(fechaComp).contains(fechaFlt))
			res = true;
		return res;		
	}
	
	
	protected Date resetTimeDate(Date fecha, int tipoReset) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(fecha);
		if(tipoReset == 1) { //Ponerlo a 00:01
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		} else { //Ponerlo a 23:59
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 0);
		}
		
		return cal.getTime();
	}
	
	public Double moneyDecimal(Float num) {
		return new Long(Math.round(num*100))/100.0;
	}
	
	public Double moneyDecimal(Double num) {
		return moneyDecimal(num.floatValue());
	}
	
	
	public boolean comparaFechas(Date fechaFlt, Date fechaComp) {
		boolean res = false;
		if(fechaFlt == null ||  
				new SimpleDateFormat("dd/MM/yyyy").format(fechaComp).equals(new SimpleDateFormat("dd/MM/yyyy").format(fechaFlt)))
			res = true;
		return res;		
	}
	
	protected List getFilteredList(EntityManager em, String hqlQuery, 
									String hqlCond, String hqlOrder, List<Map> params ) {
		List resultList = new ArrayList();
		List<Map> setParams = new ArrayList<Map>();
		//Iteramos a traves de las condiciones para evaluar si los parametros traen algun valor o son nulos
		for(Map tmpParam : params) {
			if(!(tmpParam.get("valor") instanceof String)) {
				if(tmpParam.get("valor") != null) {
					setParams.add(tmpParam);
					hqlCond += tmpParam.get("condicion").toString();
				}	
			} else if(tmpParam.get("valor") != null && !tmpParam.get("valor").toString().trim().equals("")) {
				setParams.add(tmpParam);
				hqlCond += tmpParam.get("condicion").toString();
			}	
		}
		Query qr = em.createQuery(hqlQuery + " " + hqlCond + " " + hqlOrder);
		for(Map tmpParam : setParams) 
			qr.setParameter(tmpParam.get("nomParam").toString(), tmpParam.get("valor"));
		
		resultList = qr.getResultList();
		
		return resultList;
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
		}
		return cal.getTime();
	}

	public String getAppCtx() {
		return appCtx;
	}

	 public ByteArrayInputStream getImageLogo() throws Exception {
		 	byte[] data = getBytes();
	        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        try {
	            BufferedImage image = ImageIO.read(inStream);
	            ImageIO.write(image, "jpeg", outStream);
	            outStream.close();
	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	        return new ByteArrayInputStream(outStream.toByteArray());
	     }
	
	
	
	public byte[] getBytes() throws Exception {

        File logoFile = new File("/sainv/kubeImg/logoCliRep.jpg");
        InputStream is = new FileInputStream(logoFile);

        // Get the size of the file
        long length = logoFile.length();


        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+logoFile.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
	
	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
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

	public Integer getTotInt1() {
		return totInt1;
	}

	public void setTotInt1(Integer totInt1) {
		this.totInt1 = totInt1;
	}

	public Integer getTotInt2() {
		return totInt2;
	}

	public void setTotInt2(Integer totInt2) {
		this.totInt2 = totInt2;
	}

	public Float getTotDec1() {
		return totDec1;
	}

	public void setTotDec1(Float totDec1) {
		this.totDec1 = totDec1;
	}

	public Float getTotDec2() {
		return totDec2;
	}

	public void setTotDec2(Float totDec2) {
		this.totDec2 = totDec2;
	}

	public String getValCmb3() {
		return valCmb3;
	}

	public void setValCmb3(String valCmb3) {
		this.valCmb3 = valCmb3;
	}

	public Date getFechaPFlt3() {
		return fechaPFlt3;
	}

	public void setFechaPFlt3(Date fechaPFlt3) {
		this.fechaPFlt3 = fechaPFlt3;
	}

	public Date getFechaPFlt4() {
		return fechaPFlt4;
	}

	public void setFechaPFlt4(Date fechaPFlt4) {
		this.fechaPFlt4 = fechaPFlt4;
	}

	public String getValCmb4() {
		return valCmb4;
	}

	public void setValCmb4(String valCmb4) {
		this.valCmb4 = valCmb4;
	}

	public boolean isFlag1() {
		return flag1;
	}

	public void setFlag1(boolean flag1) {
		this.flag1 = flag1;
	}

	public boolean isFlag2() {
		return flag2;
	}

	public void setFlag2(boolean flag2) {
		this.flag2 = flag2;
	}

	public Date getFechaCurrent() {
		return fechaCurrent;
	}

	public void setFechaCurrent(Date fechaCurrent) {
		this.fechaCurrent = fechaCurrent;
	}
	
	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public Object getFltObj1() {
		return fltObj1;
	}

	public void setFltObj1(Object fltObj1) {
		this.fltObj1 = fltObj1;
	}

	public Object getFltObj2() {
		return fltObj2;
	}

	public void setFltObj2(Object fltObj2) {
		this.fltObj2 = fltObj2;
	}

	public String getValCmb5() {
		return valCmb5;
	}

	public void setValCmb5(String valCmb5) {
		this.valCmb5 = valCmb5;
	}
	
	
}