package com.sa.inventario.action.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import com.sa.model.crm.Cliente;
import com.sa.model.security.Sucursal;

@Name("repClientes")
@Scope(ScopeType.CONVERSATION)
public class RepClientes extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Cliente> resultList;
	private Integer totalNuevos;
	
	private List<Sucursal> sucursales;
	
	@In
	private EntityManager entityManager;
	
	private Sucursal sucursal;
	
	
	public void obtenerClientesNuevos()
	{
		
		String jpql="SELECT c FROM Cliente c where 1=1 ";
		
		if(fechaInicio != null && fechaFin == null) 
			jpql += " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			jpql += " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			jpql += " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
		else {
			jpql += "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		
		resultList = entityManager.createQuery(jpql).setParameter("f1", fechaInicio).setParameter("f2", fechaFin) .getResultList();
		totalNuevos=resultList.size();
		
	}
	
	public boolean faltaInformacion(Cliente cliente)
	{
		
		if(cliente.getPais()!=null && cliente.getPais().getId()==68)
		{
			if(cliente.getTelefono1()==null || cliente.getDireccion()==null || cliente.getFechaNacimiento()==null || cliente.getOcupacion()==null || (cliente.getReferidoPor()==null && cliente.getDoctorRef()==null && cliente.getMdif()==null) || cliente.getMunicipio()==null || cliente.getDepto()==null)
			{
				return true;
			}
		}
		else
		{
			if(cliente.getTelefono1()==null || cliente.getDireccion()==null || cliente.getFechaNacimiento()==null || cliente.getOcupacion()==null || (cliente.getReferidoPor()==null && cliente.getDoctorRef()==null && cliente.getMdif()==null))
			{
				return true;
			}
		}
		
		
		return false;
	}
	
	public void cargarSucursales(){
		
		System.out.println("Cargo las sucursales repClientes");
		sucursales = entityManager.createQuery("select s from Sucursal s where  s.bodega=null or s.bodega=false or (s.sucursalSuperior=null and s.bodega=true)")
											.getResultList();
	}
	
	
	public void excelClientesNuevos() throws IOException
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
				"attachment;filename=clientesNuevos-"
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
					celda.setCellValue("Nombres");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Apellidos");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Fecha Registro");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Medio Referido");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Doctor que refiere");
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
					
					for(Cliente cli: resultList)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//nombres
						celda.setCellValue(cli.getNombres());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //Servicio
						celda.setCellValue(cli.getApellidos());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Fecha Registro
						celda.setCellValue(cli.getFechaCreacion());
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(2);
						 
						celda=fila.createCell(3); //Medio referido
						celda.setCellValue(cli.getMedioReferido());
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //Medio referido
						if(cli.getDoctorRef()!=null)
							celda.setCellValue(cli.getDoctorRef().getNombreCompleto());
						else
							celda.setCellValue("Sin referencia");
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(4);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	
	public void reporteClientesSucursal()
	{
		//String jpql="SELECT cli.nombres || ' ' || cli.apellidos nomCliente,cli.telefono1,cli.direccion,cli.fecha_nacimiento,cli.doc_id FROM Cliente c where 1=1 ";
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT c FROM Cliente c where 1=1 ");
		
		if(fechaInicio != null && fechaFin == null) 
			jpql.append(" AND c.fechaCreacion >= :f1 AND :f2 IS NULL ");
		else if(fechaInicio == null && fechaFin != null)
			jpql.append(" AND c.fechaCreacion <= :f2 AND :f1 IS NULL ");
		else if(fechaInicio != null && fechaFin != null)
			jpql.append(" AND c.fechaCreacion BETWEEN :f1 AND :f2 ");
		else {
			jpql.append("  AND c.fechaCreacion BETWEEN :f1 AND :f2 ");
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		if(sucursal!=null && sucursal.getSubSucursales().size()>0)
		{
			System.out.println("Sucursal superior id");
			jpql.append(" AND c.usuarioRegistro IN (SELECT u.id FROM Usuario u where u.sucursal.id=:idSucursal or u.sucursal.id in (select s.id FROM Sucursal s where s.sucursalSuperior.id=:idSucSuperior))");
			resultList = entityManager.createQuery(jpql.toString())
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("idSucursal", sucursal.getId())
					.setParameter("idSucSuperior", sucursal.getId())
					.getResultList();
		}
		else if(sucursal!=null  && sucursal.getSubSucursales().size()<=0)
		{
			System.out.println("Sucursal  id");
			jpql.append(" AND c.usuarioRegistro IN (SELECT u.id FROM Usuario u where u.sucursal.id=:idSucursal)");
			resultList = entityManager.createQuery(jpql.toString())
					.setParameter("f1", fechaInicio)
					.setParameter("f2", fechaFin)
					.setParameter("idSucursal", sucursal.getId())
					.getResultList();
		}
		
		
		
		
		
	}
	
	public void excelClientesSucursal() throws IOException
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
				"attachment;filename=clientesPorSucursal"
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
					celda.setCellValue("Nombres");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Apellidos");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Telefono");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Direccion");
					celda.setCellStyle(stTitles);;
					
					celda = fila.createCell(4);
					celda.setCellValue("Fecha Nacimiento");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Depto");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Municipio");
					celda.setCellStyle(stTitles);;
					
					celda = fila.createCell(7);
					celda.setCellValue("Ocupacion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Fecha Registro");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Medio Referido");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Doctor que refiere");
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
					
					for(Cliente cli: resultList)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//nombres
						celda.setCellValue(cli.getNombres());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //apellidos
						celda.setCellValue(cli.getApellidos());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Telefono
						celda.setCellValue(cli.getTelefono1());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //Direccion
						celda.setCellValue(cli.getDireccion());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //Fecha Nacimiento
						if(cli.getFechaNacimiento()!=null)
						{
							celda.setCellValue(cli.getFechaNacimiento());
							celda.setCellStyle(stDate);
						}
						else
						{
							celda.setCellValue("");
							celda.setCellStyle(stList);
						}
						hoja.autoSizeColumn(4);
						
						celda=fila.createCell(5); //Depto
						celda.setCellValue(cli.getDepto()!=null?cli.getDepto().getNombre():"");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(5);
						
						celda=fila.createCell(6); //Municipio
						celda.setCellValue(cli.getMunicipio()!=null?cli.getMunicipio().getNombre():"");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(6);
						
						celda=fila.createCell(7); //Ocupacion
						celda.setCellValue(cli.getOcupacion()!=null?cli.getOcupacion():"");
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //Fecha Registro
						celda.setCellValue(cli.getFechaCreacion());
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(8);
						 
						celda=fila.createCell(9); //Medio referido
						celda.setCellValue(cli.getMedioReferido()!=null?cli.getMedioReferido():"");
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //Medio referido
						if(cli.getDoctorRef()!=null)
							celda.setCellValue(cli.getDoctorRef().getNombreCompleto());
						else
							celda.setCellValue("Sin referencia");
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(10);
						
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}


	public List<Cliente> getResultList() {
		return resultList;
	}


	public void setResultList(List<Cliente> resultList) {
		this.resultList = resultList;
	}


	public Integer getTotalNuevos() {
		return totalNuevos;
	}


	public void setTotalNuevos(Integer totalNuevos) {
		this.totalNuevos = totalNuevos;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public Sucursal getSucursal() {
		return sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}
	
	

}
