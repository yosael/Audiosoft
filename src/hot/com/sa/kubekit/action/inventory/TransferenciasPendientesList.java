package com.sa.kubekit.action.inventory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Transferencia;

//Listas de transferencias pendientes de ser aprobadas por el jefe de la
//sucursal que hace la solicitud de transferencia

@Name("transferenciasPendientesList")
@Scope(ScopeType.CONVERSATION)
public class TransferenciasPendientesList extends KubeQuery<Transferencia>{

	@In
	private LoginUser loginUser;
	
	private Date fechaInicio;
	private Date fechaFin;
	private String estadoFilter="";
	private String estado;
	
	@Create
	public void init() {
		
		setRangoMes();
		
		if(loginUser.getUser().getSucursal()!=null){
			
			StringBuilder jpql = new StringBuilder();
			jpql.append("select t from Transferencia t where (t.estado = 'P' OR t.estado = 'S') and ")
			.append("t.sucursal.id = ")
			.append(loginUser.getUser().getSucursal().getId());
			
			
			//if(fechaInicio != null && fechaFin != null)
				jpql.append(" AND t.fecha>= '").append(fechaInicio).append("' AND t.fecha<='").append(fechaFin).append("'");
			
			
			if(estado!=null && !estado.equals(""))
				jpql.append(" AND t.estado='").append(estado).append("'");
			
			
			
			jpql.append(" order by t.id desc ");
			
			setJpql(jpql.toString());
			
		}else{
			setJpql("select t from Transferencia t where (t.estado like 'P' or t.estado like 'S') order by t.id desc");
		}
	}
	
	public void buscar()
	{
		
		if(loginUser.getUser().getSucursal()!=null){
			
			StringBuilder jpql = new StringBuilder();
			jpql.append("select t from Transferencia t where (t.estado = 'P' OR t.estado = 'S') and ")
			.append("t.sucursal.id = ")
			.append(loginUser.getUser().getSucursal().getId());
			
			if(fechaInicio != null && fechaFin == null) 
				jpql.append(" AND t.fecha >='").append(fechaInicio).append("' AND '").append(fechaFin).append("' IS NULL ");
			else if(fechaInicio == null && fechaFin != null)
				jpql.append(" AND t.fecha <= '").append(fechaFin).append("' AND '").append(fechaInicio).append("' IS NULL ");
			else if(fechaInicio != null && fechaFin != null)
				jpql.append(" AND t.fecha>= '").append(fechaInicio).append("' AND t.fecha<='").append(fechaFin).append("'");
			else
				jpql.append("  AND ('").append(fechaInicio).append("' = '").append(fechaFin).append("' OR 1 = 1) ");
			
			
			if(estado!=null && !estado.equals(""))
				jpql.append(" AND t.estado='").append(estado).append("'");
			
			
			
			jpql.append(" order by t.id desc ");
			
			setJpql(jpql.toString());
			
		}else{
			setJpql("select t from Transferencia t where (t.estado like 'P' or t.estado like 'S') order by t.id desc");
		}
	}
	
	public void setRangoMes() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		setFechaInicio(truncDate(cal.getTime(), false));
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		setFechaFin(truncDate(cal.getTime(), true));
	}
	
	private Date truncDate(Date dateTrunc, boolean endOfDay) {
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

	public String getEstadoFilter() {
		return estadoFilter;
	}

	public void setEstadoFilter(String estadoFilter) {
		this.estadoFilter = estadoFilter;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	
	
}
