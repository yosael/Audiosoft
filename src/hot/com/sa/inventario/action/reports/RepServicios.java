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

import com.sa.model.sales.PojoVentasApa;
import com.sa.model.security.Sucursal;


@Name("repServicios")
@Scope(ScopeType.CONVERSATION)
public class RepServicios extends MasterRep implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@In
	private EntityManager entityManager;
	private List<Object[]> ventasServPadre;
	private List<Object[]> ventasServHijo; 
	private Sucursal sucursal;
	private int mesSelected;
	private String tipoServicio="";
	//private Date fechaIni= new Date();
	//private Date fechaF=new Date();
	
	
	public void obtenerMesActual(int numMes)
	{
		
		int mesActual;
		int mesAnterior;
		
		Calendar cal = Calendar.getInstance();
		mesActual=cal.get(Calendar.MONTH);
		mesAnterior=cal.get(Calendar.MONTH)-1;
		
		
	}
	
	public void obtenerMesAnterior(int numMes)
	{
		
		
	}
	
	
	public void ventaServPadre()
	{
		
		
		//fechaIni=fechaInicio;
		//fechaF=fechaFin;
		
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
				
		//select s.tipo_servicio,sum(det.monto) as total from det_venta_prod_serv det inner join service s on(s.codigo=det.cod_exacto) group by s.tipo_servicio order by s.tipo_servicio asc;		
		String jpql="SELECT s.tipoServicio,SUM(det.monto),COUNT(det.codExacto) FROM DetVentaProdServ det,Service s where s.codigo=det.codExacto ";
		
		String condi=" group by s.tipoServicio order by s.tipoServicio )";
		
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
		
		if(sucursal!=null)
		{
			jpql+=" AND det.venta.sucursal.id="+sucursal.getId()+"";
			
		}
		
		
		
		jpql+=condi;
		
		
		ventasServPadre=entityManager.createQuery(jpql).setParameter("f1", fechaInicio).setParameter("f2", fechaFin) .getResultList();
		
		System.out.println(ventasServPadre.size());
		
		
		
	}
	
	
	public void ventaServHijo()
	{
		
		int mesActual;
		int mesAnterior;
		int anio;
		
		Calendar cal = Calendar.getInstance();
		mesActual=cal.get(Calendar.MONTH)+1;
		mesAnterior=cal.get(Calendar.MONTH);
		anio=cal.get(Calendar.YEAR);
		
		System.out.println("Mes actual"+ mesActual);
		System.out.println("Mes anterior"+ mesAnterior);
		
		System.out.println("Tipo "+ tipoServicio);
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);

		
		
		//select det.cod_exacto,s.name,sum(det.monto) as total from det_venta_prod_serv det inner join service s on(s.codigo=det.cod_exacto) where s.tipo_servicio='EXA' group by det.cod_exacto,s.name order by det.cod_exacto asc;		
		String jpql="SELECT det.codExacto,s.name,SUM(det.monto),COUNT(det.codExacto) FROM DetVentaProdServ det,Service s where s.codigo=det.codExacto and s.tipoServicio='"+tipoServicio+"' ";
		//String jpql="SELECT det.codExacto,s.name,(SELECT SUM(monto) FROM det ) FROM DetVentaProdServ det,Service s where s.codigo=det.codExacto and s.tipoServicio='"+tipoServicio+"' group by det.codExacto,s.name order by det.codExacto)";
		String condi=" group by det.codExacto,s.name order by det.codExacto)";
		//listaServicios="SELECT s.codigo from Service s where s.tipoServicio='"+tipoServicio+"'";
		
		//String jpql1="SELECT det.codExacto,s.name,SUM(det.monto) FROM DetVentaProdServ det,Service s where s.codigo=det.codExacto and s.tipoServicio='"+tipoServicio+"' and MONTH(det.venta.fechaVenta)="+mesAnterior+" and YEAR(det.venta.fechaVenta)="+anio+" group by det.codExacto,s.name order by det.codExacto)";
		//String jpql2="SELECT det.codExacto,s.name,SUM(det.monto) FROM DetVentaProdServ det,Service s where s.codigo=det.codExacto and s.tipoServicio='"+tipoServicio+"' and MONTH(det.venta.fechaVenta)="+mesActual+" and YEAR(det.venta.fechaVenta)="+anio+" group by det.codExacto,s.name order by det.codExacto)";
		
		//String jpqlServicios="SELECT s.codigo,s.name from Service s where s.tipoServicio='"+tipoServicio+"'  order by s.codigo";
		
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
		
		
		if(sucursal!=null)
		{
			jpql+=" AND det.venta.sucursal.id="+sucursal.getId()+"";
			
		}
		
		
		
		jpql+=condi;
		
		ventasServHijo=entityManager.createQuery(jpql).setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();
		/*listaServicios=entityManager.createQuery(jpqlServicios).getResultList();
		
		lista1=entityManager.createQuery(jpql1).getResultList();
		lista2=entityManager.createQuery(jpql2).getResultList();
		ventasServHijo= new ArrayList<Object[]>();
		
		Object item[];
		
		for(Object[] serv: listaServicios)
		{
			
			item = new Object[4];
			
			item[0]=serv[0];//codigo 
			item[1]=serv[1];// nombre del servicio
			
			
			for(Object[] ob1: lista1)
			{
				
				if(serv[0].equals(ob1[0]))
				{
					System.out.println("Entro al mesAnterior");
					item[2]=(Double)ob1[2];  //total mes anterior
				}
				else
					item[2]=0d;
				
				
			}
			for(Object[] ob2: lista2)
			{
				
				if(serv[0].equals(ob2[0]))
				{
					System.out.println("Entro al mesActual");
					item[3]=(Double)ob2[2]; //total mes actual
				}
				else
					item[3]=0d;
				
				
			}
			
			ventasServHijo.add(item);
		}
		*/
		
		
		System.out.println(ventasServHijo.size());
		//System.out.println(lista2.size());
		
		
		
		
		
		
	}
	
	
	
	
	public String obtenerNombreServicio(Object[] ob)
	{
		if(ob[0].equals("CMB"))
			return "Combos";
		else if(ob[0].equals("EXA"))
			return "Examenes medicos";
		else if(ob[0].equals("MED"))
			return "Servicios medicos";
		else
			return "Servicios de taller";
		
	}
	
	public String obtenerNombreServicio(String ob)
	{
		if(ob.equals("CMB"))
			return "Combos";
		else if(ob.equals("EXA"))
			return "Examenes medicos";
		else if(ob.equals("MED"))
			return "Servicios medicos";
		else
			return "Servicios de taller";
		
	}
	
	
	public void excelVentasPadre() throws IOException
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
				"attachment;filename=ventaServicios-"
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
		headfontW.setFontHeightInPoints((short) 12);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 10);
		//headfont3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		

					HSSFCellStyle stAling = libro.createCellStyle(), stDate = libro
							.createCellStyle(), stAlingRight = libro.createCellStyle(), stTitles = libro.createCellStyle(), stTotals = libro.createCellStyle(), stList = libro
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
					celda.setCellValue("Servicio");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Cantidad Vendida");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Ingreso");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					for(Object[] serv: ventasServPadre)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//Tipo de servicio
						celda.setCellValue(obtenerNombreServicio(serv));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //Cantidad Vendida
						celda.setCellValue((Long)serv[2]);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Total
						celda.setCellValue((Double)serv[1]);
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(2);
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
		
		
		
		
	}
	
	public void excelVentasHijo() throws IOException
	{
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String fechaVentas="Del "+sdf.format(fechaInicio)+" al "+sdf.format(fechaFin);

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader(
				"Content-Disposition",
				"attachment;filename=ventaServicios-"
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
		//headfont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		headfontW.setFontName("Arial");
		headfontW.setFontHeightInPoints((short) 12);
		headfontW.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headfontW.setColor(HSSFColor.WHITE.index);
		
		headfont3.setFontName("Arial");
		headfont3.setFontHeightInPoints((short) 10);
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
					stDate.setAlignment(stAling.ALIGN_CENTER);

					stTitles.setVerticalAlignment(stTitles.VERTICAL_CENTER);
					stTitles.setAlignment(stTitles.ALIGN_CENTER);
					stTitles.setFont(headfontW);
					stTitles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					//stTitlesD.setFillBackgroundColor(HSSFColor.RED.index);
					stTitles.setFillForegroundColor(HSSFColor.GREEN.index);
					
		

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
					celda.setCellValue("Codigo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Servicio");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Cantidad Vendida");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Ingreso");
					celda.setCellStyle(stTitles);
					
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					fila=hoja.createRow(0);
					celda=fila.createCell(1);
					celda.setCellValue(fechaVentas);
					celda.setCellStyle(stDate);
					
					
					
					
					int contFila=2;//,contCelda=0;
					
					for(Object[] serv: ventasServHijo)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//Codigo
						celda.setCellValue(serv[0].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //Servicio
						celda.setCellValue(serv[1].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Cantidad Vendida
						celda.setCellValue((Long)serv[3]);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						 
						celda=fila.createCell(3); //totalVenta
						celda.setCellValue((Double)serv[2]);
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(3);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
		
	}


	public List<Object[]> getVentasServPadre() {
		return ventasServPadre;
	}


	public void setVentasServPadre(List<Object[]> ventasServPadre) {
		this.ventasServPadre = ventasServPadre;
	}


	public List<Object[]> getVentasServHijo() {
		return ventasServHijo;
	}


	public void setVentasServHijo(List<Object[]> ventasServHijo) {
		this.ventasServHijo = ventasServHijo;
	}


	public Sucursal getSucursal() {
		return sucursal;
	}


	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}


	public int getMesSelected() {
		return mesSelected;
	}


	public void setMesSelected(int mesSelected) {
		this.mesSelected = mesSelected;
	}


	public String getTipoServicio() {
		return tipoServicio;
	}


	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}
	
	
	
	

}
