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

@Name("repTallerEtapas")
@Scope(ScopeType.CONVERSATION)
public class RepTallerEtapas extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	
	@In
	private EntityManager entityManager;
		
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	@In(required = true,create=true)
	protected ReparacionClienteHome reparacionClienteHome;
	
	private List<EtapaRepCliente> resultList = new ArrayList<EtapaRepCliente>();
	private List<ReparacionCliente> reparaciones = new ArrayList<ReparacionCliente>();	
	HashMap<String, Object> dtRp = new HashMap<String, Object>();
	
	private ProcesoTaller procesoTll;
	private Sucursal sucursal;
	private EtapaReparacion etapaRep;
	
	private List<Object[]> trabajosTaller= new ArrayList<Object[]>();
	
	
	//
	private List<EtapaRepCliente> etapasIngresoTaller = new ArrayList<EtapaRepCliente>();
	private List<EtapaRepCliente> etapasDiagTaller = new ArrayList<EtapaRepCliente>();
	private List<EtapaRepCliente> etapasRepTaller = new ArrayList<EtapaRepCliente>();
	
	
	public void resetClass() {
		hql = "";
		
		resultList = new ArrayList<EtapaRepCliente>();
		reparaciones = new ArrayList<ReparacionCliente>();		
		dtRp = new HashMap<String, Object>();
		procesoTll = null;
		sucursal = null;
		etapaRep = null;
		
		
		resetMainClass();
	}
	
	public void setDiaActual() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, 0);
		setFechaInicio(truncDate(cal.getTime(), true));		
		setFechaFin(truncDate(cal.getTime(), false));
		//calendario= new GregorianCalendar();
		//calendario.add(Calendar.DATE, 0);
	}
	
	public void cargarTodasEtapas()
	{
		cargarEtapasIngreso();
		cargarEtapasDiag();
		cargarEtapasReparacion();
	}
	
	public void cargarEtapasIngreso()
	{
		
		etapasIngresoTaller = entityManager.createQuery("Select e FROM EtapaRepCliente e where e.estado='PEN' AND e.etapaRep.id=101 AND e.reparacionCli.fechaIngreso>=:fechaInicio and e.reparacionCli.fechaIngreso<=:fechaFin")
				.setParameter("fechaInicio", fechaInicio)
				.setParameter("fechaFin", fechaFin)
				.getResultList();
	}
	
	public void cargarEtapasDiag()
	{
		
		etapasDiagTaller = entityManager.createQuery("Select e FROM EtapaRepCliente e where e.estado='PEN' AND e.etapaRep.id=41 AND e.reparacionCli.fechaIngreso>=:fechaInicio and e.reparacionCli.fechaIngreso<=:fechaFin")
				.setParameter("fechaInicio", fechaInicio)
				.setParameter("fechaFin", fechaFin)
				.getResultList();
	}
	
	public void cargarEtapasReparacion()
	{
		
		etapasRepTaller = entityManager.createQuery("Select e FROM EtapaRepCliente e where e.estado='PEN' AND e.etapaRep.id=42 AND e.reparacionCli.fechaIngreso>=:fechaInicio and e.reparacionCli.fechaIngreso<=:fechaFin")
				.setParameter("fechaInicio", fechaInicio)
				.setParameter("fechaFin", fechaFin)
				.getResultList();
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

	public List<EtapaRepCliente> getEtapasIngresoTaller() {
		return etapasIngresoTaller;
	}

	public void setEtapasIngresoTaller(List<EtapaRepCliente> etapasIngresoTaller) {
		this.etapasIngresoTaller = etapasIngresoTaller;
	}

	public List<EtapaRepCliente> getEtapasDiagTaller() {
		return etapasDiagTaller;
	}

	public void setEtapasDiagTaller(List<EtapaRepCliente> etapasDiagTaller) {
		this.etapasDiagTaller = etapasDiagTaller;
	}

	public List<EtapaRepCliente> getEtapasRepTaller() {
		return etapasRepTaller;
	}

	public void setEtapasRepTaller(List<EtapaRepCliente> etapasRepTaller) {
		this.etapasRepTaller = etapasRepTaller;
	}

	
	
	
	
	
	
	
}
