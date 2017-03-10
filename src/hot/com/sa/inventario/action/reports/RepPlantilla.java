package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.crm.ChequeDoc;
import com.sa.model.security.Sucursal;
import com.sa.model.vta.VentaDoc;

@Name("repPlantilla")
@Scope(ScopeType.CONVERSATION)
public class RepPlantilla extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	private Sucursal sucursal;
	
	public void resetClass() {
		
		dtRp = new HashMap<String, Object>();
		sucursal = null;
		resetMainClass();
	}

	public void detChequesEmitidos() {

		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		String hqlCond = "";
		hql = "SELECT c FROM ChequeDoc c WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hqlCond += " AND c.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hqlCond += " AND c.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hqlCond += " AND c.fecha BETWEEN :f1 AND :f2 ";
		else 
			hqlCond += "  AND (:f1 = :f2 OR 1 = 1) ";
				
		if( getValCmb1() == null || getValCmb1().equals("") )
			hqlCond += " AND (:P1 IS NULL OR 1 = 1) ";
		else
			hqlCond += " AND c.proveedor.razonSocial = :P1 ";
		
		if( getValCmb2() == null || getValCmb2().equals("") )
			hqlCond += " AND (:P2 IS NULL OR 1 = 1) ";
		else
			hqlCond += " AND c.comprobante.empresaDoc.nombre = :P2";
		
		if( getValCmb3() == null || getValCmb3().equals("") )
			hqlCond += " AND (:P3 IS NULL OR 1 = 1) ";
		else
			hqlCond += " AND c.estado = :P3";
		
		List<ChequeDoc> chequesDoc = new ArrayList<ChequeDoc>();
		chequesDoc = entityManager.createQuery(hql + hqlCond)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("P1", getValCmb1())
				.setParameter("P2", getValCmb2())
				.setParameter("P3", getValCmb3())
				.getResultList(); 
		
		dtRp.put("cheques", chequesDoc);
		
		hql = "SELECT SUM(c.monto) FROM ChequeDoc c WHERE 1 = 1 ";
		
		Double totalMontoCheques = 0.0;
		totalMontoCheques = (Double) entityManager.createQuery(hql + hqlCond)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("P1", getValCmb1())
				.setParameter("P2", getValCmb2())
				.setParameter("P3", getValCmb3())
				.getSingleResult();
		
		dtRp.put("montoCheques", totalMontoCheques);
	}	
	
	
	/*Para reporte de ventas mensuales*/
	public void selComprbt(String val) {
		this.setValCmb1(val);
		repVentasDocMensuales();
	}
	
	public void selCliente(String val) {
		this.setValCmb2(val);
		repVentasDocMensuales(); 
	} 
	
	public void selEmpDoc(String val) { 
		this.setValCmb3(val);
		repVentasDocMensuales();
	}
	  
	public void limpiarSelCce() { 
		this.setValCmb1("");
		this.setValCmb2(""); 
		this.setValCmb3("");
		repVentasDocMensuales(); 
	}
	
	public void repVentasDocMensuales() {
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);	
		 
		hql = "SELECT v FROM VentaDoc v WHERE 1 = 1 ";
		
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND v.fecha >= :f1 AND :f2 = :f2 "; 
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND v.fecha <= :f2 AND :f1 = :f1 ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND v.fecha BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		}
		 
		if(getValCmb1() != null && getValCmb1() != ""){
			hql += " AND v.comprobante.nombre = :P1 ";
		}else{ 
			hql += " AND :P1 = :P1 ";			
		}
		  
		if(getValCmb2() != null && getValCmb2() != ""){ 
			hql += " AND v.cliente.nombre = :P2 ";
		}else{
			hql += " AND :P2 = :P2 ";
		}		 
		
		if(getValCmb3() != null && getValCmb3() != ""){
			hql += " AND v.comprobante.empresaDoc.nombre = :P3 ";
		}else{
			hql += " AND :P3 = :P3 ";
		}
		
		hql += " ORDER BY v.fecha, v.comprobante.nombre";
		System.out.println(hql);
		
		List<VentaDoc> ventasDoc = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("P1", getValCmb1())
				.setParameter("P2", getValCmb2())
				.setParameter("P3", getValCmb3())
				.getResultList();
		
		dtRp.put("ventasDoc", ventasDoc);
	}
	

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}


	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}
	
	

}


