package com.sa.inventario.action.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.model.inventory.Categoria;
import com.sa.model.sales.ComboAparato;
import com.sa.model.sales.CostoServicio;
import com.sa.model.sales.CotCmbsItems;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.sales.CotizacionCombos;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.ItemComboApa;
import com.sa.model.sales.PojoVentasApa;
import com.sa.model.security.Sucursal;
import com.sun.org.apache.bcel.internal.generic.NEW;

@Name("repCotizacion")
@Scope(ScopeType.CONVERSATION)
public class RepCotizacion extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<CotizacionComboApa> cotizacionListDetalle = new ArrayList<CotizacionComboApa>();
	private List<Object[]> cotizaciones= new ArrayList<Object[]>();
	private List<PojoVentasApa> listaVentasApa=new ArrayList<PojoVentasApa>();
	
	private Sucursal sucursalFlt;
	private String estado;
	private String ladoFilter;
	private Categoria categoriaSelected;
	private String nombreCat="";
	private String ladoApa="";
	private int sumaIzq=0;
	private int sumaDer=0;
	
	
	@In
	private EntityManager entityManager;
	
	
	
	public void cargarCotizaciones() {
		
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		//String jpql="SELECT c FROM CotizacionComboApa c where 1=1 ";
		
		String jpql="SELECT c.id,c.fechaIngreso,c.estado,c.sucursal,c,cotcm.combo.nombre,cotCmbsItm.item.categoria.nombre,c.cliente.nombres,c.usuarioGenera.nombreCompleto FROM " +
				" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";
				
				
		if(fechaInicio != null && fechaFin == null) 
			jpql += " AND c.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			jpql += " AND c.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			jpql += " AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
		else {
			jpql += "  AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
				
		
		//if (sucursalFlt == null)
			//sucursalFlt = loginUser.getUser().getSucursal();
		
		
		String condiciones=" AND c.cotizacionComboBin = NULL AND cotCmbsItm.item.principal=true";
		
		
		//Para filtrar por estados
		//condiciones+=" c.estado= 'PEN'";
		
		/*if(estado!=null && estado!="")
			condiciones+=" AND c.estado = '"+estado+"'";*/
		
		if(estado!=null && estado!="")
			condiciones+=" AND c.estado = '"+estado+"'";
		
		if(categoriaSelected!=null)
			condiciones+=" AND cotCmbsItm.item.categoria.nombre='"+categoriaSelected.getNombre()+"'";
		
		
		condiciones+=" ORDER BY c.fechaIngreso DESC";
		
		//para filtrar por sucursal
		//condiciones+=" AND c.sucursal = :suc";
		
		jpql+=condiciones;
		
		
		cotizaciones = entityManager.createQuery(jpql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		
		/*cotizacionListDetalle = entityManager.createQuery(jpql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();*/
				//.setParameter("suc", sucursalFlt).getResultList();

	}
	
	public Double calcularPrecioCotizado(CotizacionComboApa cotizacion)
	{
	
		ComboAparato combo;
		
		float total = 0.0f;
		
		/*if(cotizacion.getSelComboId()!=null)
			combo=(ComboAparato) entityManager.createQuery("SELECT c FROM ComboAparato c where c.id="+cotizacion.getSelComboId()+" ").getSingleResult();
		else
		{*/
			combo=cotizacion.getCmbCotizados().get(0).getCombo();//Primer cotizacion
			//CotCmbsItems coCmb=cotizacion.getCmbCotizados().get(0).getItemsCotizados().get(0);
		//}
		
		/*	if (total == 0){
				
				float subtotal = 0.0f;	
				for (ItemComboApa tmpItem: combo.getItemsCombo()){
					if (tmpItem != null && tmpItem.getPrecioCotizado() > 0)
						subtotal += tmpItem.getPrecioCotizado();
						else if (tmpItem != null){
						if (tmpItem.getTipoPrecio().equals("NRM"))
							subtotal += tmpItem.getProducto().getPrcNormal();
						else if (tmpItem.getTipoPrecio().equals("MIN"))
							subtotal += tmpItem.getProducto().getPrcMinimo();
						else if (tmpItem.getTipoPrecio().equals("OFE"))
							subtotal += tmpItem.getProducto().getPrcOferta(); 
					}
				}*/
		//cotizacion.getCmbCotizados().get(0).getItemsCotizados().get(0).getItem().getItemsCotizados();
		//cotizacion.getCmbCotizados().get(0).getItemsCotizados().get(0).get
		if (total == 0){
			
			float subtotal = 0.0f;	
			List<CotCmbsItems> lsItemsCots = entityManager.createQuery("SELECT c FROM CotCmbsItems c where c.ctCmbs.id="+combo.getCmbCotizados().get(0).getId()+" ").getResultList();
			//for (CotCmbsItems tmpItem: cotizacion.getCmbCotizados().get(0).getItemsCotizados()){
			for (CotCmbsItems tmpItem: lsItemsCots){
				if (tmpItem != null && tmpItem.getPrecioCotizado()!=null && tmpItem.getPrecioCotizado() > 0)
				{
					/*subtotal += tmpItem.getPrecioCotizado(); comentado el 11/08/2017
					else if (tmpItem != null){*/
					if (tmpItem.getTipoPrecio().equals("NRM"))
						subtotal += tmpItem.getPrecioCotizado();
					else if (tmpItem.getTipoPrecio().equals("MIN"))
						subtotal += tmpItem.getPrecioCotizado();
					else if (tmpItem.getTipoPrecio().equals("OFE"))
						subtotal += tmpItem.getPrecioCotizado();
				}
				//}
			}
				
				total += subtotal;
				//obtener los costos de los diferentes servicios del combo
				for (CostoServicio tmpCst : combo.getCostosCombo()) {
					total += tmpCst.getServicio().getCosto().floatValue();
				}
				
			
				//Para binaural cotizado
				if(cotizacion.getHijoBin().size()>0 && cotizacion.getHijoBin()!=null)
				{
					
					
					subtotal = 0.0f;
					CotizacionCombos cmBin = cotizacion.getHijoBin().get(0).getCmbCotizados().get(0);
					List<CotCmbsItems> lsItemsCotsBin = entityManager.createQuery("SELECT c FROM CotCmbsItems c where c.ctCmbs.id="+cmBin.getId()+" ").getResultList();
					//for (CotCmbsItems tmpItem: cotizacion.getHijoBin().get(0).getCmbCotizados().get(0).getItemsCotizados()){
					for (CotCmbsItems tmpItem: lsItemsCotsBin){
						if (tmpItem != null && tmpItem.getPrecioCotizado() > 0)
							subtotal += tmpItem.getPrecioCotizado();
							else if (tmpItem != null){
							if (tmpItem.getTipoPrecio().equals("NRM"))
								subtotal += tmpItem.getPrecioCotizado();
							else if (tmpItem.getTipoPrecio().equals("MIN"))
								subtotal += tmpItem.getPrecioCotizado();
							else if (tmpItem.getTipoPrecio().equals("OFE"))
								subtotal += tmpItem.getPrecioCotizado(); 
						}
					}
						
						total += subtotal;
						//obtener los costos de los diferentes servicios del combo
						for (CostoServicio tmpCst : combo.getCostosCombo()) {
							total += tmpCst.getServicio().getCosto().floatValue();
						}
					
					
					
				}
				
				
			}
		
		
		return moneyDecimal(total);
		
	}
	
	public Double calcularPrecioVenta(CotizacionComboApa cotizacion)
	{
		
		float precioVenta=0f;
		//float precioVenta=(Float) entityManager.createQuery("SELECT v.monto FROM VentaProdServ v Where v.cotizacion,id="+cotizacion.getId()+"").getSingleResult();
		
		if(cotizacion.getVenta()!=null)
			precioVenta=cotizacion.getVenta().getMonto();
		
		return moneyDecimal(precioVenta);
	}
	
	public Double calcularPrecioVtaLados(CotizacionComboApa cotizacion)
	{
		
		float precioVenta=0f;
		if(cotizacion.getVenta()!=null)
		{
			for(DetVentaProdServ det: cotizacion.getVenta().getDetVenta())
			{
				if(det.getCodCoti()!=null)
				{
					if(cotizacion.getId().equals(det.getCodCoti()))
						precioVenta=det.getMonto();
				}
			}
		}
		
		return moneyDecimal(precioVenta);
	}
	
	public Double calcularPrecioVtaLadosBin(CotizacionComboApa cotizacion)
	{
		
		float precioVenta=0f;
		if(cotizacion.getVenta()!=null)
		{
			for(DetVentaProdServ det: cotizacion.getVenta().getDetVenta())
			{
				if(det.getCodCoti()!=null)
				{
					if(cotizacion.getHijoBin().get(0).getId().equals(det.getCodCoti()))
						precioVenta=det.getMonto();
				}
			}
		}
		
		return moneyDecimal(precioVenta);
	}
	
	
	
	public void cargarCotizacionesCliente() {
		
		cotizaciones.clear();
		
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		//String jpql="SELECT c FROM CotizacionComboApa c where 1=1 ";
		//					0		1				2		3		4 		5              6 								7
		String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,CONCAT(c.cliente.nombres,' ',c.cliente.apellidos),c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
				" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";
			
		
		
		if(estado==null || estado.equals("PEN"))
		{
			System.out.println("Entro a estado pendiente o nulo");
			System.out.println("Estado = "+ estado);
			
			if(fechaInicio != null && fechaFin == null) 
				jpql += " AND c.fechaIngreso >= :f1 AND :f2 IS NULL ";
			else if(fechaInicio == null && fechaFin != null)
				jpql += " AND c.fechaIngreso <= :f2 AND :f1 IS NULL ";
			else if(fechaInicio != null && fechaFin != null)
				jpql += " AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
			else {
				jpql += "  AND c.fechaIngreso BETWEEN :f1 AND :f2 ";
				Calendar calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
				calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
				calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
				setFechaFin(resetTimeDate(calTmp.getTime(), 2));
			}
		}
		else
		{
			System.out.println("Entro a estado de venta o cotizacion");
			System.out.println("estado= "+ estado);
			
			if(fechaInicio != null && fechaFin == null) 
				jpql += " AND c.fechaVenta >= :f1 AND :f2 IS NULL ";
			else if(fechaInicio == null && fechaFin != null)
				jpql += " AND c.fechaVenta <= :f2 AND :f1 IS NULL ";
			else if(fechaInicio != null && fechaFin != null)
				jpql += " AND c.fechaVenta BETWEEN :f1 AND :f2 ";
			else {
				jpql += "  AND c.fechaVenta BETWEEN :f1 AND :f2 ";
				Calendar calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
				calTmp = new GregorianCalendar();
				calTmp.set(Calendar.DATE, 1);
				calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
				calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
				setFechaFin(resetTimeDate(calTmp.getTime(), 2));
			}
		}
			
				
		
		//if (sucursalFlt == null)
			//sucursalFlt = loginUser.getUser().getSucursal();
		
		
		//String condiciones=" AND c.cotizacionComboBin = NULL AND cotCmbsItm.item.principal=true";
		String condiciones=" AND c.cotizacionComboBin = NULL";
		//String condiciones="";
		
		//Para filtrar por estados
		//condiciones+=" c.estado= 'PEN'";
		
		if(estado!=null && !estado.equals(""))
		{
			System.out.println("condicion de estado si");
			if(estado.equals("PEN"))
			{
				condiciones+=" AND c.estado = 'PEN'";
				System.out.println("condiones de estado pendiente");
			}
			else
			{
				condiciones+=" AND c.estado != 'PEN'";
				System.out.println("condiones de estado vendido o cotizado");
			}
		}
			
		
		if(categoriaSelected!=null)
			condiciones+=" AND cotCmbsItm.item.categoria.nombre='"+categoriaSelected.getNombre()+"'";
		
		
		condiciones+=" GROUP BY c.id,CONCAT(c.cliente.nombres,' ',c.cliente.apellidos),c.usuarioGenera.nombreCompleto ORDER BY c.fechaIngreso DESC";
		
		//para filtrar por sucursal
		//condiciones+=" AND c.sucursal = :suc";
		
		jpql+=condiciones;
		
		try {
			
			cotizaciones = entityManager.createQuery(jpql)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.getResultList();	
			
		} catch (Exception e) {

			FacesMessages.instance().add(Severity.WARN,"Problema al cargar los datos");
			e.printStackTrace();
			return;
		}
		
		/*cotizacionListDetalle = entityManager.createQuery(jpql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();*/
				//.setParameter("suc", sucursalFlt).getResultList();
	}
	
	public String esBinaural(CotizacionComboApa coti)
	{
		if(coti.getHijoBin()!=null && coti.getHijoBin().size()>0)
			return "si";
		else
			return "no";
	}
	
	
	public String obtenerLado(CotizacionComboApa cot) {
		if (cot.getHijoBin() != null && cot.getHijoBin().size() > 0) {
			return "Binaural";
		} 
		else if (cot.getLadoAparato().equals("DER"))
			return "Oido derecho";
		else
			return "Oido izquierdo";
	}
	
	/*public long calcularDiasCotizados(Date fecha)
	{
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		Date fechaActual= new Date();
		long dias=(fechaActual.getTime()-fecha.getTime())/MILLSECS_PER_DAY;
		return dias;
		
	}*/
	
	public long calcularDiasTranscurridos(CotizacionComboApa cot)
	{
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		long dias=0;
		Date fechaActual=new Date();
		
		if(cot.getEstado().equals("PEN"))
		{
			
			dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else if(cot.getEstado().equals("COT"))
		{
			if(cot.getFechaCredito()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else if(cot.getEstado().equals("APL") && cot.getFechaCredito()!=null)
		{
			
				dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else
		{
			if(cot.getFechaVenta()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaVenta().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		
		return dias;
		
	}
	
	
	public String calcularTiempoVenta(CotizacionComboApa cot)
	{
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		long dias=0;
		String dato;
		Date fechaActual=new Date();
		
		if(cot.getEstado().equals("PEN"))
		{
			dato="";
			//dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else if(cot.getEstado().equals("COT"))
		{
			if(cot.getFechaCredito()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			
			dato=Long.toString(dias);
		}
		else if(cot.getEstado().equals("APL") && cot.getFechaCredito()!=null)
		{
			
			dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			dato=Long.toString(dias);
		}
		else
		{
			if(cot.getFechaVenta()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaVenta().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			
			dato=Long.toString(dias);
		}
		
		return dato;
		
	}
	
	public Double calcularSumaCotizacion(CotizacionComboApa c,Double montoActual)
	{
		
		Double suma=0d;
		
		Double montoAct=0d;
		if(c.getEstado().equals("PEN"))
		{
			
			System.out.println("cotizacion pendiente");
			montoAct=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
					" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND cotcm.id=(SELECT MIN(id) FROM CotizacionCombos cc where cc.cotizacion.id="+c.getId()+")  AND c.id="+c.getId()+" ").getSingleResult();
			
			//return moneyDecimal(montoActual);//este monto deberia ser solo del primer combo
			//return moneyDecimal(suma);
		}
		else //Si se vendio ese lado
		{
			if(c.getSelComboId()!=null)
			{
				int idCombo=c.getSelComboId(); //retorna la suma de los totales del combo seleccionado si ya fue vendido
				montoAct=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
						" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND c.id="+c.getId()+" AND cotcm.combo.id="+idCombo+" ").getSingleResult();
				System.out.println("cotizacion vendida" + suma); //123
			}
			//return moneyDecimal(suma);
		}
		
		
				if(c.getHijoBin()!=null && c.getHijoBin().size()>0) //Si es binaural se sumas los totales de los combos seleccionados
				{
					
					if(c.getEstado().equals("PEN")) //Si solo esta cotizado, se toma los totales de los combos cotizados (solo se tomara el primero, tomar en cuenta)
					{
						suma=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND cotcm.id=(SELECT MIN(id) FROM CotizacionCombos cc where cc.cotizacion.cotizacionComboBin.id="+c.getId()+")  AND c.cotizacionComboBin.id="+c.getId()+" ").getSingleResult();
						System.out.println("cotizacion binaural pendiente" + suma);
					}
					else //Si se vendio se tomaran los totales del combo vendido
					{
						//Si se vendio hay q tomar en cuenta si tambien se vendio el lado binaural
						
						if(c.getHijoBin().get(0).getSelComboId()!=null)
						{
						
							//int idCombo=c.getCotizacionComboBin().getSelComboId();
							System.out.println("id cotizacion binaural "+ c.getHijoBin().get(0).getId());
							System.out.println("id combosel binaural "+ c.getHijoBin().get(0).getSelComboId());
							
							int idCombo=(Integer) entityManager.createQuery("SELECT selComboId FROM CotizacionComboApa cc where cc.id="+c.getHijoBin().get(0).getId()+" ").getSingleResult();
									
							suma=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
									" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND c.cotizacionComboBin.id="+c.getId()+" AND cotcm.combo.id="+idCombo+" ").getSingleResult();
							System.out.println("suma de cotizacion binaural vendida" + suma); //123
							System.out.println("idCombo binaural "+idCombo);
							
						}
					}
					
					
					
					System.out.println("la suma es "+ suma);
					System.out.println("id cotizacion "+c.getId());
					System.out.println("id cotizacion bin "+c.getHijoBin().get(0).getId());
					//return moneyDecimal(suma+montoActual);
				}
		/*else
		{
			if(c.getEstado().equals("PEN"))
			{
				
				System.out.println("cotizacion pendiente");
				suma=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
						" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND cotcm.id=(SELECT MIN(id) FROM CotizacionCombos cc where cc.cotizacion.id="+c.getId()+")  AND c.id="+c.getId()+" ").getSingleResult();
				
				//return moneyDecimal(montoActual);//este monto deberia ser solo del primer combo
				return moneyDecimal(suma);
			}
			else 
			{
				int idCombo=c.getSelComboId(); //retorna la suma de los totales del combo seleccionado si ya fue vendido
				suma=(Double) entityManager.createQuery("SELECT SUM(cotCmbsItm.precioCotizado) FROM " +
						" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c AND c.id="+c.getId()+" AND cotcm.combo.id="+idCombo+" ").getSingleResult();
				System.out.println("cotizacion vendida" + suma); //123
				return moneyDecimal(suma);
			}
		}*/
		
		return moneyDecimal(montoAct+suma);
	}
	
	/*public long calcularDiasVenta(Date fechaVenta,Date fechaCotizacion)
	{
		long dias=fechaVenta.getTime()-fechaCotizacion.getTime();
		return dias;
	}*/
	
	public String definirAlertas(CotizacionComboApa cot)
	{
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		/*Date fechaActual= new Date();
		long dias=(fechaActual.getTime()-fecha.getTime())/MILLSECS_PER_DAY;*/
		long dias;
		
		Date fechaActual=new Date();
		
		if(cot.getEstado().equals("PEN"))
		{
			dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else if(cot.getEstado().equals("COT"))
		{
			if(cot.getFechaCredito()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else if(cot.getEstado().equals("APL") && cot.getFechaCredito()!=null)
		{
			
			dias= (cot.getFechaCredito().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else
		{
			if(cot.getFechaVenta()==null)
				dias=(fechaActual.getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
			else
				dias= (cot.getFechaVenta().getTime()-cot.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		
		if(dias<=10)
			return "/kubeImg/enabled.png";
		else if(dias>10 && dias <20)
			return "/kubeImg/warn.png";
		else//(dias>=20)
			return "/kubeImg/alert.png";
	}
	
	public String obtenerFechaVenta(CotizacionComboApa coti)
	{
		
		if(coti.getFechaCredito()!=null)
			return coti.getFechaCredito().toString();
		else if(coti.getFechaCredito()==null && coti.getFechaVenta()!=null)
			return coti.getFechaVenta().toString();
		else
			return "";
	}
	
	
	
	public void reporteVentasApa()
	{
		sumaIzq=0;
		sumaDer=0;
		cotizaciones.clear();
		listaVentasApa.clear();
		
		
		//Verificamos si pusieron alguna fecha
				setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
				setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
				
				//String jpql="SELECT c FROM CotizacionComboApa c where 1=1 ";
				//					0		1				2	 3		4 		5              6 								7
				String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
						" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";
					
				
			
					//System.out.println("Entro a estado de venta o cotizacion");
					//System.out.println("estado= "+ estado);
					
					if(fechaInicio != null && fechaFin == null) 
						jpql += " AND c.fechaVenta >= :f1 AND :f2 IS NULL ";
					else if(fechaInicio == null && fechaFin != null)
						jpql += " AND c.fechaVenta <= :f2 AND :f1 IS NULL ";
					else if(fechaInicio != null && fechaFin != null)
						jpql += " AND c.fechaVenta BETWEEN :f1 AND :f2 ";
					else {
						jpql += "  AND c.fechaVenta BETWEEN :f1 AND :f2 ";
						Calendar calTmp = new GregorianCalendar();
						calTmp.set(Calendar.DATE, 1);
						setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
						calTmp = new GregorianCalendar();
						calTmp.set(Calendar.DATE, 1);
						calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
						calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
						setFechaFin(resetTimeDate(calTmp.getTime(), 2));
				
					}
				
				//if (sucursalFlt == null)
					//sucursalFlt = loginUser.getUser().getSucursal();
				
				
				//String condiciones=" AND c.cotizacionComboBin = NULL AND cotCmbsItm.item.principal=true";
				String condiciones=" AND c.cotizacionComboBin = NULL";
				//String condiciones="";
				
				condiciones+=" AND c.estado ='APL'";
					
				
				if(categoriaSelected!=null)
					condiciones+=" AND cotCmbsItm.item.categoria.categoriaPadre.nombre='"+categoriaSelected.getNombre()+"'";
				
				
				if(ladoApa!=null && !ladoApa.equals(""))
					condiciones+=" AND c.ladoAparato='"+ladoApa+"'";
				
				
				condiciones+=" GROUP BY c.id,c.cliente.nombres,c.usuarioGenera.nombreCompleto ORDER BY c.fechaIngreso DESC";
				
				//para filtrar por sucursal
				//condiciones+=" AND c.sucursal = :suc";
				
				
				jpql+=condiciones;
				
				
				cotizaciones = entityManager.createQuery(jpql)
						.setParameter("f1", fechaInicio)
						.setParameter("f2", fechaFin)
						.getResultList();
				
				
		List<Object[]> listaItems;
				
				
		PojoVentasApa ventasApa;
		Float sumaIz=0f,sumaDer=0f;
		
		for(Object[] cotiz: cotizaciones) //Todas las cotizaciones vendidas
		{
		
			ventasApa = new PojoVentasApa();
			CotizacionComboApa coti =(CotizacionComboApa) cotiz[3];
			
			ventasApa.setCliente(coti.getCliente());
			ventasApa.setPrecioD(0d);
			ventasApa.setPrecioI(0d);
			ventasApa.setFechaVenta(coti.getFechaVenta());
			
			if(coti.getSelComboId()!=null)//Si se vendio ese lado hay que extraer el items principal y su bateria
			{
				
				//Query
				listaItems=new ArrayList<Object[]>();
				listaItems=entityManager.createQuery("SELECT item,cotiItem.precioCotizado FROM CotizacionCombos cotiCm,CotCmbsItems cotiItem,ItemComboApa item where cotiCm=cotiItem.ctCmbs AND item=cotiItem.item AND cotiCm.cotizacion.id="+coti.getId()+" and cotiCm.combo.id="+coti.getSelComboId()+" ").getResultList();
				ItemComboApa item;
				for(Object[] itemls:listaItems)
				{
					item=(ItemComboApa) itemls[0];
					
						
						//Verificar que lado se va a llenar
						if(coti.getLadoAparato().equals("IZQ"))
						{
														
							if(item.isPrincipal())
							{
							
								ventasApa.setCategoriaI(item.getCategoria());
								ventasApa.setMarcaI(item.getProducto().getMarca());
								//ventasApa.setModeloI(item.getProducto().getModelo());
								ventasApa.setModeloI(item.getProducto().getNombre());
								//ventasApa.setNumSerieI("55555");//Extraer numero serie
								ventasApa.setNumSerieI(buscarNumeroSerie(coti,1));
								//ventasApa.setPrecioI(ventasApa.getPrecioI()+(Float)itemls[1]);
								buscarNumSerie(coti);
								this.sumaIzq++;
							}
							else
							{
								ventasApa.setBateriaI(item.getProducto().getTipo()+item.getProducto().getModelo());
								//sumaIz+=(Float) itemls[1];
								//ventasApa.setPrecioI(ventasApa.getPrecioI()+(Float)itemls[1]);
							}
							
							ventasApa.setPrecioI(calcularPrecioVtaLados(coti));
							
						}
						else
						{
							//sino llenar derecho
							
							if(item.isPrincipal())
							{
							
								ventasApa.setCategoriaD(item.getCategoria());
								ventasApa.setMarcaD(item.getProducto().getMarca());
								//ventasApa.setModeloD(item.getProducto().getModelo());
								ventasApa.setModeloD(item.getProducto().getNombre());
								//ventasApa.setNumSerieD("55555");//Extraer numero serie
								ventasApa.setNumSerieD(buscarNumeroSerie(coti,1));
								
								//ventasApa.setPrecioD(ventasApa.getPrecioD()+(Float)itemls[1]);
								
								this.sumaDer++;
							}
							else
							{
								ventasApa.setBateriaD(item.getProducto().getTipo()+item.getProducto().getModelo());
								//sumaIz+=(Float) itemls[1];
								//ventasApa.setPrecioD(ventasApa.getPrecioD()+(Float)itemls[1]);
							}
							
							ventasApa.setPrecioD(calcularPrecioVtaLados(coti));
						}
						
						
					
				}
				
				
				
			}
			else //Sino se vendio hay q dejarlo vacio
			{
				
			}
			
			
			if(coti.getHijoBin()!=null && coti.getHijoBin().size() >0)//si tiene aparato binaural
			{
				
				if(coti.getHijoBin().get(0).getSelComboId()!=null)//Si se vendio hay q agregarlo
				{
					
					//Query
					listaItems=new ArrayList<Object[]>();
					listaItems=entityManager.createQuery("SELECT item,cotiItem.precioCotizado FROM CotizacionCombos cotiCm,CotCmbsItems cotiItem,ItemComboApa item where cotiCm=cotiItem.ctCmbs AND item=cotiItem.item AND cotiCm.cotizacion.cotizacionComboBin.id="+coti.getId()+" and cotiCm.combo.id="+coti.getHijoBin().get(0).getSelComboId()+" ").getResultList();
					ItemComboApa item;
					for(Object[] itemls:listaItems)
					{
						item=(ItemComboApa) itemls[0];
					
							//Verificar que lado se va a llenar
						if(coti.getHijoBin().get(0).getLadoAparato().equals("IZQ"))
						{
							//si es izquierdo hacer
							
							if(item.isPrincipal())
							{
							
								ventasApa.setCategoriaI(item.getCategoria());
								ventasApa.setMarcaI(item.getProducto().getMarca());
								//ventasApa.setModeloI(item.getProducto().getModelo());
								ventasApa.setModeloI(item.getProducto().getNombre());
								//ventasApa.setNumSerieI("777777");//Extraer numero serie
								ventasApa.setNumSerieI(buscarNumeroSerie(coti,2));
								//ventasApa.setPrecioI(ventasApa.getPrecioI()+(Float)itemls[1]);
								
								this.sumaIzq++;
							}
							else
							{
								ventasApa.setBateriaI(item.getProducto().getTipo()+item.getProducto().getModelo());
								//sumaIz+=(Float) itemls[1];
								//ventasApa.setPrecioI(ventasApa.getPrecioI()+(Float)itemls[1]);
							}
							
							ventasApa.setPrecioI(calcularPrecioVtaLadosBin(coti));
							System.out.println("Binaural izqierdo + idBin "+coti.getHijoBin().get(0).getId());
							System.out.println("Binaural izqierdo + total "+calcularPrecioVtaLadosBin(coti));
						}
						else
						{
							//si es derecho hacer
							
							if(item.isPrincipal())
							{
							
								ventasApa.setCategoriaD(item.getCategoria());
								ventasApa.setMarcaD(item.getProducto().getMarca());
								//ventasApa.setModeloD(item.getProducto().getModelo());
								ventasApa.setModeloD(item.getProducto().getNombre());
								//ventasApa.setNumSerieD("55555");//Extraer numero serie
								ventasApa.setNumSerieD(buscarNumeroSerie(coti,2));
								//ventasApa.setPrecioD(ventasApa.getPrecioD()+(Float)itemls[1]);
								
								this.sumaDer++; System.out.println("ENtrooo a derechooo Bin");
							}
							else
							{
								ventasApa.setBateriaD(item.getProducto().getTipo()+item.getProducto().getModelo());
								//sumaIz+=(Float) itemls[1];
								//ventasApa.setPrecioD(ventasApa.getPrecioD()+(Float)itemls[1]);
							}
							
							ventasApa.setPrecioD(calcularPrecioVtaLadosBin(coti));
							System.out.println("Binaural derecho + idBin "+coti.getHijoBin().get(0).getId());
							System.out.println("Binaural derecho + total "+calcularPrecioVtaLadosBin(coti));
						}
					}
					
				}
			}
			else
			{
				
			}
			
			
		//ventasApa.setPrecioTotal(moneyDecimal(ventasApa.getPrecioD()+ventasApa.getPrecioI()));
		ventasApa.setPrecioTotal(moneyDecimal(ventasApa.getPrecioD()+ventasApa.getPrecioI()));
		
		System.out.println("Precio total "+(ventasApa.getPrecioD()+ventasApa.getPrecioI()));
		if(coti.getVenta()!=null)
			ventasApa.setUsEntrego(coti.getVenta().getUsrEfectua());//Posiblemente ccrear usuario entrega en cotizacion para estar seguros.
		else
			ventasApa.setUsEntrego(coti.getUsuarioGenera());
			
		listaVentasApa.add(ventasApa);
		
			
		}//Fin for cotizacion
		
	}
	
	public String buscarNumSerie(CotizacionComboApa cot)
	{
		//Object[] obj=(Object[]) entityManager.createQuery("SELECT apa.numSerie FROM CotCmbsItems cotItm,Producto p,AparatoCliente apa,CotizacionComboApa coti,CotizacionCombos cotCom where cotCom.cotizacion=coti and cotItm.ctCmbs=cotCom and cotItm.item.producto=p and p.id=apa.idPrd and cotCom.cotizacion.id="+co.getId()+" group by apa.numSerie").getSingleResult();
		/*System.out.println("ACtual "+co.getId());
		List<Object[]> lis=entityManager.createQuery("SELECT apa.numSerie,coti.id FROM CotCmbsItems cotItm,Producto p,AparatoCliente apa,CotizacionComboApa coti,CotizacionCombos cotCom where cotCom.cotizacion=coti and cotItm.ctCmbs=cotCom and cotItm.item.producto=p and p.id=apa.idPrd and cotCom.cotizacion.id="+co.getId()+" group by coti.id").getResultList();
		
		for(Object[] obj:lis)
		{
			System.out.println("imprimiendo "+obj[1]);
			if(obj[1].equals(co.getId()))
			{
				System.out.println("NumSerie "+obj[0]);
				System.out.println("IDCOT "+obj[1]);
				//System.out.println("NumSerie "+obj[2]);
			}
			
		}*/
		List<String> lis = new ArrayList<String>();
		//List<Object[]> lis=entityManager.createQuery("SELECT det.numSerie,cotCom.cotizacion.id FROM CotizacionCombos cotCom,DetVentaProdServ det where cotCom.combo=det.combo and cotCom.cotizacion.id="+co.getId()+"").getResultList();
		if(cot.getFechaVenta()!=null)
		{
			lis=entityManager.createQuery("SELECT apa.numSerie FROM AparatoCliente apa where apa.cliente.id="+cot.getCliente().getId()+" and apa.ladoAparato='"+cot.getLadoAparato()+"' and apa.fechaAdquisicion='"+cot.getFechaVenta()+"' ").getResultList();
		}
		else
		{
			lis=entityManager.createQuery("SELECT apa.numSerie FROM AparatoCliente apa where apa.cliente.id="+cot.getCliente().getId()+" and apa.ladoAparato='"+cot.getLadoAparato()+"' ").getResultList();
		}
		/*for(String obj:lis)
		{
			//System.out.println("imprimiendo "+obj[1]);
			if(obj[1].equals(co.getId()))
			{
				System.out.println("NumSerie "+obj);
				//System.out.println("IDCOT "+obj[1]);
				//System.out.println("NumSerie "+obj[2]);
			//}
			
		}*/
		/*List<String> lis=entityManager.createQuery("SELECT apa.numSerie FROM ItemComboApa item,AparatoCliente apa where item.producto.id=apa.idPrd and item.id="+item.getId()+"").getResultList();
		for(String obj:lis)
		{
			System.out.println("NumSerie "+obj[0]);
			if(obj[1].equals(co.getId()))
			{
				System.out.println("NumSerie "+obj);
				//System.out.println(" "+obj[1]);
				//System.out.println("NumSerie "+obj[2]);
			//}
			
		}*/
		
		//return (String)obj[0]; 
		if(!lis.isEmpty())
			return lis.get(0);
		else
			return "";
	}
	
	public String buscarNumeroSerie(CotizacionComboApa coti,int opcion)
	{
		String serie="";
		if(coti.getVenta()!=null)
		{
			
			if(opcion==1)
			{
				
				for(DetVentaProdServ det:coti.getVenta().getDetVenta())
				{
					if(det.getCodCoti()!=null && det.getNumSerie()!=null)
					{
						if(coti.getId().equals(det.getCodCoti()))
							serie=det.getNumSerie();
					}
					
				}
			}
			else
			{
				
				for(DetVentaProdServ det:coti.getVenta().getDetVenta())
				{
					if(det.getCodCoti()!=null && det.getNumSerie()!=null)
					{
						if(coti.getHijoBin().get(0).getId().equals(det.getCodCoti()))
							serie=det.getNumSerie();
					}
					
				}
				
			}
			
		}
		
		
		return serie;
	}
	
	public void excelVentasAparatos() throws IOException
	{
		
			
		
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader(
				"Content-Disposition",
				"attachment;filename=ventasAparatos-"
						+ sdf.format(cal.getTime()) + ".xls");
		
		
		HSSFWorkbook libro = new HSSFWorkbook();
		HSSFSheet hoja = libro.createSheet();
		CreationHelper ch = libro.getCreationHelper();

		HSSFRow fila;
		HSSFCell celda;

		// definicion de estilos para las celdas
		HSSFFont headfont = libro.createFont(), headfont2 = libro
				.createFont(),headfontW = libro.createFont(), headfont3 = libro.createFont();
		headfont.setFontName("Arial");
		headfont.setFontHeightInPoints((short) 8);
		headfont2.setFontName("Arial");
		headfont2.setFontHeightInPoints((short) 10);
		headfont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		headfontW.setFontName("Arial");
		headfontW.setFontHeightInPoints((short) 8);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 7);
		//headfont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		

					HSSFCellStyle stAling = libro.createCellStyle(), stDate = libro
							.createCellStyle(), stAlingRight = libro.createCellStyle(), stTitles = libro.createCellStyle(),stTitlesD = libro
							.createCellStyle(),stTitlesI = libro.createCellStyle(), stTotals = libro.createCellStyle(), stList = libro
							.createCellStyle(), stFinal = libro.createCellStyle(), stPorcent = libro
							.createCellStyle();

					// Para Formatos de dolar y porcentaje
					DataFormat estFormato = libro.createDataFormat();

					stAling.setFont(headfont);
					stAling.setWrapText(true);
					stAling.setAlignment(stAling.ALIGN_RIGHT);
					stAling.setDataFormat(estFormato.getFormat("$#,#0.00"));

					stDate.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yy"));
					stDate.setFont(headfont2);

					stTitles.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitles.setAlignment(stTitles.ALIGN_CENTER);
					stTitles.setFont(headfontW);
					stTitles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitles.setFillForegroundColor(HSSFColor.GREEN.index);
					
					stTitlesD.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitlesD.setAlignment(stTitles.ALIGN_CENTER);
					stTitlesD.setFont(headfontW);
					stTitlesD.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitlesD.setFillForegroundColor(HSSFColor.RED.index);
					
					stTitlesI.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitlesI.setAlignment(stTitles.ALIGN_CENTER);
					stTitlesI.setFont(headfontW);
					stTitlesI.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitlesI.setFillForegroundColor(HSSFColor.BLUE.index);

					stList.setAlignment(stList.ALIGN_CENTER);
					stList.setVerticalAlignment(stList.VERTICAL_TOP);
					stList.setWrapText(true);
					stList.setFont(headfont3);

					stFinal.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stFinal.setAlignment(stTitles.ALIGN_RIGHT);
					stFinal.setFont(headfont2);
					stFinal.setDataFormat(estFormato.getFormat("$#,#0.00"));

					// Estilo para porcentaje
					stPorcent.setFont(headfont);
					stPorcent.setWrapText(true);
					stPorcent.setAlignment(stAlingRight.ALIGN_RIGHT);
					stPorcent.setDataFormat(estFormato.getFormat("#0.#00%"));
					
					
					// agregando la lista de productos, srv, combos.
					fila = hoja.createRow(1);
					
					celda = fila.createCell(0);
					celda.setCellValue("Fecha Venta");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Cliente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Categoria D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(3);
					celda.setCellValue("Marca D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(4);
					celda.setCellValue("Modelo D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(5);
					celda.setCellValue("# Serie D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(6);
					celda.setCellValue("Bateria D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(7);
					celda.setCellValue("Precio D");
					celda.setCellStyle(stTitlesD);
					
					celda = fila.createCell(8);
					celda.setCellValue("Categoria I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(9);
					celda.setCellValue("Marca I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(10);
					celda.setCellValue("Modelo I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(11);
					celda.setCellValue("# Serie I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(12);
					celda.setCellValue("Bateria I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(13);
					celda.setCellValue("Precio I");
					celda.setCellStyle(stTitlesI);
					
					celda = fila.createCell(14);
					celda.setCellValue("Precio Venta");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(15);
					celda.setCellValue("Entrego");
					celda.setCellStyle(stTitles);
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					for(PojoVentasApa vtaApa: listaVentasApa)
					{
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						celda=fila.createCell(0);//Fecha Venta
						celda.setCellValue(vtaApa.getFechaVenta().toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						 
						celda=fila.createCell(1); //Cliente
						celda.setCellValue(vtaApa.getCliente().getNombreCompleto());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //CAtegoriaD
						if(vtaApa.getCategoriaD()!=null)
							celda.setCellValue(vtaApa.getCategoriaD().getNombre());
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //MarcaD
						if(vtaApa.getMarcaD()!=null)
							celda.setCellValue(vtaApa.getMarcaD().getNombre());
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						
						celda=fila.createCell(4); //ModeloD
						celda.setCellValue(vtaApa.getModeloD());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //SerieD
						celda.setCellValue(vtaApa.getNumSerieD());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						celda=fila.createCell(6); //BateriaD
						celda.setCellValue(vtaApa.getBateriaD());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //PrecioD
						celda.setCellValue(vtaApa.getPrecioD());
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //CAtegoriaI
						if(vtaApa.getCategoriaI()!=null)
							celda.setCellValue(vtaApa.getCategoriaI().getNombre());
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						
						celda=fila.createCell(9); //MarcaI
						if(vtaApa.getMarcaI()!=null)
							celda.setCellValue(vtaApa.getMarcaI().getNombre());
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //ModeloI
						celda.setCellValue(vtaApa.getModeloI());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						celda=fila.createCell(11); //SerieI
						celda.setCellValue(vtaApa.getNumSerieI());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(11);
						
						celda=fila.createCell(12); //BateriaI
						celda.setCellValue(vtaApa.getBateriaI());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(12);
						
						celda=fila.createCell(13); //PrecioI
						celda.setCellValue(vtaApa.getPrecioI());
						celda.setCellStyle(stList);
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(13);
						
						celda=fila.createCell(14); //Precio Total
						celda.setCellValue(vtaApa.getPrecioTotal());
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(14);
						
						celda=fila.createCell(15); //usuario
						celda.setCellValue(vtaApa.getUsEntrego().getNombreCompleto());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(15);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
		
		
	}
	
	
	public void excelCotCliente() throws IOException
	{
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader(
				"Content-Disposition",
				"attachment;filename=cotizacionesPorCliente-"
						+ sdf.format(cal.getTime()) + ".xls");
		
		
		HSSFWorkbook libro = new HSSFWorkbook();
		HSSFSheet hoja = libro.createSheet();
		CreationHelper ch = libro.getCreationHelper();

		HSSFRow fila;
		HSSFCell celda;

		// definicion de estilos para las celdas
		HSSFFont headfont = libro.createFont(), headfont2 = libro
				.createFont(), headfont3 = libro.createFont(),headfontW = libro.createFont();
		headfont.setFontName("Arial");
		headfont.setFontHeightInPoints((short) 8);
		
		headfont2.setFontName("Arial");
		headfont2.setFontHeightInPoints((short) 10);
		headfont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		headfontW.setFontName("Arial");
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 7);
		headfont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		

					HSSFCellStyle stAling = libro.createCellStyle(), stDate = libro
							.createCellStyle(), stAlingRight = libro.createCellStyle(), stTitles = libro
							.createCellStyle(), stTotals = libro.createCellStyle(), stList = libro
							.createCellStyle(), stFinal = libro.createCellStyle(), stPorcent = libro
							.createCellStyle();

					// Para Formatos de dolar y porcentaje
					DataFormat estFormato = libro.createDataFormat();

					stAling.setFont(headfont);
					stAling.setWrapText(true);
					stAling.setAlignment(stAling.ALIGN_RIGHT);
					stAling.setDataFormat(estFormato.getFormat("$#,#0.00"));

					stDate.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yy"));
					stDate.setFont(headfont2);

					stTitles.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitles.setAlignment(stTitles.ALIGN_CENTER);
					stTitles.setFont(headfontW);
					stTitles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);;
					stTitles.setFillForegroundColor(HSSFColor.GREEN.index);

					stList.setAlignment(stList.ALIGN_CENTER);
					stList.setVerticalAlignment(stList.VERTICAL_TOP);
					stList.setWrapText(true);
					stList.setFont(headfont3);

					stFinal.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stFinal.setAlignment(stTitles.ALIGN_RIGHT);
					stFinal.setFont(headfont3);
					stFinal.setDataFormat(estFormato.getFormat("$#,#0.00"));

					// Estilo para porcentaje
					stPorcent.setFont(headfont);
					stPorcent.setWrapText(true);
					stPorcent.setAlignment(stAlingRight.ALIGN_RIGHT);
					stPorcent.setDataFormat(estFormato.getFormat("#0.#00%"));
					
					
					// agregando la lista de productos, srv, combos.
					fila = hoja.createRow(1);
					celda = fila.createCell(0);
					celda.setCellValue("FECHA COTIZACION");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("FECHA DE VENTA");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("VENDIDO");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("NUM COT");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("CLIENTE");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("TIEMPO VENTA");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Lado");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("VENDEDOR");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("PRECIO COTIZADO");
					celda.setCellStyle(stTitles);

					celda = fila.createCell(9);
					celda.setCellValue("PRECIO VENTA");
					celda.setCellStyle(stTitles);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2,contCelda=0;
					
					for(Object[] item: cotizaciones)
					{
						fila = hoja.createRow(contFila);
						
						fila.createCell(0).setCellValue(item[1].toString());//Fecha Cotizacion
						 hoja.autoSizeColumn(0);
						 
						fila.createCell(1).setCellValue(obtenerFechaVenta((CotizacionComboApa)item[3])); //Fecha Venta
						hoja.autoSizeColumn(1);
						
						if(obtenerVendido(item[2].toString())!=null && !obtenerVendido(item[2].toString()).equals(""))
							fila.createCell(2).setCellValue(obtenerVendido(item[2].toString())); //Vendido
						
						hoja.autoSizeColumn(2);
						
						fila.createCell(3).setCellValue("COT"+item[0].toString()); //IdCotizacion
						hoja.autoSizeColumn(3);
						
						fila.createCell(4).setCellValue(item[4].toString()); //Cliente
						hoja.autoSizeColumn(4);
						
						fila.createCell(5).setCellValue(calcularTiempoVenta((CotizacionComboApa)item[3])); //Tiempo venta
						hoja.autoSizeColumn(5);
						
						fila.createCell(6).setCellValue(obtenerLado((CotizacionComboApa)item[3])); //Lado
						hoja.autoSizeColumn(6);
						
						fila.createCell(7).setCellValue(item[5].toString()); //Vendedor
						hoja.autoSizeColumn(7);
						
						fila.createCell(8).setCellValue(calcularPrecioCotizado((CotizacionComboApa)item[3])); //Precio Venta
						hoja.autoSizeColumn(8);
						
						fila.createCell(9).setCellValue(calcularPrecioVenta((CotizacionComboApa)item[3])); //Precio Venta
						hoja.autoSizeColumn(9);
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	public String obtenerVendido(String estado)
	{
		
		if(estado.equals("APL") || estado.equals("COT"))
			return "X";
		else
			return "";
		
	}


	public List<CotizacionComboApa> getCotizacionListDetalle() {
		return cotizacionListDetalle;
	}


	public void setCotizacionListDetalle(
			List<CotizacionComboApa> cotizacionListDetalle) {
		this.cotizacionListDetalle = cotizacionListDetalle;
	}


	public Sucursal getSucursalFlt() {
		return sucursalFlt;
	}


	public void setSucursalFlt(Sucursal sucursalFlt) {
		this.sucursalFlt = sucursalFlt;
	}


	public List<Object[]> getCotizaciones() {
		return cotizaciones;
	}


	public void setCotizaciones(List<Object[]> cotizaciones) {
		this.cotizaciones = cotizaciones;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getLadoFilter() {
		return ladoFilter;
	}


	public void setLadoFilter(String ladoFilter) {
		this.ladoFilter = ladoFilter;
	}


	public Categoria getCategoriaSelected() {
		return categoriaSelected;
	}


	public void setCategoriaSelected(Categoria categoriaSelected) {
		this.categoriaSelected = categoriaSelected;
		nombreCat=categoriaSelected.getNombre();
	}


	public String getNombreCat() {
		return nombreCat;
	}


	public void setNombreCat(String nombreCat) {
		this.nombreCat = nombreCat;
	}


	public List<PojoVentasApa> getListaVentasApa() {
		return listaVentasApa;
	}


	public void setListaVentasApa(List<PojoVentasApa> listaVentasApa) {
		this.listaVentasApa = listaVentasApa;
	}


	public String getLadoApa() {
		return ladoApa;
	}


	public void setLadoApa(String ladoApa) {
		this.ladoApa = ladoApa;
	}


	public int getSumaIzq() {
		return sumaIzq;
	}


	public void setSumaIzq(int sumaIzq) {
		this.sumaIzq = sumaIzq;
	}


	public int getSumaDer() {
		return sumaDer;
	}


	public void setSumaDer(int sumaDer) {
		this.sumaDer = sumaDer;
	}


	
	

	
	
	
	
	

}
