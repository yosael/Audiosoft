package com.sa.inventario.action.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.model.crm.ChequeDoc;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Producto;
import com.sa.model.inventory.Transferencia;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.ReparacionCliente;

@Name("repTransferencia")
@Scope(ScopeType.CONVERSATION)
public class RepTransferencia extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@In
	private EntityManager entityManager;
	
	@In
	private LoginUser loginUser;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	private List<Transferencia> resultList = new ArrayList<Transferencia>();
	private String estado="";
	private Sucursal sucursalDesde;
	private Sucursal sucursalHacia;
	List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private String estadoFilter="";
	
	
	public void buscarTransferencias()
	{
		StringBuilder jpql = new StringBuilder();
		
		jpql.append("SELECT t FROM Transferencia t where 1=1 "); //filtrar por fechas, sucursal y estado;
		
		
		if(fechaInicio != null && fechaFin == null) 
			jpql.append(" AND t.fecha >= :f1 AND :f2 IS NULL ");
		else if(fechaInicio == null && fechaFin != null)
			jpql.append(" AND t.fecha <= :f2 AND :f1 IS NULL ");
		else if(fechaInicio != null && fechaFin != null)
			jpql.append(" AND t.fecha>=:f1 AND t.fecha<=:f2 ");//condGnr += " AND x.movimiento.fecha BETWEEN :f1 AND :f2 ";
		else
			jpql.append("  AND (:f1 = :f2 OR 1 = 1) ");
		
		
		if(estado!=null && !estado.equals(""))
			jpql.append(" AND t.estado='").append(estado).append("'");
		
		if(sucursalDesde!=null)
		{
			jpql.append(" AND t.sucursalDestino.id=").append(sucursalDesde.getId());
		}
		
		if(sucursalHacia!=null)
		{
			jpql.append(" AND t.sucursal.id=").append(sucursalHacia.getId());
		}
		
		jpql.append(" Order by t.id desc");
		
		resultList = entityManager.createQuery(jpql.toString())
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
	}
	
	
	
	public void cargarSucursales()
	{
		sucursales = entityManager.createQuery("SELECT s FROM Sucursal s where s.bodega=true").getResultList();
	}


	public List<Transferencia> getResultList() {
		return resultList;
	}


	public void setResultList(List<Transferencia> resultList) {
		this.resultList = resultList;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public List<Sucursal> getSucursales() {
		return sucursales;
	}


	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}


	public String getEstadoFilter() {
		return estadoFilter;
	}


	public void setEstadoFilter(String estadoFilter) {
		this.estadoFilter = estadoFilter;
	}


	public Sucursal getSucursalDesde() {
		return sucursalDesde;
	}


	public void setSucursalDesde(Sucursal sucursalDesde) {
		this.sucursalDesde = sucursalDesde;
	}


	public Sucursal getSucursalHacia() {
		return sucursalHacia;
	}


	public void setSucursalHacia(Sucursal sucursalHacia) {
		this.sucursalHacia = sucursalHacia;
	}
	
	
	
	
	
	

}
