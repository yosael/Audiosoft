package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.acct.CajaChicaMov;
import com.sa.model.acct.CuentaCobrar;
import com.sa.model.security.Sucursal;

@Name("repContabilidad")
@Scope(ScopeType.CONVERSATION)
public class RepContabilidad extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	private List<CuentaCobrar> cuentasCobrar = new ArrayList<CuentaCobrar>();
	
	private Sucursal sucursal;
	
	private String intervaloTiempo;
	
	public void resetClass() {
		dtRp = new HashMap<String, Object>();
		cuentasCobrar = new ArrayList<CuentaCobrar>();
		intervaloTiempo = "";
		resetMainClass();
	}
		
	public void repCxcClientes() {
		//Verificamos si pusieron alguna fecha
		List<HashMap<String, Object>> listaClientes = new ArrayList<HashMap<String, Object>>();
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		String hqlCond = "";
		hql = "SELECT DISTINCT x.cliente, 'NRM' FROM CuentaCobrar x WHERE x.cliCorp IS NULL ";
		
		if(fechaInicio != null && fechaFin == null) 
			hqlCond += " AND x.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hqlCond += " AND x.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hqlCond += " AND x.fechaIngreso BETWEEN :f1 AND :f2 ";
		else 
			hqlCond += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		//Estado
		if(getValCmb1() == null || getValCmb1().equals(""))
			hqlCond+=" AND (:est IS NULL OR 1 = 1) ";
		else
			hqlCond+=" AND x.estado = :est ";

		//Clientes normales o corporativos
		if(getValCmb2() != null && !getValCmb2().equals("")) {
			if(getValCmb2().equals("CRP")) 
				hqlCond+=" AND x.cliCorp IS NOT NULL ";
			else if(getValCmb2().equals("NRM"))
				hqlCond+=" AND x.cliCorp IS NULL AND x.cliente IS NOT NULL ";
		} 
		
		List<Object[]> clientes = entityManager.createQuery(hql + hqlCond)
				.setParameter("f1", getFechaInicio())
				.setParameter("f2", getFechaFin())
				.setParameter("est", getValCmb1())
				.getResultList();
		
		hql = "SELECT DISTINCT x.cliCorp, 'CRP' FROM CuentaCobrar x WHERE 1 = 1  ";
		
		List<Object[]> clientesCrp = entityManager.createQuery(hql + hqlCond)
				.setParameter("f1", getFechaInicio())
				.setParameter("f2", getFechaFin())
				.setParameter("est", getValCmb1())
				.getResultList();

		//Unimos las listas
		clientes.addAll(clientesCrp);
		
		//Iteramos sobre la lista para sacar las cuentas por pagar que tienen
		for(Object[] tmpCli : clientes) {
			if(tmpCli[1].equals("NRM"))
				hql = "SELECT x FROM CuentaCobrar x WHERE x.cliente = :cli ";
			else if(tmpCli[1].equals("CRP"))
				hql = "SELECT x FROM CuentaCobrar x WHERE x.cliCorp = :cli ";
			
			List<CuentaCobrar> cuentasCobrar = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", getFechaInicio())
					.setParameter("f2", getFechaFin())
					.setParameter("est", getValCmb1())
					.setParameter("cli", tmpCli[0])
					.getResultList();
			
			HashMap<String, Object> tmpMp = new HashMap<String, Object>();
			tmpMp.put("tpc", tmpCli[1]);
			tmpMp.put("cli", tmpCli[0]);
			tmpMp.put("cxc", cuentasCobrar);
			
			listaClientes.add(tmpMp);
		}
		
		dtRp.put("lst", listaClientes);
	}
	
	public void repAbonosCxc() {
		//Verificamos si pusieron alguna fecha
		List<HashMap<String, Object>> listaAbonos = new ArrayList<HashMap<String, Object>>();
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		String hqlCond = "";
		hql = "SELECT x FROM PagoCuentaPend x WHERE 1 = 1 ";
		
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.fechaIngreso BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		//Sucursal
		if(sucursal != null) 
			hql += " AND x.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		listaAbonos = entityManager.createQuery(hql)
				.setParameter("f1", getFechaInicio())
				.setParameter("f2", getFechaFin())
				.setParameter("suc", sucursal)
				.getResultList();
		
		dtRp.put("lst", listaAbonos);
	}
	
	public void setCliCorpSald(Object obj1) {
		setFltObj1(obj1);
		repCxcSaldadas();
	}
	
	public void repCxcSaldadas() {
		//Verificamos si pusieron alguna fecha
		List<CuentaCobrar> cuentasPagadas = new ArrayList<CuentaCobrar>();
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		String hqlCond = "";
		hql = "SELECT x FROM CuentaCobrar x WHERE 1 = 1 AND x.estado = 'PGD' ";
		
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.fechaFinalizacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.fechaFinalizacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.fechaFinalizacion BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		//Sucursal
		if(sucursal != null) 
			hql += " AND x.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		if(getFltObj1() != null)
			hql += " AND x.cliCorp = :clc ";
		else
			hql += " AND (:clc = 0 OR 1 = 1) ";
		
		hql += " ORDER BY x.fechaFinalizacion ASC ";
		
		cuentasPagadas = entityManager.createQuery(hql)
				.setParameter("f1", getFechaInicio())
				.setParameter("f2", getFechaFin())
				.setParameter("suc", sucursal)
				.setParameter("clc", getFltObj1())
				.getResultList();
		
		dtRp.put("lst", cuentasPagadas);
	}
	
	public void repStockBodegas() {
		//Verificamos si pusieron alguna fecha
		/*setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		*/
		hql = "SELECT v FROM Inventario i WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND v.fechaVenta >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND v.fechaVenta <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND v.fechaVenta BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND v.fechaVenta BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(getValCmb1() == null || getValCmb1().equals(""))
			hql+=" AND (:tpv IS NULL OR :tpv = '') ";
		else
			hql+=" AND v.tipoVenta = :tpv  ";

		if(getValCmb4() == null || getValCmb4().equals(""))
			hql+=" AND (:est IS NULL OR :est = '') ";
		else
			hql+=" AND v.estado = :est  ";

		cuentasCobrar = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpv", getValCmb1())
				.setParameter("est", getValCmb4())
				.getResultList();
	}
	
	public void repCajasChicas() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT x FROM CajaChicaMov x WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.fecha BETWEEN :f1 AND :f2 ";
		else
			hql = " AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursal != null) 
			hql += " AND x.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		hql += " ORDER BY x.sucursal.id ASC, x.fecha DESC ";
		
		List<CajaChicaMov> diasCajaChica = entityManager.createQuery(hql)
				.setParameter("suc", sucursal)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		dtRp.put("lst", diasCajaChica);
	}
	
	public String getIntervaloTiempo() {
		return intervaloTiempo;
	}

	public void setIntervaloTiempo(String intervaloTiempo) {
		this.intervaloTiempo = intervaloTiempo;
	}

	public List<CuentaCobrar> getCuentasCobrar() {
		return cuentasCobrar;
	}

	public void setCuentasCobrar(List<CuentaCobrar> cuentasCobrar) {
		this.cuentasCobrar = cuentasCobrar;
	}

	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

}


