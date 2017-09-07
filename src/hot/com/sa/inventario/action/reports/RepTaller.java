package com.sa.inventario.action.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.sa.kubekit.action.workshop.ReparacionClienteHome;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.sales.PojoVentasApa;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.EtapaRepCliente;
import com.sa.model.workshop.EtapaReparacion;
import com.sa.model.workshop.ItemRequisicionEta;
import com.sa.model.workshop.ProcesoTaller;
import com.sa.model.workshop.ReparacionCliente;
import com.sa.model.workshop.ServicioReparacion;

@Name("repTaller")
@Scope(ScopeType.CONVERSATION)
public class RepTaller extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	private String hqlCond;
	private String hqlOrder;
	private final String repCtx = "/taller";
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	@In(required = true,create=true)
	protected ReparacionClienteHome reparacionClienteHome;
	
	private List<EtapaRepCliente> resultList = new ArrayList<EtapaRepCliente>();
	private List<ReparacionCliente> reparaciones = new ArrayList<ReparacionCliente>();
	private List<ItemRequisicionEta> itemsRequisicion = new ArrayList<ItemRequisicionEta>();
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	private ProcesoTaller procesoTll;
	private Sucursal sucursal;
	private EtapaReparacion etapaRep;
	
	private String intervaloTiempo;
	
	
	private List<Object[]> trabajosTaller= new ArrayList<Object[]>();
	

	public void reparacionesMecanico() {
		trabajosTaller("RPR");
	}
	
	public void ensamblajesMecanico() {
		trabajosTaller("ENS");
	}
	
	public void resetClass() {
		hql = "";
		hqlCond = "";
		hqlOrder = "";
		resultList = new ArrayList<EtapaRepCliente>();
		reparaciones = new ArrayList<ReparacionCliente>();
		itemsRequisicion = new ArrayList<ItemRequisicionEta>();
		dtRp = new HashMap<String, Object>();
		procesoTll = null;
		sucursal = null;
		etapaRep = null;
		
		intervaloTiempo = "";
		resetMainClass();
	}
	
	
	
	public void cargarTrabajosTaller()
	{
		
							//Falta quien hiso la etapa de taller principal, si es reparacion o si es elaboracion de molde etc. Y falta quien hiso control de calida, esto puede sscarse desde una funcion
		String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,CondAparatoRep cond where r.id=cond.repCliente.id";
		
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		
		if(fechaInicio != null && fechaFin == null) 
			jpql += " AND r.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			jpql += " AND r.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			jpql += " AND r.fechaIngreso BETWEEN :f1 AND :f2 ";
		else {
			
			jpql += "  AND r.fechaIngreso BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		if(sucursal!=null)
		{
			jpql += " AND r.sucursal.id="+sucursal.getId()+"";
		}
		
		if(procesoTll!=null)
		{
			jpql += " AND r.proceso.id="+procesoTll.getId()+"";
		}
		
		trabajosTaller=entityManager.createQuery(jpql).setParameter("f1",fechaInicio).setParameter("f2", fechaFin).getResultList();
		
		
	}
	
	public void calcularDias()
	{
		
	}
	public long calcularDiasTranscurridos(ReparacionCliente rep)
	{
		final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
		long dias=0;
		Date fechaActual=new Date();
		
		if(rep.getEstado().equals("DLV"))
		{
			
			dias=(rep.getFechaEntrega().getTime()-rep.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		else
		{
			dias=(fechaActual.getTime()-rep.getFechaIngreso().getTime())/MILLSECS_PER_DAY;
		}
		
		
		
		return dias;
		
	}
	
	public String buscarUsuarioEtapa(ReparacionCliente rep)
	{
		
		String usuario="";
		
		for(EtapaRepCliente etap: rep.getEtapasReparacion())
		{
			if(etap.getEstado()!=null)
			{
				if(etap.getEstado().equals("APR") && etap.getUsuario()!=null && etap.getEtapaRep().getId()==42 || etap.getEtapaRep().getId()==49 || etap.getEtapaRep().getId()==55 || etap.getEtapaRep().getId()==58)
				{
					usuario=etap.getUsuario().getNombreUsuario();
				}
			}
		}
		
		return usuario;
		
	}
	
	public String buscarControlCalidad(ReparacionCliente rep)
	{
		String usuario="";
		
		for(EtapaRepCliente etap: rep.getEtapasReparacion())
		{
			if(etap.getEstado()!=null)
			{
				if(etap.getEstado().equals("APR") && etap.getUsuario()!=null && etap.getEtapaRep().getId().equals(44) || etap.getEtapaRep().getId().equals(57))
				{
					usuario=etap.getUsuario().getNombreUsuario();
				}
			}
		}
		
		return usuario;
		
	}
	
	public String obtenerTextoEstado(String estadoAct)
	{
		
		if(estadoAct.equals("DLV"))
			return "Entregado";
		else
			return "En proceso";
		
	}
	
	public String etapaActual(ReparacionCliente rep)
	{
		String etapa="";
		for(EtapaRepCliente etap: rep.getEtapasReparacion())
		{
			if(etap.getEstado()!=null)
			{
				if(etap.getEstado().equals("PEN"))
					etapa=etap.getEtapaRep().getNombre();
			}
		}
		
		return etapa;
	}
	
	public String tieneReparacion(ReparacionCliente rep)
	{
		
		String tiene="si";
		
		for(EtapaRepCliente etapa: rep.getEtapasReparacion())
		{
			if(etapa.getEstado()!=null)
			{
				if(etapa.getEstado().equals("NAP"))
				{
					tiene="no";
					break;
				}
				
			}
		}
		
		
		return tiene;
	}
	
	
	public void excelTrabajosTaller() throws IOException
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
				"attachment;filename=TrabajosTaller-"
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
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 8);
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
					stDate.setFont(headfont3);

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
					//stList.setVerticalAlignment(stList.VERTICAL_TOP);
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
					celda.setCellValue("Fecha Entrega");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Fecha Ingreso");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Estado");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Dias Transcurridos");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Tiene Reparacion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Condicion Inicial");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Cliente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("Marca");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Modelo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Serie");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Tipo de Trabajo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(11);
					celda.setCellValue("Tecnico");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(12);
					celda.setCellValue("Control Calidad");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(13);
					celda.setCellValue("Monto");
					celda.setCellStyle(stTitles);
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					
					int contFila=2;//,contCelda=0;
					
					for(Object[] rep: trabajosTaller)
					{
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						celda=fila.createCell(0);//Fecha Entrega
						
						if(rep[1]!=null)
							celda.setCellValue((Date)rep[1]);
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(0);
						 
						celda=fila.createCell(1); //Fecha Ingreso
						celda.setCellValue((Date)rep[2]);
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Estado
						celda.setCellValue(obtenerTextoEstado(rep[0].toString()));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //Dias 
						celda.setCellValue(calcularDiasTranscurridos((ReparacionCliente)rep[10]));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						
						celda=fila.createCell(4); //Tiene Reparacion
						celda.setCellValue(tieneReparacion((ReparacionCliente)rep[10]));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //Condicion Inicial	
						celda.setCellValue(rep[3].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						celda=fila.createCell(6); //Cliente
						celda.setCellValue(rep[4].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //Marca
						celda.setCellValue(rep[5].toString());
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //Modelo
						celda.setCellValue(rep[6].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						
						celda=fila.createCell(9); //Num Serie
						
						if(rep[7]!=null)
							celda.setCellValue(rep[7].toString());
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //Tipo trabajo
						celda.setCellValue(rep[11].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						celda=fila.createCell(11); //Tecnico
						
						celda.setCellValue(buscarUsuarioEtapa((ReparacionCliente)rep[10]));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(11);
						
						celda=fila.createCell(12); //Control Calidad
						celda.setCellValue(buscarControlCalidad((ReparacionCliente)rep[10]));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(12);
						
						celda=fila.createCell(13); //Monto
						if(rep[9]!=null)
							celda.setCellValue((Float)rep[9]);
						else
							celda.setCellValue("");
						
						celda.setCellStyle(stList);
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(13);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void trabajosTaller(String procTaller){
		Date date1 = truncDate(fechaInicio, false);			
		Date date2 = truncDate(fechaFin, true);
		
		if(intervaloTiempo != null && !intervaloTiempo.isEmpty()) {
			Calendar fechaActual = new GregorianCalendar();
			Calendar fechaIni = new GregorianCalendar(), fechaFin = new GregorianCalendar();
			fechaIni.setFirstDayOfWeek(Calendar.MONDAY);
			fechaFin.setFirstDayOfWeek(Calendar.MONDAY);
			fechaActual.setFirstDayOfWeek(Calendar.MONDAY);
			if(intervaloTiempo.equals("ANL")) {
				fechaIni.set(Calendar.MONTH, 0);
				fechaFin.set(Calendar.MONTH, 11);
			}
			
			if(intervaloTiempo.equals("TRI")) {
				int mesIni = 0;
				if(fechaActual.get(Calendar.MONTH) <= 11) mesIni=9;
				else if(fechaActual.get(Calendar.MONTH) <= 8) mesIni = 6;
				else if(fechaActual.get(Calendar.MONTH) <= 5) mesIni = 3;
				else if(fechaActual.get(Calendar.MONTH) <= 2) mesIni = 0;
				fechaIni.set(Calendar.MONTH, mesIni);
				fechaFin.set(Calendar.MONTH, mesIni + 2);
			} 

			if(intervaloTiempo.equals("MNS") || intervaloTiempo.equals("TRI") || intervaloTiempo.equals("ANL")) { 
				fechaIni.set(Calendar.DAY_OF_MONTH, 1);
				fechaFin.set(Calendar.DAY_OF_MONTH, 1);
				fechaFin.add(Calendar.MONTH, 1);
				fechaFin.add(Calendar.DATE, -1);
			} 
			
			if(intervaloTiempo.equals("SEM")) {
				fechaIni.add(Calendar.DATE, -fechaActual.get(Calendar.DAY_OF_WEEK)+1);
				fechaFin.add(Calendar.DATE, (8 - fechaActual.get(Calendar.DAY_OF_WEEK)));
			} 
			
			if(intervaloTiempo.equals("DRO")) {
				fechaIni.setTime(truncDate(fechaActual.getTime(), false));
				fechaFin.setTime(truncDate(fechaActual.getTime(), true));
			}
			
			
			date1 = truncDate(fechaIni.getTime(), false);
			date2 = truncDate(fechaFin.getTime(), true);
		}
		
		
		hql = "SELECT r FROM EtapaRepCliente r ";
		hqlCond = " WHERE r.reparacionCli.estado = 'FIN' " +
				"	AND r.etapaRep.codEta = 'REP' " +
				"	AND r.etapaRep.procesoTll.prcCode = '"+procTaller+"' ";
		hqlOrder = " ORDER by r.usuario.nombreUsuario";
		
		List<Map> parametros = new ArrayList<Map>();
		parametros.add(new HashMap<String, Object>(){{put("nomParam","fechaIni");put("valor",fechaInicio);
							put("condicion"," AND r.reparacionCli.fechaFin >= :fechaIni ");}});
		parametros.add(new HashMap<String, Object>(){{put("nomParam","fechaFin");put("valor",fechaFin);
							put("condicion"," AND r.reparacionCli.fechaFin <= :fechaFin ");}});
		resultList = getFilteredList(entityManager, hql, hqlCond, hqlOrder, parametros);
		
	}
	
	public void historialApaCli() {
		
		List<HashMap<String, Object>> listaAparatos = new ArrayList<HashMap<String, Object>>();
		hql = "SELECT x FROM AparatoCliente x WHERE x.cliente = :cli ";
		
		listaAparatos = entityManager.createQuery(hql)
				.setParameter("cli", getFltObj1())
				.getResultList(); 
	
		
		dtRp.put("lst", listaAparatos);
		dtRp.put("cli", getFltObj1());
	}
	
	public void repTrabajosPorMecanico() {
		reparaciones = new ArrayList<ReparacionCliente>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT e FROM ReparacionCliente e WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND e.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND e.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND e.fechaIngreso BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND e.fechaIngreso BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		/*if(getEtapaRep() == null )
			hql+=" AND (:fse IS NULL OR :fse = '') ";
		else
			hql+=" AND e.etapaRep = :fse  ";
		*/
		if(getProcesoTll() == null )
			hql+=" AND (:prc IS NULL OR :prc = '') ";
		else 
			hql+=" AND e.proceso = :prc ";
		
		if(sucursal != null) 
			hql += " AND e.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		if(getValCmb1() != null && !getValCmb1().equals("") &&
				(getValCmb1().equals("APR") || getValCmb1().equals("LST") || getValCmb1().equals("FIN"))) {
			if(getValCmb1().equals("APR"))
				hql += " AND e.aprobada = true ";
			else if(getValCmb1().equals("LST"))
				hql += " AND e.estado = 'FIN' ";
			else if(getValCmb1().equals("FIN"))
				hql += " AND e.estado = 'DLV' ";
		}
		
		List<ReparacionCliente> tmpRep = new ArrayList<ReparacionCliente>();
		tmpRep = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				//.setParameter("fse", getEtapaRep())
				.setParameter("prc", getProcesoTll())
				.setParameter("suc", sucursal)
				.getResultList();
		
		//Si hay filtro de etapa descartamos las de las etapas no seleccionadas
		for(ReparacionCliente repCli : tmpRep) {
			for(EtapaRepCliente tmpEta : repCli.getEtapasReparacion()) {
				if(tmpEta.getEstado() != null && tmpEta.getEstado().equals("PEN"))
					repCli.setCurrEtapa(tmpEta);
			} 
			if(getValCmb1() == null || getValCmb1().equals("")) 
				reparaciones.add(repCli);
			else if(getValCmb1() != null && !getValCmb1().equals("") && (
					(getValCmb1().equals("APR") || getValCmb1().equals("LST") || getValCmb1().equals("FIN"))
					|| (repCli.getCurrEtapa() != null && repCli.getCurrEtapa().getEtapaRep() != null && repCli.getCurrEtapa().getEtapaRep().getCodEta().equals(getValCmb1())) )
					) 
				reparaciones.add(repCli);
		}
		
		//Calculamos total en reparaciones
		setTotDec1(0f);
		for(ReparacionCliente tmpRp: reparaciones) 
		if(tmpRp.getCosto() != null)
			setTotDec1(getTotDec1()+tmpRp.getCosto());
	}
	
	
	
	public void repTrabajosTaller() {
		reparaciones = new ArrayList<ReparacionCliente>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT e FROM ReparacionCliente e WHERE 1 = 1  ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND e.fechaIngreso >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND e.fechaIngreso <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND e.fechaIngreso BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(getProcesoTll() == null )
			hql+=" AND (:prc IS NULL OR :prc = '') ";
		else 
			hql+=" AND e.proceso = :prc ";
		
		if(sucursal != null) 
			hql += " AND e.sucursal = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		if(getValCmb1() != null && !getValCmb1().equals("")) {
			if(getValCmb1().equals("APR"))
				hql += " AND e.aprobada = false AND e.estado <> 'REC' AND e.estado <> 'FIN' AND e.estado <> 'DLV' ";
			else if(getValCmb1().equals("LST"))
				hql += " AND e.estado = 'FIN' ";
			else if(getValCmb1().equals("FIN"))
				hql += " AND e.estado = 'DLV' ";
			else if(getValCmb1().equals("REC"))
				hql += " AND e.estado = 'REC' and e.aprobada = false ";
			else if(getValCmb1().equals("REP"))
				hql += " AND e.aprobada=true AND e.estado <> 'FIN' ";
		}
		
		List<ReparacionCliente> tmpRep = new ArrayList<ReparacionCliente>();
		tmpRep = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				//.setParameter("fse", getEtapaRep())
				.setParameter("prc", getProcesoTll())
				.setParameter("suc", sucursal)
				.getResultList();
		
		//Si hay filtro de etapa descartamos las de las etapas no seleccionadas
		
		Calendar cal = new GregorianCalendar();
		for(ReparacionCliente repCli : tmpRep) {
			boolean agregar = false;
			for(EtapaRepCliente tmpEta : repCli.getEtapasReparacion()) {
				if(tmpEta.getEstado() != null && tmpEta.getEstado().equals("PEN")) {
					repCli.setCurrEtapa(tmpEta);
					//Calcular cuantos dias lleva en la fase actual
					if(repCli.getCurrEtapa().getFechaInicio() != null) {
						Long resta = cal.getTime().getTime() - repCli.getCurrEtapa().getFechaInicio().getTime(); 
						resta = resta / 1000 / 60 / 60 / 24;
						repCli.getCurrEtapa().setNumDias(resta.intValue());
					}
						
				}
			} 
			if(getValCmb1() == null || getValCmb1().equals("")) 
				agregar = true;
			else if(getValCmb1() != null && !getValCmb1().equals("") && (
					(getValCmb1().equals("REC") || getValCmb1().equals("APR") || getValCmb1().equals("LST") || getValCmb1().equals("FIN"))
					|| (repCli.getCurrEtapa() != null && repCli.getCurrEtapa().getEtapaRep() != null && repCli.getCurrEtapa().getEtapaRep().getCodEta().equals(getValCmb1())) )
					) 
				agregar = true;
			
			//Verificamos si tiene una garantia activa el aparato
			boolean garVtaVigente = reparacionClienteHome.tieneGarantiaVigente(repCli.getAparatoRep().getFechaAdquisicion(), repCli.getAparatoRep().getPeriodoGarantia());
			boolean garRepVigente = reparacionClienteHome.tieneGarantiaVigente(repCli.getAparatoRep().getFechaGarRep(), repCli.getAparatoRep().getPeriodoGarantiaRep());
			if(garVtaVigente || garRepVigente)
				repCli.getAparatoRep().setGarantiaVigente(true);
						
			if(getValCmb2() != null && !getValCmb2().equals("")) {
				if(getValCmb2().equals("GAR") && !repCli.getAparatoRep().isGarantiaVigente())
					agregar = false;
				else if(getValCmb2().equals("NGR") && repCli.getAparatoRep().isGarantiaVigente())
					agregar = false;
			}
			
			
			if(agregar)
				reparaciones.add(repCli);
		}
		
		//Calculamos total en reparaciones
		setTotDec1(0f);
		for(ReparacionCliente tmpRp: reparaciones) 
		if(tmpRp.getCosto() != null)
			setTotDec1(getTotDec1()+tmpRp.getCosto());
	}
	
	public void repPrdsRequisiciones() {
		itemsRequisicion = new ArrayList<ItemRequisicionEta>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT i FROM ItemRequisicionEta i WHERE 1 = 1 AND i.reqEtapa.estado = 'APR' ";
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND i.reqEtapa.fechaAprobacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND i.reqEtapa.fechaAprobacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND i.reqEtapa.fechaAprobacion BETWEEN :f1 AND :f2 ";
		else {
			hql += "  AND i.reqEtapa.fechaAprobacion BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		if(getProcesoTll() == null )
			hql+=" AND (:prc IS NULL OR :prc = '') ";
		else 
			hql+=" AND i.reqEtapa.etapaRepCli.reparacionCli.proceso = :prc ";
		
		if(sucursal != null) 
			hql += " AND i.reqEtapa.sucursalSol = :suc ";
		else
			hql += " AND (:suc IS NULL OR :suc = '') ";
		
		
		itemsRequisicion = entityManager.createQuery(hql)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				//.setParameter("fse", getEtapaRep())
				.setParameter("prc", getProcesoTll())
				.setParameter("suc", sucursal)
				.getResultList();
				
		//Calculamos total en reparaciones
		setTotDec1(0f);
		for(ItemRequisicionEta tmpIr: itemsRequisicion) 
		if(tmpIr.getProducto().getPrcNormal() != null)
			setTotDec1(getTotDec1()+tmpIr.getProducto().getPrcNormal()*tmpIr.getCantidad());
	}
	
	public void repPromedioEtapas() {
		List<HashMap<String, Object>> tiemposPromedios = new ArrayList<HashMap<String, Object>>();
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		hql = "SELECT DISTINCT x FROM EtapaReparacion x ORDER BY x.procesoTll.id ASC, x.orden ASC";
		
		List<EtapaReparacion> etapas = entityManager.createQuery(hql).getResultList();
		
		hql = " SELECT x FROM EtapaRepCliente x WHERE 1 = 1 " +
				"	AND x.fechaRealFin IS NOT NULL AND x.fechaInicio IS NOT NULL" +
				"	AND x.etapaRep = :etr ";
		
		if(fechaInicio != null && fechaFin == null) 
			hql += " AND x.reparacionCli.fechaIngreso >= :f1 AND (:f2 IS NULL OR 1 = 1) ";
		else if(fechaInicio == null && fechaFin != null)
			hql += " AND x.reparacionCli.fechaIngreso <= :f2 AND (:f1 IS NULL OR 1 = 1) ";
		else if(fechaInicio != null && fechaFin != null)
			hql += " AND x.reparacionCli.fechaIngreso BETWEEN :f1 AND :f2 ";
		else 
			hql += "  AND (:f1 = :f2 OR 1 = 1) ";
		
		//Por cada etapa, sacamos solo los terminados comprendidos en los filtros
		for(EtapaReparacion tmpEta : etapas) {
			List<EtapaRepCliente> etapasCli = entityManager.createQuery(hql)
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("etr", tmpEta)
					.getResultList();
			
			Integer numReps = 0; 
			Long totalMilis = 0l;
			//Haremos la resta en milisegundos y despues la convertiremos a dias para irlo sumando a la sumatoria de dias
			for(EtapaRepCliente tmpEtCli : etapasCli) {
				try {
					totalMilis += (tmpEtCli.getFechaRealFin().getTime() - tmpEtCli.getFechaInicio().getTime()) ;
					numReps++;
				} catch(Exception ex) { }
			}
			//Calculamos el promedio
			HashMap<String, Object> detEtapa = new HashMap<String, Object>();
			if(totalMilis > 0)
				detEtapa.put("prmDias", (totalMilis / numReps) / 1000 / 60 / 60 / 24 );
			else
				detEtapa.put("prmDias", 0);
			detEtapa.put("eta", tmpEta);
			detEtapa.put("numReps", numReps);
			tiemposPromedios.add(detEtapa);
		}
		
		dtRp.put("lst", tiemposPromedios);
	}
	
	
	
	//nuevo el 28/07/2017
	public void reporteReparacionesTaller(String tipoEtapa)
	{
		//idEtapa = reparacion, molde o ensamble
		
		StringBuilder jpql = new StringBuilder();
		//List<Object[]> contenidoDiagnostico;
		//List<Object[]> contenidoReparacion;
		
		if(tipoEtapa.equals("R"))
			jpql.append("SELECT x FROM EtapaRepCliente x where x.etapaRep.id=42");
		else if(tipoEtapa.equals("M"))
			jpql.append("SELECT x FROM EtapaRepCliente x where x.etapaRep.id=49");
		else 
			jpql.append("SELECT x FROM EtapaRepCliente x where x.etapaRep.id=55");
		
		
		if(fechaInicio != null && fechaFin == null) 
			jpql.append(" AND x.reparacionCli.fechaIngreso >= :f1 AND (:f2 IS NULL OR 1 = 1) ");
		else if(fechaInicio == null && fechaFin != null)
			jpql.append(" AND x.reparacionCli.fechaIngreso <= :f2 AND (:f1 IS NULL OR 1 = 1) ");
		else if(fechaInicio != null && fechaFin != null)
			jpql.append(" AND x.reparacionCli.fechaIngreso BETWEEN :f1 AND :f2 ");
		else 
			jpql.append("  AND (:f1 = :f2 OR 1 = 1) ");
		
		
		resultList = entityManager.createQuery(jpql.toString()).setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();
		
	}
	
	
	public String obtenerContenidoReparacionEtapa(EtapaRepCliente etapa)
	{
		StringBuilder contenido = new StringBuilder();
		
		List<ServicioReparacion> servicios = entityManager.createQuery("FROM ServicioReparacion s where s.reparacion.id=:idReparacion ").setParameter("idReparacion", etapa.getReparacionCli().getId()).getResultList();
		
		
		float ingresoTaller=0f;
		
		if(servicios!=null && servicios.size()>0)
		{
			int i=1;
			for(ServicioReparacion sr:servicios)
			{
				if(i>1)
					contenido.append(" + ");
				
				contenido.append(sr.getServicio().getName());
				ingresoTaller += sr.getServicio().getCosto();
				
				i++;
			}
		}
		
		List<ItemRequisicionEta> itemsRequi = entityManager.createQuery("SELECT i FROM ItemRequisicionEta i WHERE i.reqEtapa.etapaRepCli.reparacionCli.id = :rep").setParameter("rep", etapa.getReparacionCli().getId()).getResultList();
		
		if(itemsRequi!=null && itemsRequi.size()>0)
		{
			for(ItemRequisicionEta item:itemsRequi)
			{
				contenido.append(" + ");
				contenido.append(item.getProducto().getNombre());
				ingresoTaller += (item.getProducto().getPrcNormal()*item.getCantidad()); 
			}
		}
		
		if(etapa.getReparacionCli().getIngresosTaller()==null || etapa.getReparacionCli().getIngresosTaller()<=0)
			etapa.getReparacionCli().setIngresosTaller(ingresoTaller);
		
		
		
		return contenido.toString();
	}
	
	public String buscarUsuarioRecibe(Integer idUsuario)
	{
		
		String nombre = (String) entityManager.createQuery("SELECT u.nombreCompleto FROM Usuario u where u.id=:idUsuario").setParameter("idUsuario",idUsuario).getResultList().get(0);
				
		
		return nombre;
	}
	
	public void exportarReparacionesExcel() throws IOException
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
				"attachment;filename=TrabajosTaller-"
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
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 8);
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
					stDate.setFont(headfont3);

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
					//stList.setVerticalAlignment(stList.VERTICAL_TOP);
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
					celda.setCellValue("Id");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Usuario recibe");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Fecha ingreso");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Cliente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Aparato");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Modelo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("# Serie");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("Sucursal");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Tecnico");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Servicios");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Valor reparacion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(11);
					celda.setCellValue("Garantia");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(12);
					celda.setCellValue("Fecha entrega");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(13);
					celda.setCellValue("Estado Reparacion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(14);
					celda.setCellValue("Estado Trabajo Taller");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					
					int contFila=2;//,contCelda=0;
					
					for(EtapaRepCliente rep: resultList)
					{
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						
						celda=fila.createCell(0); //id
						celda.setCellValue(rep.getReparacionCli().getId());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //recibe
						celda.setCellValue(rep.getReparacionCli().getUsuarioRecibe()!=null?rep.getReparacionCli().getUsuarioRecibe().getNombreCompleto():null);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Fecha Ingreso'
						if(obtenerFechaIngresoTaller(rep.getReparacionCli())!=null)
							celda.setCellValue(obtenerFechaIngresoTaller(rep.getReparacionCli()));
						else
							celda.setCellValue("");
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //cliente
						celda.setCellValue(rep.getReparacionCli().getCliente().getNombreCompleto());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //aparatos 
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						
						celda=fila.createCell(5); //modelo
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getModelo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						celda=fila.createCell(6); //serie
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getNumSerie());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //Sucursal	
						celda.setCellValue(rep.getReparacionCli().getSucursal().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //Tecnico
						if(rep.getFechaRealFin()!=null)
							celda.setCellValue(rep.getUsuario().getNombreCompleto());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						
						celda=fila.createCell(9); //Servicios
						celda.setCellValue(obtenerContenidoReparacionEtapa(rep));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //Valor reparacion
						celda.setCellValue(rep.getReparacionCli().getIngresosTaller()!=null?moneyDecimal(rep.getReparacionCli().getIngresosTaller()):0f);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						celda=fila.createCell(11); //
						celda.setCellValue(rep.getReparacionCli().getDescuentos_garantia()!=null?moneyDecimal(rep.getReparacionCli().getDescuentos_garantia()):0f);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(11);
						
						celda=fila.createCell(12); //entrega
						
						if(rep.getReparacionCli().getFechaEntrega()!=null)
							celda.setCellValue(rep.getReparacionCli().getFechaEntrega());
						else
							celda.setCellValue("");
						//celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(12);
						
						celda=fila.createCell(13); //Estado reparacion
						if(rep.getEstado()!=null && rep.getEstado().equals("APR"))
							celda.setCellValue("Finalizado");
						else
							celda.setCellValue("Pendiente");
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(13);
						
						celda=fila.createCell(14); //Estado trabajo taller
						celda.setCellValue(obtenerEstadoRep(rep.getReparacionCli()));
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(14);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void exportarRepEnsamblesExcel() throws IOException
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
				"attachment;filename=TrabajosTaller-"
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
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 8);
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
					stDate.setFont(headfont3);

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
					//stList.setVerticalAlignment(stList.VERTICAL_TOP);
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
					celda.setCellValue("Id");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Usuario recibe");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Fecha ingreso");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Cliente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Aparato");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Marca");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Modelo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("# Serie");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Lado");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Sucursal");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Tecnico");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(11);
					celda.setCellValue("Total trabajo taller");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(12);
					celda.setCellValue("Fecha ensamble");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(13);
					celda.setCellValue("Fecha entrega");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(14);
					celda.setCellValue("Estado Ensamble");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(15);
					celda.setCellValue("Estado Trabajo Taller");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					
					int contFila=2;//,contCelda=0;
					
					for(EtapaRepCliente rep: resultList)
					{
						fila = hoja.createRow(contFila);
						//System.out.println("Fila "+contFila);
						
						
						celda=fila.createCell(0); //id
						celda.setCellValue(rep.getReparacionCli().getId());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //recibe
						celda.setCellValue(rep.getReparacionCli().getUsuarioRecibe()!=null?rep.getReparacionCli().getUsuarioRecibe().getNombreCompleto():null);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Fecha Ingreso
						celda.setCellValue(rep.getReparacionCli().getFechaIngreso());
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //paciente
						celda.setCellValue(rep.getReparacionCli().getCliente().getNombreCompleto());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //aparatos 
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //marca
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getMarca());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						
						celda=fila.createCell(6); //modelo
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getModelo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //serie
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getNumSerie());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //lado
						celda.setCellValue(rep.getReparacionCli().getAparatoRep().getLadoAparato());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						celda=fila.createCell(9); //Sucursal	
						celda.setCellValue(rep.getReparacionCli().getSucursal().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //Tecnico
						if(rep.getFechaRealFin()!=null)
							celda.setCellValue(rep.getUsuario().getNombreCompleto());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						
						celda=fila.createCell(11); //Valor ensamble
						celda.setCellValue(rep.getReparacionCli().getIngresosTaller()!=null?moneyDecimal(rep.getReparacionCli().getIngresosTaller()):0f);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(11);
						
						celda=fila.createCell(12); //Fecha ensamble
						if(rep.getFechaRealFin()!=null)
							celda.setCellValue((Date)rep.getFechaRealFin());
						else 
							celda.setCellValue("");
						
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(12);
						
						
						celda=fila.createCell(13); //entrega
						
						if(rep.getReparacionCli().getFechaEntrega()!=null)
							celda.setCellValue((Date)rep.getReparacionCli().getFechaEntrega());
						else
							celda.setCellValue("");
						celda.setCellStyle(stDate);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(13);
						
						celda=fila.createCell(14); //estado emsable
						if(rep.getEstado()!=null && rep.getEstado().equals("APR"))
							celda.setCellValue("Finalizado");
						else
							celda.setCellValue("Pendiente");
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(14);
						
						celda=fila.createCell(15); //estado trabajo taller
						celda.setCellValue(obtenerEstadoRep(rep.getReparacionCli()));
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(15);
						
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void exportarRepMoldesExcel() throws IOException
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
				"attachment;filename=TrabajosTaller-"
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
		headfontW.setFontHeightInPoints((short) 10);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 8);
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
					stDate.setFont(headfont3);

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
					//stList.setVerticalAlignment(stList.VERTICAL_TOP);
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
					celda.setCellValue("Id");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Usuario recibe");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Fecha ingreso");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Cliente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Aparato");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Marca");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Modelo");
					celda.setCellStyle(stTitles);
					
					
					celda = fila.createCell(7);
					celda.setCellValue("Lado");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Sucursal");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Tecnico");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Precio");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(11);
					celda.setCellValue("Tipo elaboracion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(12);
					celda.setCellValue("Fecha realizacion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(13);
					celda.setCellValue("Fecha entrega");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(14);
					celda.setCellValue("Estado Molde");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(15);
					celda.setCellValue("Estado Trabajo Taller");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					
					int contFila=2;//,contCelda=0;
					
					for(EtapaRepCliente rep: resultList)
					{
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						
						celda=fila.createCell(0); //id
						celda.setCellValue(rep.getReparacionCli().getId());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //recibe
						celda.setCellValue(rep.getReparacionCli().getUsuarioRecibe()!=null?rep.getReparacionCli().getUsuarioRecibe().getNombreCompleto():null);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Fecha Ingreso'
						celda.setCellValue(rep.getReparacionCli().getFechaIngreso());
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //paciente
						celda.setCellValue(rep.getReparacionCli().getCliente().getNombreCompleto());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //aparatos 
						if(rep.getReparacionCli().getAparatoRep()!=null)
							celda.setCellValue(rep.getReparacionCli().getAparatoRep().getNombre());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //marca
						if(rep.getReparacionCli().getAparatoRep()!=null)
							celda.setCellValue(rep.getReparacionCli().getAparatoRep().getMarca());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						
						celda=fila.createCell(6); //modelo
						if(rep.getReparacionCli().getAparatoRep()!=null)
							celda.setCellValue(rep.getReparacionCli().getAparatoRep().getModelo());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						
						celda=fila.createCell(7); //lado
						if(rep.getReparacionCli().getAparatoRep()!=null)
							celda.setCellValue(rep.getReparacionCli().getAparatoRep().getLadoAparato());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //Sucursal	
						celda.setCellValue(rep.getReparacionCli().getSucursal().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						celda=fila.createCell(9); //Tecnico
						if(rep.getFechaRealFin()!=null)
							celda.setCellValue(rep.getUsuario().getNombreCompleto());
						else
							celda.setCellValue("");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						
						celda=fila.createCell(10); //Valor molde
						celda.setCellValue(rep.getReparacionCli().getIngresosTaller()!=null?rep.getReparacionCli().getIngresosTaller():0f);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						celda=fila.createCell(11); //Tipo elaboracion
						if(rep.getReparacionCli().getMoldeAparatoNuevo()!=null && rep.getReparacionCli().getMoldeAparatoNuevo())
							celda.setCellValue("Molde de aparato nuevo");
						else 
							celda.setCellValue("Molde individual");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(11);
						
						celda=fila.createCell(12); //Fecha molde
						if(rep.getFechaRealFin()!=null)
							celda.setCellValue((Date)rep.getFechaRealFin());
						else 
							celda.setCellValue("");
						
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(12);
						
						
						celda=fila.createCell(13); //entrega
						
						if(rep.getReparacionCli().getFechaEntrega()!=null)
							celda.setCellValue((Date)rep.getReparacionCli().getFechaEntrega());
						else
							celda.setCellValue("");
						//celda.setCellStyle(stList);
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(13);
					
						
						celda=fila.createCell(14); //estado molde 
						if(rep.getEstado()!=null && rep.getEstado().equals("APR"))
							celda.setCellValue("Finalizado");
						else
							celda.setCellValue("Pendiente");
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(14);
						
						
						celda=fila.createCell(15); //estado trabajo taller
						celda.setCellValue(obtenerEstadoRep(rep.getReparacionCli()));
						celda.setCellStyle(stList);
						//celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(15);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	public Date obtenerFechaIngresoTaller(ReparacionCliente rep)
	{
	
		System.out.println("Entro al metodo obtenerFechaIngreso ******");
		for(EtapaRepCliente etapa:rep.getEtapasReparacion())
		{
			//System.out.println("Entro al for ******");
			if(etapa.getEtapaRep().getId()==101 && etapa.getFechaRealFin()!=null)
			{
				System.out.println("Entro al if del metodo "+etapa.getEtapaRep().getNombre());
				return etapa.getFechaRealFin();
			}
		}
		
		return null;
	}
	
	public String obtenerEstadoRep(ReparacionCliente rep)
	{
		String estado="";
		
		if(rep.isAprobada()==false && rep.getEstado().equals(""))
			estado = "Esperando aprobacion de inicio";
		else if(rep.getEstado().equals("REC"))
			estado = "Cancelado por el cliente";
		else if(rep.getEstado().equals("FIN"))
			estado = "Trabajo de taller finalizado";
		else if(rep.isAprobada() && rep.getEstado().equals("PEN"))
			estado = "En proceso";
		else if(rep.getEstado().equals("DLV"))
			estado = "Aparato entregado al cliente";
		
		
		return estado;
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

	public List<EtapaRepCliente> getResultList() {
		return resultList;
	}

	public void setResultList(List<EtapaRepCliente> resultList) {
		this.resultList = resultList;
	}

	public ProcesoTaller getProcesoTll() {
		return procesoTll;
	}

	public void setProcesoTll(ProcesoTaller procesoTll) {
		this.procesoTll = procesoTll;
	}

	public EtapaReparacion getEtapaRep() {
		return etapaRep;
	}

	public void setEtapaRep(EtapaReparacion etapaRep) {
		this.etapaRep = etapaRep;
	}

	public List<ReparacionCliente> getReparaciones() {
		return reparaciones;
	}

	public void setReparaciones(List<ReparacionCliente> reparaciones) {
		this.reparaciones = reparaciones;
	}

	public List<ItemRequisicionEta> getItemsRequisicion() {
		return itemsRequisicion;
	}

	public void setItemsRequisicion(List<ItemRequisicionEta> itemsRequisicion) {
		this.itemsRequisicion = itemsRequisicion;
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

	public List<Object[]> getTrabajosTaller() {
		return trabajosTaller;
	}

	public void setTrabajosTaller(List<Object[]> trabajosTaller) {
		this.trabajosTaller = trabajosTaller;
	}

	
	
	
	
	
	
	
}
