package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.crm.Cliente;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.ReparacionCliente;

@Name("repMedico")
@Scope(ScopeType.CONVERSATION)
public class RepMedico extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	private final String repCtx = "/med";
	private List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<MedicalAppointment> citas = new ArrayList<MedicalAppointment>();
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	public void resetClass() {
		clientes = new ArrayList<Cliente>();
		resultList = new ArrayList<HashMap<String, Object>>();
		consultasMed = new ArrayList<MedicalAppointment>();
		sucursal = null;
		dtRp = new HashMap<String, Object>();
		citas = new ArrayList<MedicalAppointment>();
		resetMainClass();
	}
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	private List<MedicalAppointment> consultasMed = new ArrayList<MedicalAppointment>();
	private Sucursal sucursal;
	
	private String intervaloTiempo;
	
	
	public void repConsultasCan() {
		consultasMed = new ArrayList<MedicalAppointment>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT e FROM MedicalAppointment e WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND e.dateTime >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND e.dateTime <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND e.dateTime BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND e.dateTime BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(getTotInt1() == null )
			hql+=" AND (:est IS NULL OR :est = '') ";
		else 
			hql+=" AND e.status = :est ";
		
		if(sucursal != null) 
			hql += " AND e.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
				
		List<ReparacionCliente> tmpRep = new ArrayList<ReparacionCliente>();
		consultasMed = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("est", getTotInt1())
				.setParameter("suc", sucursal)
				.getResultList();
	}
	
	public void expedienteTotal() {
		hql = " SELECT c FROM Cliente c WHERE c = :cli ";
		
		Cliente tmpCli = (Cliente)entityManager.createQuery(hql)
				.setParameter("cli", getFltObj1())
				.getSingleResult();
		
		dtRp.put("cli", tmpCli);
	}
	
	public void repPacientes() {
		setFechaInicio(fechaInicio!=null?truncDate(fechaInicio, false):null);
		setFechaFin(fechaFin!=null?truncDate(fechaFin, true):null);
		
		hql = "SELECT x FROM Cliente x WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.fechaCreacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.fechaCreacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.fechaCreacion BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		

		if(getValCmb1() == null || getValCmb1().equals(""))
			hql+=" AND (:tpref IS NULL OR 1 = 1) ";
		else if(getValCmb1().equals("CLI"))
			hql+=" AND x.referidoPor IS NOT NULL AND (:tpref IS NULL OR 1 = 1) ";
		else if(getValCmb1().equals("DOC"))
			hql+=" AND x.doctorRef IS NOT NULL AND (:tpref IS NULL OR 1 = 1) ";
		
		if(getValCmb2() == null || getValCmb2().equals(""))
			hql+="  ";
		else 
			hql+=" AND (UPPER(x.referidoPor.nombres) LIKE '" + getValCmb2().toUpperCase().concat("%") + "' OR UPPER(x.doctorRef.nombres) LIKE '" + getValCmb2().toUpperCase().concat("%") + "' ) ";
		
		hql += " ORDER BY x.fechaNacimiento ASC ";
		
		clientes = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpref", getValCmb1())
				//.setParameter("rfpor", )
				.getResultList();
	}
	
	public void repCitasPrg48() {
		//Calculamos los siguientes 2 dias
		setFechaInicio(new Date());
		Calendar cal = new GregorianCalendar();
		cal.setTime(getFechaInicio());
		cal.add(Calendar.DATE, 2);
		setFechaFin(cal.getTime());
		setFechaInicio(fechaInicio!=null?truncDate(fechaInicio, false):null);
		setFechaFin(fechaFin!=null?truncDate(fechaFin, true):null);
		
		hql = "SELECT x FROM MedicalAppointment x WHERE 1 = 1 AND x.status = 0 ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.dateTime >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.dateTime <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.dateTime BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND x.dateTime BETWEEN :f1 AND :f2 ";
			
		}

		hql += " ORDER BY x.dateTime ASC ";
		
		citas = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
	}
	
	public void repCitasPrg() {
		setFechaInicio(fechaInicio!=null?truncDate(fechaInicio, false):null);
		setFechaFin(fechaFin!=null?truncDate(fechaFin, true):null);
		
		hql = "SELECT x FROM MedicalAppointment x WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.dateTime >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.dateTime <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.dateTime BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND x.dateTime BETWEEN :f1 AND :f2 ";
			
		}

		hql += " ORDER BY x.dateTime ASC ";
		
		citas = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
	}

	public String getRepCtx() {
		return repCtx;
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

	public String getIntervaloTiempo() {
		return intervaloTiempo;
	}

	public void setIntervaloTiempo(String intervaloTiempo) {
		this.intervaloTiempo = intervaloTiempo;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public List<MedicalAppointment> getConsultasMed() {
		return consultasMed;
	}

	public void setConsultasMed(List<MedicalAppointment> consultasMed) {
		this.consultasMed = consultasMed;
	}

	public List<HashMap<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<HashMap<String, Object>> resultList) {
		this.resultList = resultList;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<MedicalAppointment> getCitas() {
		return citas;
	}

	public void setCitas(List<MedicalAppointment> citas) {
		this.citas = citas;
	}

	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}
	
	
	
}
