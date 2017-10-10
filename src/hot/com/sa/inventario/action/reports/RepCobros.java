package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Sucursal;

@Name("repCobros")
@Scope(ScopeType.CONVERSATION)
public class RepCobros extends MasterRep implements Serializable {
	
	
	private Calendar calendario = new GregorianCalendar();
	private List<VentaProdServ> resultList = new ArrayList<VentaProdServ>();
	
	@In
	private EntityManager entityManager;
	
	@In
	private LoginUser loginUser;
	
	public void setDiaActual() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, 0);
		setFechaInicio(truncDate(cal.getTime(), true));		
		setFechaFin(truncDate(cal.getTime(), false));
		calendario= new GregorianCalendar();
		calendario.add(Calendar.DATE, 0);
	}
	
	
	public void verReporteCobrosPendientes() {

		List<Sucursal> subSucFlt = entityManager
				.createQuery(
						"SELECT s FROM Sucursal s WHERE (s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc) ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter(
						"otraSuc",
						loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
								.getUser().getSucursal() : loginUser.getUser()
								.getSucursal().getSucursalSuperior())
				.getResultList();

		if (subSucFlt == null || subSucFlt.size() <= 0)
			subSucFlt = new ArrayList<Sucursal>();

		subSucFlt.add(loginUser.getUser().getSucursal());
		subSucFlt
				.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null ? loginUser
						.getUser().getSucursal() : loginUser.getUser()
						.getSucursal().getSucursalSuperior());

		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
		if (getFechaInicio() != null && getFechaFin() != null) {
			setFechaInicio(truncDate(getFechaInicio(), false));
			setFechaFin(truncDate(getFechaFin(), true));
			fltFch = " AND v.fechaVenta BETWEEN :fch1 AND :fch2 ";
		}

		resultList = entityManager
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE v.estado='PEN' and (v.sucursal = :suc or v.sucursal IN (:subSuc) ) "
								+ fltFch + " ORDER BY v.cliente.nombres ASC,v.fechaVenta DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaInicio())
				.setParameter("fch2", getFechaFin())
				.setParameter(
						"subSuc",
						subSucFlt == null ? new ArrayList<Sucursal>()
								: subSucFlt).getResultList();
		System.out.println("sucursal: "
				+ loginUser.getUser().getSucursal().getNombre());

		// cambio, todos los resultados de estados se desean en cobros.
		/*
		 * resultList = getEntityManager() .createQuery(
		 * "SELECT v FROM VentaProdServ v WHERE (v.sucursal = :suc or v.sucursal IN (:subSuc) ) "
		 * + fltFch + " AND v.estado <> 'PDS' ORDER BY v.fechaVenta DESC ")
		 * .setParameter("suc", loginUser.getUser().getSucursal())
		 * .setParameter("fch1", getFechaPFlt1()) .setParameter("fch2",
		 * getFechaPFlt2()) .setParameter("subSuc", subSucFlt==null?new
		 * ArrayList<Sucursal>():subSucFlt) .getResultList();
		 */

	}


	public List<VentaProdServ> getResultList() {
		return resultList;
	}


	public void setResultList(List<VentaProdServ> resultList) {
		this.resultList = resultList;
	}
	
	
	

}
