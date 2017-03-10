package com.sa.kubekit.action.acct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.acct.AsientoContDet;
import com.sa.model.acct.AsientoContable;
import com.sa.model.acct.CuentaContable;

@SuppressWarnings("unchecked")
@Name("libroDiaMayHome")
@Scope(ScopeType.CONVERSATION)
public class LibroDiaMayHome extends KubeDAO<AsientoContable>{

	private static final long serialVersionUID = 1L;
	private List<AsientoContable> resultList = new ArrayList<AsientoContable>();
	private List<AsientoContDet> libroDiario = new ArrayList<AsientoContDet>();
	private List<CuentaContable> libroMayorAct = new ArrayList<CuentaContable>();
	private List<CuentaContable> libroMayorPsv = new ArrayList<CuentaContable>();
	private Date fechaLibro;
	private String tipoCuenta;
	private Date fechaIni;
	private Date fechaFin;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("libdm_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("libdm_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("libdm_deleted")));
		fechaLibro = new Date();
	}
	
	public void genLibroDiario() {
		Calendar fch1, fch2;
		fch1 = new GregorianCalendar(); fch1.setTime(fechaLibro);
		fch2 = new GregorianCalendar(); fch2.setTime(fechaLibro);
		fch1.set(Calendar.HOUR_OF_DAY, 0);  fch1.set(Calendar.MINUTE, 0);  
		fch1.set(Calendar.SECOND, 0);  fch1.set(Calendar.MILLISECOND, 0);  
		fch2.set(Calendar.HOUR_OF_DAY, 23);  fch2.set(Calendar.MINUTE, 59);  
		fch2.set(Calendar.SECOND, 59);  fch2.set(Calendar.MILLISECOND, 0);  
		//Obtenemos todos los asientos contables del dia
		resultList = getEntityManager()
				.createQuery("SELECT a FROM AsientoContable a " +
						"	WHERE a.fechaAsiento >= :fch1 " +
						"	AND a.fechaAsiento <= :fch2 " +
						"		ORDER BY a.fechaAsiento ASC ")
				.setParameter("fch1", fch1.getTime())
				.setParameter("fch2", fch2.getTime())
				.getResultList();
		//Sacamos por separado lo del activo del pasivo
		for(AsientoContable tmpAsi : resultList) {
			tmpAsi.setDetalleActivo(new ArrayList<AsientoContDet>());
			tmpAsi.setDetallePasivo(new ArrayList<AsientoContDet>());
			for(AsientoContDet tmpDet : tmpAsi.getDetalleAsiento()) {
				if(tmpDet.getCuenta().getTipoCuenta().getCodigo().equals("AC"))
					tmpAsi.getDetalleActivo().add(tmpDet);
				else
					tmpAsi.getDetallePasivo().add(tmpDet);
			}
		}
	}
	
	public void genLibroMayor() {
		libroMayorAct = new ArrayList<CuentaContable>();
		libroMayorPsv = new ArrayList<CuentaContable>();
		String hqlQuery = "SELECT c FROM CuentaContable c WHERE 1 = 1 ";
		Calendar fch1 = new GregorianCalendar(), fch2 = new GregorianCalendar();
		if(fechaIni != null) {
			fch1.setTime(fechaIni);
			fch1.set(Calendar.HOUR_OF_DAY, 0);  fch1.set(Calendar.MINUTE, 0);  
			fch1.set(Calendar.SECOND, 0);  fch1.set(Calendar.MILLISECOND, 0);
		}
		
		if(fechaFin != null) {
			fch2.setTime(fechaFin);
			fch2.set(Calendar.HOUR_OF_DAY, 23);  fch2.set(Calendar.MINUTE, 59);  
			fch2.set(Calendar.SECOND, 59);  fch2.set(Calendar.MILLISECOND, 0);  
		}
		
		//Obtenemos todas las cuentas para sacarles el detalle a cada una
		if(!(tipoCuenta == null || tipoCuenta.trim().equals("")))
			hqlQuery += " AND c.tipoCuenta.codigo = :tpc ";
		
		Query qr = getEntityManager().createQuery(hqlQuery);
		if(!(tipoCuenta == null || tipoCuenta.trim().equals("")))
			qr.setParameter("tpc", tipoCuenta);
		
		List<CuentaContable> libroMayor = qr.getResultList();
		hqlQuery = "SELECT a FROM AsientoContDet a " +
				"	WHERE a.cuenta = :cta " +
				"	ORDER BY a.asiento.fechaAsiento ASC ";
		
		if(fechaIni != null) 
			hqlQuery += " AND a.asiento.fechaAsiento >= :fch1 ";
		if(fechaFin != null) 
			hqlQuery += " AND a.asiento.fechaAsiento <= :fch2 ";
		
		//Sacamos el detalle de los asientos contables que afectan la cuenta ordenados por fecha
		for(CuentaContable tmpCta : libroMayor) {
			qr = getEntityManager().createQuery(hqlQuery);
			qr.setParameter("cta", tmpCta);
			if(fechaIni != null) 
				qr.setParameter("fch1", fch1.getTime());
			if(fechaFin != null) 
				qr.setParameter("fch2", fch2.getTime());
			tmpCta.setAsientosCuenta(qr.getResultList());
			if(tmpCta.getTipoCuenta().getCodigo().equals("AC"))
				libroMayorAct.add(tmpCta);
			else
				libroMayorPsv.add(tmpCta);
			//Sacamos el total de cada cuenta
			
			Double totalMovs = 0.00;
			for(AsientoContDet detMov : tmpCta.getAsientosCuenta()) {
				if(detMov.getTipo().equals("CRG"))
					totalMovs += detMov.getMonto();
				else
					totalMovs -= detMov.getMonto();
			}
			tmpCta.setTotalMovs(new Long(Math.round(totalMovs*100))/100.0);
			
		}
	}

	@Override
	public boolean preSave() {
		
		return true;
	}

	@Override
	public boolean preModify() {
				
		return true;
	}
	
	@Override
	public boolean preDelete() {
		
		return true;
	}
	
	
	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		
	}
	
	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}


	public List<AsientoContable> getResultList() {
		return resultList;
	}


	public void setResultList(List<AsientoContable> resultList) {
		this.resultList = resultList;
	}


	public List<AsientoContDet> getLibroDiario() {
		return libroDiario;
	}


	public void setLibroDiario(List<AsientoContDet> libroDiario) {
		this.libroDiario = libroDiario;
	}

	public Date getFechaLibro() {
		return fechaLibro;
	}

	public void setFechaLibro(Date fechaLibro) {
		this.fechaLibro = fechaLibro;
	}

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public List<CuentaContable> getLibroMayorAct() {
		return libroMayorAct;
	}

	public void setLibroMayorAct(List<CuentaContable> libroMayorAct) {
		this.libroMayorAct = libroMayorAct;
	}

	public List<CuentaContable> getLibroMayorPsv() {
		return libroMayorPsv;
	}

	public void setLibroMayorPsv(List<CuentaContable> libroMayorPsv) {
		this.libroMayorPsv = libroMayorPsv;
	}
	
	
}
