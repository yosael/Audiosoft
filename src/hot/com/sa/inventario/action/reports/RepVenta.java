package com.sa.inventario.action.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.drools.lang.DRLParser.neg_operator_key_return;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.model.crm.Cliente;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Item;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.ItemComboApa;
import com.sa.model.sales.PojoCotizacionRp;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Sucursal;
import com.sa.model.vta.VentaDoc;
import com.sa.model.workshop.AparatoCliente;

@Name("repVenta")
@Scope(ScopeType.CONVERSATION)
public class RepVenta extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	private List<CotizacionComboApa> cotizaciones = new ArrayList<CotizacionComboApa>();
	private List<VentaProdServ> ventas = new ArrayList<VentaProdServ>();
	private List<DetVentaProdServ> detVentas = new ArrayList<DetVentaProdServ>();
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	private String intervaloTiempo;
	private String codsServ;
	private Sucursal sucursal;
	private Categoria catSelected;
	private List<Object> cotizacionCombos= new ArrayList<Object>();
	private List<Object[]> cotizacionCombos0= new ArrayList<Object[]>();
	private List<Object[]> cotizacionCombos1= new ArrayList<Object[]>();
	private List<Object[]> cotizacionCombos2= new ArrayList<Object[]>();
	private List<Object[]> cotizacionCombos3= new ArrayList<Object[]>();
	private List<PojoCotizacionRp> cotizacionesCombo= new ArrayList<PojoCotizacionRp>();
	
	
	
	
	//Nuevo 09/11/2016
	private List<DetVentaProdServ> detalleVentas  = new ArrayList<DetVentaProdServ>();
	
	public void resetClass() {
		cotizaciones = new ArrayList<CotizacionComboApa>();
		ventas = new ArrayList<VentaProdServ>();
		detVentas = new ArrayList<DetVentaProdServ>();
		dtRp = new HashMap<String, Object>();
		intervaloTiempo = "";
		codsServ = "";
		sucursal = null;
		resetMainClass();
	}

	public void repCotizaciones() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT c FROM CotizacionComboApa c WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND c.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND c.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(getValCmb2() == null || getValCmb2().equals(""))
			hql+=" AND (:est IS NULL OR :est = '') ";
		else
			hql+=" AND c.estado = :est  ";
		
		if(isFlag1())
			hql+=" AND c.hijoBin IS NOT EMPTY AND (:bin = true OR 1 = 1)  ";
		else
			hql+=" AND (:bin = false OR 1 = 1)  ";
		
		if(sucursal != null) 
			hql += " AND c.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";

		cotizaciones = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("est", getValCmb2())
				.setParameter("suc", sucursal)
				.setParameter("bin", isFlag1())
				.getResultList();
		
		if(cotizaciones != null)
			dtRp.put("totalaparatos", cotizaciones.size());
		else
			dtRp.put("totalaparatos", 0);
	}
	
	public void repVentasMensuales() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT v FROM VentaProdServ v WHERE 1 = 1  ";
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
		
		if(sucursal != null) 
			hql += " AND v.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		ventas = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpv", getValCmb1())
				.setParameter("est", getValCmb4())
				.setParameter("suc", sucursal)
				.getResultList();
		
		//Calculamos total de ventas
		setTotDec1(0f);
		for(VentaProdServ tmpVt: ventas) 
			setTotDec1(getTotDec1()+tmpVt.getMonto());
	}
	
	
	public void cotizacionCombo()
	{
		cotizacionesCombo.clear();
		
		String jpql0="";
		String jpql1="";
		String jpql2="";
		String jpql3="";
		String sortGroup1="";
		String sortGroup2="";
		
		jpql0="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),coti.sucursal.nombre " +
				"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
				"Where cotiCombos.cotizacion=coti";
		
		jpql1="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),coti.estado,coti.sucursal.nombre " +
					"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
					"Where cotiCombos.cotizacion=coti and coti.estado='PEN'";
		
		jpql2="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),coti.estado,coti.sucursal.nombre " +
				"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
				"Where cotiCombos.cotizacion=coti and coti.estado='APL'";
		
		jpql3="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),coti.estado,coti.sucursal.nombre " +
				"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
				"Where cotiCombos.cotizacion=coti and coti.estado='COT'";
		
		
		/*if(!isFlag1())
		{*/
			//Seleccionar: Combo, #Cotizaciones(una suma), monto(suma), tipo(normal,binaural),estado(APL,PEN),Sucursal
			
			/*jpql="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),SUM(cotCmb.precioCotizado),coti.estado,coti.sucursal.nombre " +
						"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti,CotCmbsItems cotCmb " +
						"Where cotiCombos.cotizacion=coti and cotiCombos=cotCmb.ctCmbs";*/
			
			/*jpql="SELECT cotiCombos.combo.nombre,COUNT(cotiCombos.combo),coti.estado,coti.sucursal.nombre " +
					"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
					"Where cotiCombos.cotizacion=coti";*/
			
			/*jpql="SELECT cotCmbs.ctCmbs.combo.nombre,COUNT(cotCmbs.ctCmbs.combo),SUM(cotCmbs.precioCotizado),cotCmbs.ctCmbs.cotizacion.estado,cotCmbs.ctCmbs.cotizacion.sucursal.nombre " +
					"FROM CotCmbsItems cotCmbs " +
					"Where 1=1 ";*/
			
			sortGroup1=" group by cotiCombos.combo.nombre,coti.sucursal.nombre order by cotiCombos.combo.nombre";
			sortGroup2=" group by cotiCombos.combo.nombre,coti.estado,coti.sucursal.nombre order by cotiCombos.combo.nombre";
			
			/*sortGroup1=" group by cotiCombos.combo.nombre,coti.estado,coti.sucursal.nombre order by cotiCombos.combo.nombre";
			sortGroup2=" group by cotiCombos.combo.nombre,coti.estado,coti.sucursal.nombre order by cotiCombos.combo.nombre";
			sortGroup3=" group by cotiCombos.combo.nombre,coti.estado,coti.sucursal.nombre order by cotiCombos.combo.nombre";*/
			//sortGroup=" group by cotCmbs.ctCmbs.combo.nombre,cotCmbs.ctCmbs.cotizacion.estado,cotCmbs.ctCmbs.cotizacion.sucursal.nombre order by cotCmbs.ctCmbs.combo.nombre";
		/*}
		else
		{
			//Seleccionar: Combo, #Cotizaciones Cotizados(una suma),#Cotizaciones Vendidos(una suma),#Cotizaciones CxC(una suma) ,Total Cot,Sucursal
			
			jpql="SELECT cotiCombos.combo.nombre,(SELECT COUNT(cotiCombos.combo) FROM CotizacionCombos cotiCombos,CotizacionComboApa coti Where cotiCombos.cotizacion=coti and coti.estado='PEN')," +			
					"(SELECT COUNT(cotiCombos.combo) FROM CotizacionCombos cotiCombos,CotizacionComboApa coti Where cotiCombos.cotizacion=coti and coti.estado='APL')," +
					"(SELECT COUNT(cotiCombos.combo) FROM CotizacionCombos cotiCombos,CotizacionComboApa coti Where cotiCombos.cotizacion=coti and coti.estado='COT') " +
					"FROM CotizacionCombos cotiCombos,CotizacionComboApa coti " +
					"Where cotiCombos.cotizacion=coti";
			
			
			
			sortGroup=" group by cotiCombos.combo.nombre order by cotiCombos.combo.nombre";
		}*/
		
		
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
				
		
		if(fechaInicio != null && fechaFin == null) 
		{
			jpql0 += " AND cotiCombos.cotizacion.fechaIngreso >= :f1 AND :f2 IS NULL ";
			jpql1 += " AND cotiCombos.cotizacion.fechaIngreso >= :f1 AND :f2 IS NULL ";
			jpql2 += " AND cotiCombos.cotizacion.fechaIngreso >= :f1 AND :f2 IS NULL ";
			jpql3 += " AND cotiCombos.cotizacion.fechaIngreso >= :f1 AND :f2 IS NULL ";
		}
		else if(fechaInicio == null && fechaFin != null)
		{
			jpql0 += " AND cotiCombos.cotizacion.fechaIngreso <= :f2 AND :f1 IS NULL ";
			jpql1 += " AND cotiCombos.cotizacion.fechaIngreso <= :f2 AND :f1 IS NULL ";
			jpql2 += " AND cotiCombos.cotizacion.fechaIngreso <= :f2 AND :f1 IS NULL ";
			jpql3 += " AND cotiCombos.cotizacion.fechaIngreso <= :f2 AND :f1 IS NULL ";
		}
		else if(fechaInicio != null && fechaFin != null)
		{
			jpql0 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql1 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql2 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql3 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
		}
		else {
			jpql0 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql1 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql2 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			jpql3 += " AND cotiCombos.cotizacion.fechaIngreso BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		
		}
		
		/*if(!isFlag1())
		{*/
			/*if(getValCmb2()==null)
			{
				jpql0+=" AND coti.estado!='x'";
				jpql1+=" AND coti.estado!='x'";
				jpql2+=" AND coti.estado!='x'";
				jpql3+=" AND coti.estado!='x'";
				System.out.println("Estado es nulo");
			}
			else if(getValCmb2().equals(""))
			{
				jpql0+=" AND coti.estado!='x'";
				jpql1+=" AND coti.estado!='x'";
				jpql2+=" AND coti.estado!='x'";
				jpql3+=" AND coti.estado!='x'";
				System.out.println("estado vacio");
			}
			else
			{
				jpql0+=" AND coti.estado='"+getValCmb2()+"'";
				jpql1+=" AND coti.estado='"+getValCmb2()+"'";
				jpql2+=" AND coti.estado='"+getValCmb2()+"'";
				jpql3+=" AND coti.estado='"+getValCmb2()+"'";
				System.out.println("estado lleno");
			}*/
			
			if(sucursal==null)
			{
				jpql0+=" AND coti.sucursal.nombre!='x'";
				jpql1+=" AND coti.sucursal.nombre!='x'";
				jpql2+=" AND coti.sucursal.nombre!='x'";
				jpql3+=" AND coti.sucursal.nombre!='x'";
				System.out.println("Sucursal es nula");
			}
			else
			{
				jpql0+=" AND coti.sucursal.nombre='"+sucursal.getNombre()+"'";
				jpql1+=" AND coti.sucursal.nombre='"+sucursal.getNombre()+"'";
				jpql2+=" AND coti.sucursal.nombre='"+sucursal.getNombre()+"'";
				jpql3+=" AND coti.sucursal.nombre='"+sucursal.getNombre()+"'";
			}
		//}
			
		
		jpql0 += sortGroup1;
		jpql1 += sortGroup2;
		jpql2 += sortGroup2;
		jpql3 += sortGroup2;
		
		
		cotizacionCombos0 = entityManager.createQuery(jpql0)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		cotizacionCombos1 = entityManager.createQuery(jpql1)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		cotizacionCombos2 = entityManager.createQuery(jpql2)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		cotizacionCombos3 = entityManager.createQuery(jpql3)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		System.out.println("Tamanio lis 0 "+ cotizacionCombos0.size());
		System.out.println("Tamanio lis 1 "+ cotizacionCombos1.size());
		System.out.println("Tamanio lis 2 "+ cotizacionCombos2.size());
		System.out.println("Tamanio lis 3 "+ cotizacionCombos3.size());
		
		PojoCotizacionRp cotiTable;
		boolean entro;
		for(Object[] coti0: cotizacionCombos0)
		{
			
			cotiTable = new PojoCotizacionRp();
			cotiTable.setNombre(coti0[0].toString());
			cotiTable.setTotal((Long) coti0[1]);
			cotiTable.setSucursal(coti0[2].toString());
			
			entro=false;
			for(Object[] coti1:cotizacionCombos1)
			{
				if(coti0[0].equals(coti1[0].toString()))
				{
					cotiTable.setTotalCotizado((Long) coti1[1]);
					entro=true;
				}
				
				if(!entro)
					cotiTable.setTotalCotizado(0l);
			}
			
			entro=false;
			for(Object[] coti2:cotizacionCombos2)
			{
				if(coti0[0].equals(coti2[0]))
				{
					cotiTable.setTotalVendido((Long) coti2[1]);
					entro=true;
				}
				
				if(!entro)
					cotiTable.setTotalVendido(0l);
			}
			
			entro=false;
			for(Object[] coti3:cotizacionCombos3)
			{
				if(coti0[0].equals(coti3[0]) )
				{
					cotiTable.setTotalCxc((Long) coti3[1]);
					entro=true;
				}
				
				if(!entro)
					cotiTable.setTotalCxc(0l);
			}
			
			cotizacionesCombo.add(cotiTable);
		}
		
		/*
		  .setParameter("est", getValCmb2())
				.setParameter("suc", sucursal)
				.setParameter("bin", isFlag1())*/
		
		
		
		/*String jpql2="SELECT cotiCombos.combo,COUNT(cotiCombos.combo),cotiCombos.cotizacion.estado " +
				"FROM CotizacionCombos cotiCombos";*/
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
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		 
		if(getValCmb1() != null && !getValCmb1().equals("")){
			hql += " AND v.comprobante.nombre = :P1 ";
		}else{ 
			hql += " AND (:P1 IS NULL OR 1 = 1) ";			
		}
		  
		if(getValCmb2() != null && !getValCmb2().equals("")){ 
			hql += " AND v.cliente.nombre = :P2 ";
		}else{
			hql += " AND (:P2 IS NULL OR 1 = 1) ";
		}		 
		
		if(getValCmb3() != null && !getValCmb3().equals("")){
			hql += " AND v.comprobante.empresaDoc.nombre = :P3 ";
		}else{
			hql += " AND (:P3 IS NULL OR 1 = 1) ";
		}
		
		if(getValCmb4() != null && !getValCmb4().equals(""))
			hql += " AND v.estado = :P4 ";
		else
			hql += " AND (:P4 IS NULL OR 1 = 1) ";
		
		hql += " ORDER BY v.fecha, v.comprobante.nombre";
				
		List<VentaDoc> ventasDoc = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("P1", getValCmb1())
				.setParameter("P2", getValCmb2())
				.setParameter("P3", getValCmb3())
				.setParameter("P4", getValCmb4())
				.getResultList();
		
		dtRp.put("ventasDoc", ventasDoc);
	}
	
	public void repDetallesCombo() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT DISTINCT v.cliente FROM AparatoCliente v " +
				"	WHERE 1 = 1 AND v.costoVenta IS NOT NULL AND v.costoVenta > 0 ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND v.fechaAdquisicion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND v.fechaAdquisicion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND v.fechaAdquisicion BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		}
				
		if(sucursal != null) 
			hql += " AND v.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		if(getFltObj1() != null) 
			hql += " AND v.cliente = :cli ";
		else
			hql += " AND (:cli IS NULL OR :cli = '') ";
		
		List<Cliente> res = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("cli", getFltObj1())
				.getResultList();
		
		//Calculamos total de los aparatos vendidos
		Float totVta = 0f;
		Integer numApa = 0;
		List<AparatoCliente> tmpDel = new ArrayList<AparatoCliente>();
		for(Cliente tmpCli: res) {
			for(AparatoCliente tmpApa : tmpCli.getAparatosAuditivos()) {
				if(tmpApa.getCostoVenta() != null && tmpApa.getCostoVenta() > 0) {
					totVta += tmpApa.getCostoVenta();
					numApa++;
				} else 
					tmpDel.add(tmpApa);
			}
			if(tmpDel.size() > 0)
				tmpCli.getAparatosAuditivos().removeAll(tmpDel);
		}
		
		dtRp.put("clientes", res);
		dtRp.put("totalaparatos", numApa);
		dtRp.put("vtaaparatos", totVta);
	}
	
	public void repVentasDiarias() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		List<Object[]> lstDetalles = new ArrayList<Object[]>();
		HashMap<String, Object> lstTotales = new HashMap<String, Object>();
		List<Object[]> lstTemp = new ArrayList<Object[]>();
		Double tmpTotal = 0d;
		String hqlCond = "";
		
		if(fechaInicio != null && fechaFin == null) 
			hqlCond += " AND x.venta.fechaVenta >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hqlCond += " AND x.venta.fechaVenta <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hqlCond += " AND x.venta.fechaVenta BETWEEN :f1 AND :f2 ";
		else 
			hqlCond += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursal != null) 
			hqlCond += " AND x.venta.sucursal = :suc ";
		else
			hqlCond += " AND (:suc = :suc OR 1 = 1) ";
		
		//Vamos sacando cada cosa en una lista generica
		
		//##### Examenes medicos	
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("EXA")) {
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'EXA' " +
					"	FROM DetVentaProdServ x, Service s " +
					"	WHERE s.codigo = x.codExacto AND s.tipoServicio = 'EXA' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, Service s " +
					"	WHERE s.tipoServicio = 'EXA' AND s.codigo = x.codExacto ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("EXA", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("MED")) {
			//##### Servicios de consulta
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'MED' " +
					"	FROM DetVentaProdServ x, Service s " +
					"	WHERE s.tipoServicio = 'MED' AND s.codigo = x.codExacto ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, Service s " +
					"	WHERE s.tipoServicio = 'MED' AND s.codigo = x.codExacto ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("MED", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("RPR")) {
			//##### Reparaciones de aparatos
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'REP' " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'RPR' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'RPR' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("RPR", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("LMP")) {
			//##### Limpiezas
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'LMP' " +
					"	FROM DetVentaProdServ x, ReparacionCliente r WHERE r.proceso.prcCode = 'LMP' " +
					"	AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, ReparacionCliente r WHERE r.proceso.prcCode = 'LMP' " +
					"	AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("LMP", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("MLD")) {
			//##### Fabricacion de moldes
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'MLD' " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'MLD' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'MLD' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("MLD", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("ENS")) {
			//##### Ensamblaje de aparatos
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'ENS' " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'ENS' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x, ReparacionCliente r " +
					"	WHERE r.proceso.prcCode = 'ENS' AND r.id = x.venta.idDetalle AND x.venta.tipoVenta = 'TLL' ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("ENS", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("ITM")) {
			//##### Items
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'ITM' " +
					"	FROM DetVentaProdServ x WHERE x.venta.tipoVenta = 'ITM' ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x WHERE x.venta.tipoVenta = 'ITM' ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		lstTotales.put("ITM", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		if(getValCmb4() == null || getValCmb4().trim().equals("") || getValCmb4().equals("CMB")) {
			//##### Combos
			lstTemp = new ArrayList<Object[]>();
			hql = "SELECT x.detalle, x.cantidad, x.monto, x.venta.usrEfectua, x.venta.sucursal, 'CMB' " +
					"	FROM DetVentaProdServ x WHERE (x.codClasifVta = 'CMB' OR x.codClasifVta = 'CMBBIN') ";
			
			lstTemp = entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getResultList();
			
			lstDetalles.addAll(lstTemp);
			//Sacamos el total
			hql = "SELECT SUM(x.cantidad * x.monto) " +
					"	FROM DetVentaProdServ x WHERE (x.codClasifVta = 'CMB' OR x.codClasifVta = 'CMBBIN') ";
			tmpTotal = (Double)entityManager.createQuery(hql + hqlCond)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("suc", sucursal)
					.getSingleResult();
		}
		
		lstTotales.put("CMB", tmpTotal==null?0d:tmpTotal);
		tmpTotal = 0d;
		
		lstTotales.put("TOT", Double.parseDouble(lstTotales.get("CMB").toString()) + 
				Double.parseDouble(lstTotales.get("ITM").toString()) +
				Double.parseDouble(lstTotales.get("LMP").toString()) +
				Double.parseDouble(lstTotales.get("RPR").toString()) +
				Double.parseDouble(lstTotales.get("MLD").toString()) +
				Double.parseDouble(lstTotales.get("ENS").toString()) +
				Double.parseDouble(lstTotales.get("EXA").toString()) +
				Double.parseDouble(lstTotales.get("MED").toString()) );
		
		dtRp.put("lst", lstDetalles);
		dtRp.put("totales", lstTotales);
	}
	
	public void repGananciasVentas() {
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		String hqlFcha = " ";
		if(fechaInicio != null && fechaFin == null) 
			hqlFcha += " AND x.venta.fechaVenta >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hqlFcha += " AND x.venta.fechaVenta <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hqlFcha += " AND x.venta.fechaVenta BETWEEN :f1 AND :f2 ";
		else
			hqlFcha = " AND (:f1 = :f2 OR 1 = 1) ";
		
		Double gananciasTlls = 0d;
		Double gananciasItms = 0d;
		Double gananciasCmbs = 0d;
		
		List<HashMap<String, Object>> lstGanancias = new ArrayList<HashMap<String, Object>>();
		List lstProductosVen = new ArrayList();
		
		hql = "SELECT c FROM Sucursal c WHERE 1 = 1  ";
		
		if(sucursal != null) 
			hql += " AND c = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		List<Sucursal> sucursalesInv = entityManager.createQuery(hql)
				.setParameter("suc", sucursal)
				.getResultList();
		
		hql = "SELECT x.codExacto, x.detalle, SUM(x.cantidad) as qnt, " +
				"	SUM((x.monto * x.cantidad) - (x.costo * x.cantidad)) as gan " +
				"	FROM DetVentaProdServ x WHERE 1 = 1 " + hqlFcha +
				"	AND x.codClasifVta IN ('PRD', 'PRQ') ";
		
		if(sucursal != null) 
			hql += " AND x.venta.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR 1 = 1) ";
		
		hql += " GROUP BY x.codExacto, x.detalle ORDER BY x.codExacto ASC ";
		
		if(isFlag1()) {
			lstProductosVen = entityManager.createQuery(hql)
					.setParameter("suc", sucursal)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.getResultList(); 
		}
		
		for(Sucursal tmpSuc : sucursalesInv) {
			
			Double gananSucItm = 0d;
			Double gananSucCmb = 0d;
			Double gananSucTll = 0d;
			
			//Sacamos las ganancias de productos vendidos por separado
			hql = "SELECT SUM((x.monto * x.cantidad) - (x.costo * x.cantidad)) " +
					"	FROM DetVentaProdServ x WHERE 1 = 1 " + hqlFcha +
					"	AND x.venta.sucursal = :suc AND x.venta.tipoVenta = 'ITM' " +
					"	AND x.codClasifVta = 'PRD' ";
			gananSucItm = (Double)entityManager.createQuery(hql)
					.setParameter("suc", tmpSuc)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.getSingleResult();
			
			//Sacamos las ganancias de productos vendidos como combos
			hql = "SELECT SUM((x.monto * x.cantidad) - (x.costo * x.cantidad)) " +
					"	FROM DetVentaProdServ x WHERE 1 = 1 " + hqlFcha +
					"	AND	x.venta.sucursal = :suc AND x.venta.tipoVenta = 'CMB' " +
					"	AND x.codClasifVta = 'PRD' ";
			gananSucCmb = (Double)entityManager.createQuery(hql)
					.setParameter("suc", tmpSuc)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.getSingleResult();
			
			//Sacamos las ganancias de productos utilizados en trabajos de taller
			hql = "SELECT SUM((x.monto * x.cantidad) - (x.costo * x.cantidad)) " +
					"	FROM DetVentaProdServ x WHERE 1 = 1 " + hqlFcha +
					"	AND	x.venta.sucursal = :suc AND x.venta.tipoVenta = 'TLL' " +
					"	AND x.codClasifVta = 'PRQ' ";
			gananSucTll = (Double)entityManager.createQuery(hql)
					.setParameter("suc", tmpSuc)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.getSingleResult();
			
			gananSucTll=gananSucTll==null?0d:gananSucTll;
			gananSucCmb=gananSucCmb==null?0d:gananSucCmb;
			gananSucItm=gananSucItm==null?0d:gananSucItm;
			
			HashMap<String, Object> ganPrd = new HashMap<String, Object>();
			ganPrd.put("itm", gananSucItm);
			ganPrd.put("tll", gananSucTll);
			ganPrd.put("cmb", gananSucCmb);
			ganPrd.put("suc", tmpSuc);
			
			lstGanancias.add(ganPrd);
			
			gananciasTlls += gananSucTll;
			gananciasItms += gananSucItm;
			gananciasCmbs += gananSucCmb;
		}
		
		dtRp.put("lstsuc", lstGanancias);
		dtRp.put("lstprd", lstProductosVen);
		dtRp.put("tottll", gananciasTlls);
		dtRp.put("totitm", gananciasItms);
		dtRp.put("totcmb", gananciasCmbs);
	}
	
	public void repConsolidadoItems() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT v.codExacto, v.detalle, SUM(v.cantidad), SUM(v.monto)  FROM DetVentaProdServ v " +
				"	WHERE 1 = 1 AND v.codClasifVta NOT IN ('CMB', 'CMBBIN') ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND v.venta.fechaVenta >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND v.venta.fechaVenta <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND v.venta.fechaVenta BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		}
				
		if(sucursal != null) 
			hql += " AND v.venta.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		if(getValCmb1() != null && !getValCmb1().equals("")) 
			hql += " AND v.codExacto LIKE :cod ";
		else
			hql += " AND (:cod IS NULL OR 1 = 1) ";
		
		if(getValCmb2() == null || getValCmb2().equals(""))
			hql+=" AND (:tpv IS NULL OR 1 = 1) ";
		else
			hql+=" AND v.venta.tipoVenta = :tpv  ";
		
		hql += " GROUP BY v.codExacto, v.detalle ORDER BY SUM(v.cantidad) DESC ";
		
		List<Object[]> res = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursal)
				.setParameter("cod", getValCmb1().concat("%"))
				.setParameter("tpv", getValCmb2())
				.getResultList();
		
		//Calculamos total de los productos y servicios
		Integer numDet = 0;
		Float totDet = 0f;
		
		for(Object[] tmpDet: res) {
			numDet += Integer.parseInt(tmpDet[2].toString());
			totDet += Float.parseFloat(tmpDet[3].toString());
		}
		
		dtRp.put("detventas", res);
		dtRp.put("totaldetalles", numDet);
		dtRp.put("vtadetalles", totDet);
	}
	
	public void repDetallesVenta() {
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		detVentas = new ArrayList<DetVentaProdServ>();
		List<Sucursal> subSucFlt = new ArrayList<Sucursal>();
		String condGnr = "";
		
		hql = "SELECT x FROM DetVentaProdServ x WHERE 1 = 1  ";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.venta.fechaVenta >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.venta.fechaVenta <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.venta.fechaVenta BETWEEN :f1 AND :f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(getValCmb1() == null || getValCmb1().equals(""))
			condGnr+=" AND (:tpv IS NULL OR :tpv = '') ";
		else
			condGnr+=" AND x.venta.tipoVenta = :tpv  ";

		if(getValCmb4() == null || getValCmb4().equals(""))
			condGnr+=" AND (:est IS NULL OR 1 = 1) ";
		else
			condGnr+=" AND x.venta.estado = :est  ";
		
		if(getValCmb5() == null || getValCmb5().equals(""))
			condGnr+=" AND (:cod IS NULL OR 1 = 1) ";
		else
			condGnr+=" AND x.codExacto LIKE :cod ";
		
		if(getFltObj1() == null || getFltObj1().equals(""))
			condGnr+=" AND (:usr IS NULL OR 1 = 1) ";
		else
			condGnr+=" AND x.venta.usrEfectua = :usr ";
		
		if(sucursal != null) {
			subSucFlt = entityManager
					.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc OR s = :suc2 OR s.sucursalSuperior = :suc2 ")
					.setParameter("suc", sucursal)
					.setParameter("suc2", sucursal.getSucursalSuperior())
					.getResultList(); 
			
			if(subSucFlt == null || subSucFlt.size() <= 0) 
				subSucFlt = new ArrayList<Sucursal>();
			
			condGnr += " AND (x.venta.sucursal IN (:suc) ) ";
		} else
			condGnr += " AND (x.venta.sucursal IN (:suc) OR 1 = 1) ";
		
		subSucFlt.add(sucursal);
		//FIltro wildcard de codigos de servicio
		
		if(codsServ != null && !codsServ.trim().equals("")) {
			condGnr += " AND ( ";
			//Hacemos split de cada comita
			String[] arrBusq = codsServ.split(",");
			int cnt = 0;
			for(String tmpB : arrBusq) {
				condGnr += " x.codClasifVta LIKE '" + tmpB + "%' " ;
				cnt++;
				if(cnt != arrBusq.length)
					hql += " OR ";
			}
			condGnr += ") ";
		}

		detVentas = entityManager.createQuery(hql + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpv", getValCmb1())
				.setParameter("est", getValCmb4())
				.setParameter("usr", getFltObj1())
				.setParameter("suc", subSucFlt)
				.setParameter("cod", getValCmb5().concat("%"))
				.getResultList();
		
		//Sacamos tambien el detalle de los pacientes para saber el numero
		hql = "SELECT DISTINCT x.venta.cliente FROM DetVentaProdServ x WHERE 1 = 1 ";
		List<Cliente> clientes = entityManager.createQuery(hql + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpv", getValCmb1())
				.setParameter("est", getValCmb4())
				.setParameter("usr", getFltObj1())
				.setParameter("suc", sucursal)
				.setParameter("cod", getValCmb5().concat("%"))
				.getResultList();
		
		Integer cantidadPrds = 0;
		Integer cantidadServs = 0;
		Integer cantidadPrdsReq = 0;
		Integer cantidadCmbs = 0;
		
		//Calculamos total de ventas
		setTotDec1(0f);
		for(DetVentaProdServ tmpVt: detVentas) 
			setTotDec1(getTotDec1()+(tmpVt.getMonto()*tmpVt.getCantidad()));
	}
	
	
	
	//Nuevo 09/11/2016
	public void generarReporteDetalleVentas()
	{
		//filtros:
		//se pogra filtrar por: categoriaProducto(Producto,Detalle), rangoFechas(Venta), sucursal(Venta),NumeroSerie/Lote(Detalle)
		
		/*setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);*/
		
		//String jpql="";
		String jpql="SELECT det FROM DetVentaProdServ det Where 1=1 ";
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
				
		
		/*if(getValCmb1()==null)
			jpql +="AND det.producto!=null";
		else if(getValCmb1()=="ITM")
			jpql="SELECT det.cantidad,det.monto";
		else if(getValCmb1()=="CMB")*/
			
		
			if(fechaInicio != null && fechaFin == null) 
				jpql += " AND det.venta.fechaVenta >= :f1 AND :f2 IS NULL ";
			else if(fechaInicio == null && fechaFin != null)
				jpql += " AND det.venta.fechaVenta <= :f2 AND :f1 IS NULL ";
			else if(fechaInicio != null && fechaFin != null)
				jpql += " AND det.venta.fechaVenta BETWEEN :f1 AND :f2 ";
			else {
				jpql += "  AND det.venta.fechaVenta BETWEEN :f1 AND :f2 ";
				Calendar calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
				calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
				calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
				setFechaFin(resetTimeDate(calTmp.getTime(), 2));
			}
		
		
		//Con rango de fecha sin ningun filtro
		detalleVentas = entityManager.createQuery(jpql)
						.setParameter("f1", fechaInicio)
						.setParameter("f2", fechaFin)
						.getResultList();
		
		System.out.println("Tamanio "+ detalleVentas.size());
		
		Float suma=0f;
		List<DetVentaProdServ> listaDetalle= new ArrayList<DetVentaProdServ>();
		for(DetVentaProdServ detalleV: detalleVentas)
		{
			if(detalleV.getProducto()!=null)
			{
				if(catSelected==null)
				{
					detalleV.setDetalle(detalleV.getProducto().getNombre());
					detalleV.setMonto(detalleV.getCantidad()*detalleV.getMonto());
					suma+=detalleV.getMonto();
					
					listaDetalle.add(detalleV);
				}
				else if(catSelected.getNombre()==detalleV.getProducto().getCategoria().getCategoriaPadre().getNombre())
				{
					detalleV.setDetalle(detalleV.getProducto().getNombre());
					detalleV.setMonto(detalleV.getCantidad()*detalleV.getMonto());
					suma+=detalleV.getMonto();
					
					listaDetalle.add(detalleV);
				}
			}
			
			if(detalleV.getCombo()!=null)
			{
				int i=1;
				for(ItemComboApa item: detalleV.getCombo().getItemsCombo())
				{
					System.out.println("nombre "+ item.getProducto().getNombre());
					if(i==1)
					{
						if(catSelected==null)
						{
							detalleV.setDetalle(item.getProducto().getNombre());
							detalleV.setMonto(item.getProducto().getPrcNormal());
							detalleV.setCantidad(1);
							suma+=detalleV.getMonto();
							listaDetalle.add(detalleV);
						}
						else if(catSelected.getNombre()==item.getProducto().getCategoria().getCategoriaPadre().getNombre())
						{
							detalleV.setDetalle(item.getProducto().getNombre());
							detalleV.setMonto(item.getProducto().getPrcNormal());
							detalleV.setCantidad(1);
							suma+=detalleV.getMonto();
							listaDetalle.add(detalleV);
						}
					}
					else
					{
						if(catSelected==null)
						{
							DetVentaProdServ det2= new DetVentaProdServ();
							//det2=detalleV;
							det2.setCombo(detalleV.getCombo());
							det2.setCodExacto(detalleV.getCodExacto());
							det2.setVenta(detalleV.getVenta());
							det2.setCosto(detalleV.getCosto());
							det2.setMonto(detalleV.getMonto());
							det2.setNumSerie(detalleV.getNumSerie());
							det2.setNumLote(detalleV.getNumLote());
							det2.setDetalle(item.getProducto().getNombre());
							det2.setCantidad(1);
							det2.setMonto(item.getProducto().getPrcNormal());
							suma+=det2.getMonto();
							listaDetalle.add(det2);
						}
						else if(catSelected.getNombre()==item.getProducto().getCategoria().getCategoriaPadre().getNombre())
						{
							DetVentaProdServ det2= new DetVentaProdServ();
							//det2=detalleV;
							det2.setCombo(detalleV.getCombo());
							det2.setCodExacto(detalleV.getCodExacto());
							det2.setVenta(detalleV.getVenta());
							det2.setCosto(detalleV.getCosto());
							det2.setMonto(detalleV.getMonto());
							det2.setNumSerie(detalleV.getNumSerie());
							det2.setNumLote(detalleV.getNumLote());
							det2.setDetalle(item.getProducto().getNombre());
							det2.setCantidad(1);
							det2.setMonto(item.getProducto().getPrcNormal());
							suma+=det2.getMonto();
							listaDetalle.add(det2);
						}
						
						
					}
					i++;
					
					
				}
			}
		}
		
		setTotDec1(suma);
		
		detalleVentas = new ArrayList<DetVentaProdServ>();
		detalleVentas=listaDetalle;
	}
	
	public void repStockBodegas() {
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
			hql+=" AND v.tipoVenta = :tpv ";

		if(getValCmb4() == null || getValCmb4().equals(""))
			hql+=" AND (:est IS NULL OR :est = '') ";
		else
			hql+=" AND v.estado = :est ";

		ventas = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("tpv", getValCmb1())
				.setParameter("est", getValCmb4())
				.getResultList();
		
		//Calculamos total de ventas
		setTotDec1(0f);
		for(VentaProdServ tmpVt: ventas) 
			setTotDec1(getTotDec1()+tmpVt.getMonto());
	}
	
	public String getIntervaloTiempo() {
		return intervaloTiempo;
	}

	public void setIntervaloTiempo(String intervaloTiempo) {
		this.intervaloTiempo = intervaloTiempo;
	}

	public List<CotizacionComboApa> getCotizaciones() {
		return cotizaciones;
	}

	public void setCotizaciones(List<CotizacionComboApa> cotizaciones) {
		this.cotizaciones = cotizaciones;
	}

	public List<VentaProdServ> getVentas() {
		return ventas;
	}

	public void setVentas(List<VentaProdServ> ventas) {
		this.ventas = ventas;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public String getCodsServ() {
		return codsServ;
	}

	public void setCodsServ(String codsServ) {
		this.codsServ = codsServ;
	}

	public List<DetVentaProdServ> getDetVentas() {
		return detVentas;
	}

	public void setDetVentas(List<DetVentaProdServ> detVentas) {
		this.detVentas = detVentas;
	}

	public HashMap<String, Object> getDtRp() {
		return dtRp;
	}

	public void setDtRp(HashMap<String, Object> dtRp) {
		this.dtRp = dtRp;
	}

	public List<DetVentaProdServ> getDetalleVentas() {
		return detalleVentas;
	}

	public void setDetalleVentas(List<DetVentaProdServ> detalleVentas) {
		this.detalleVentas = detalleVentas;
	}

	public Categoria getCatSelected() {
		return catSelected;
	}

	public void setCatSelected(Categoria catSelected) {
		this.catSelected = catSelected;
	}

	public List<Object> getCotizacionCombos() {
		return cotizacionCombos;
	}

	public void setCotizacionCombos(List<Object> cotizacionCombos) {
		this.cotizacionCombos = cotizacionCombos;
	}

	public List<PojoCotizacionRp> getCotizacionesCombo() {
		return cotizacionesCombo;
	}

	public void setCotizacionesCombo(List<PojoCotizacionRp> cotizacionesCombo) {
		this.cotizacionesCombo = cotizacionesCombo;
	}
	
	
	

}


