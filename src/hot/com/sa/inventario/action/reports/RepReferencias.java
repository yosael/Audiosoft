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

import com.sa.model.crm.Cliente;
import com.sa.model.crm.MedioDifusion;
import com.sa.model.medical.DoctorExterno;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.medical.MedicalAppointmentService;
import com.sa.model.sales.Service;
import com.sa.model.security.Usuario;



@Name("repReferencias")
@Scope(ScopeType.CONVERSATION)
public class RepReferencias extends MasterRep implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Object[]> referenciasDoctoresLs = new ArrayList<Object[]>();
	private List<Object[]> referenciasDoctores = new ArrayList<Object[]>();
	private List<Cliente> listaClientes=new ArrayList<Cliente>();
	private List<DoctorExterno> listaDoctores= new ArrayList<DoctorExterno>();
	private List<MedioDifusion> listaMedios=new ArrayList<MedioDifusion>();
	private List<Object[]> referenciasMedios=new ArrayList<Object[]>();
	private List<Object[]> referenciasNulas=new ArrayList<Object[]>();
	private List<Object[]> listaServiciosReferidos = new ArrayList<Object[]>(); 
	private int idDoctorSelected=0;
	
	
	@In
	private EntityManager entityManager;
	
	
	
	public void cargarReferenciasDoc()
	{
		
		listaDoctores.clear();
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		String filtFecha="";
		
		if(fechaInicio != null && fechaFin == null) 
			filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
		else {
			filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		listaDoctores=entityManager.createQuery("SELECT doc FROM DoctorExterno doc").getResultList();

		Double sumaIngreso=0d;
		
		referenciasDoctores.clear();
		
		for(DoctorExterno doc:listaDoctores)
		{
			Object[] ref= new Object[4];
			sumaIngreso=0d;
			ref[0]=doc.getId();
			ref[1]=doc.getNombres()+" "+doc.getApellidos();

			
			listaClientes=entityManager.createQuery("SELECT c FROM Cliente c where c.doctorRef.id="+doc.getId()+" "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();
			
			
			for(Cliente c:listaClientes)
			{
				sumaIngreso+=obtenerIngresoConsulta(c);
			}
			
			ref[2]=obtenerTotalReferidos(doc);
			ref[3]=sumaIngreso;
			
			referenciasDoctores.add(ref);
			
		}
		
	}
	
	
	
	public void obtenerDetalleServiciosRef()
	{
		System.out.println("Entro al detalle Servicios Ref");
		
		listaServiciosReferidos.clear();
		//Obtener lista de servicios medicos
		List<Service> listaServicios= entityManager.createQuery("SELECT s FROM Service s where s.tipoServicio='MED' or s.tipoServicio='EXA' ").getResultList();
		
	  //Recorrer servicios y por cada servicio extraer la suma o cantidad de servicios que ha referido el doctor
		for(Service service: listaServicios)
		{
			Object[] servicioReferido= new Object[3];
			
			servicioReferido[0]=service.getCodigo();
			
			servicioReferido[1]=service;
			
			servicioReferido[2]=cantidadServiciosRef(service);//Select sum from medicalAppointServr where service=service and medicalAppointment.cliente
			
			//servicioReferido[2]=
			listaServiciosReferidos.add(servicioReferido);
		}
				
	}
	
	public String obtenerNombreDoctor()
	{
		DoctorExterno doctor=(DoctorExterno) entityManager.createQuery("SELECT d FROM DoctorExterno d where d.id=:idDoctor ").setParameter("idDoctor", idDoctorSelected).getSingleResult();
		
		return doctor.getNombres()+" "+doctor.getApellidos();
		
	}
	
	public int cantidadServiciosRef(Service service)
	{
		
		int cantidadServicios=0;
		String filtFecha="";
		
		if(fechaInicio != null && fechaFin == null) 
			filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
		else {
			filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		//Consultar en medicalAppointmentService Parametros: Service, MedicalAppointment-> Cliente-> Medico;
		
		listaClientes=entityManager.createQuery("SELECT c FROM Cliente c where c.doctorRef.id="+idDoctorSelected+" "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();
		
		List<MedicalAppointment> listaCitas = new ArrayList<MedicalAppointment>();
		
		
		
		
		for(Cliente c:listaClientes)
		{
			//sumaIngreso+=obtenerIngresoConsulta(c);
			//MedicalAppointment medicalAppointment = new MedicalAppointment();
			List<MedicalAppointment> listaMed = new ArrayList<MedicalAppointment>();
			//listaCitas = entityManager.createQuery("SELECT m FROM MedicalAppointment m where m.cliete.doctorRef=:doctorRefiere and ").getResultList();				
			listaMed = (List<MedicalAppointment>) entityManager.createQuery("SELECT m FROM MedicalAppointment m where  m.id=(SELECT MIN(me.id) FROM MedicalAppointment me where me.cliente.id=:idCliente )")
					.setParameter("idCliente", c.getId())
					.getResultList();
			
			System.out.println("Tamanio de citas medicas seleccionadas: "+ listaMed.size());
			
			//listaCitas.add(medicalAppointment);
			if(listaMed.size()>0)
			{
				if(listaMed.get(0).getMedicalAppointmentServices().size()>0)
				{
					for(MedicalAppointmentService mediService:listaMed.get(0).getMedicalAppointmentServices())
					{
						if(mediService.getService().getId()==service.getId())
						{
							cantidadServicios++;
						}
					}
				}
			}
			
		}
		
		//Cargar la lista de medicalAppointment que ha referido el doctor;
		
				//.setParameter("doctorRefiere", doctorSelected)
				//.getResultList();
		
		return cantidadServicios;
	}
	
	
	//Monto ingresado por la consulta que se dio el dia que se registro el paciente. Tuvo que generarse una venta esa fecha, a cargo de ese paciente.La fecha que se registro, tambien se genero una venta
	public Double obtenerIngresoConsulta(Cliente c)
	{
		
		Double sumaIngreso=0d;

		Date fecha1;
		Date fecha2;
		
		//Otras opciones
		/*
		   -La fecha de la primera consulta del paciente. Podria determinarse con el id de menor tamanio
		    
		  */
		
		//Posible try catch , posible devuelta float
		
		try
		{
			//idApp=(Integer) entityManager.createQuery("SELECT min(ap2.id) FROM MedicalAppointment ap2 where ap2.cliente.id="+c.getId()+"").getSingleResult();// and DATE(det.venta.fechaVenta)=(SELECT DATE(ap.dateTime) FROM MedicalAppointment ap where ap.cliente.id="+c.getId()+" and ap.id=(SELECT min(ap2.id) FROM MedicalAppointment ap2 where ap2.cliente.id="+c.getId()+"))
			Date fecha=(Date) entityManager.createQuery("SELECT ap.dateTime FROM MedicalAppointment ap where ap.cliente.id="+c.getId()+" and ap.id=(SELECT min(ap2.id) FROM MedicalAppointment ap2 where ap2.cliente.id="+c.getId()+")").getSingleResult();
			
			
			fecha1=truncDate(fecha,false);
			fecha2=truncDate(fecha,true);
			
			
			//sumaIngreso=(Double) entityManager.createQuery("SELECT sum(det.monto) FROM DetVentaProdServ det where det.venta.cliente.id="+c.getId()+"").getSingleResult();
			sumaIngreso=(Double) entityManager.createQuery("SELECT sum(det.monto) FROM DetVentaProdServ det where det.venta.cliente.id="+c.getId()+" and det.venta.fechaVenta BETWEEN :f1 and :f2 ").setParameter("f1", fecha1).setParameter("f2", fecha2).getSingleResult();
			
			
			if(sumaIngreso==null || sumaIngreso<=0)
				sumaIngreso=0d;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getLocalizedMessage());
			//System.out.println(e.getStackTrace());
			//e.getStackTrace();
		}
		//if(sum!=null)
		
		
		return sumaIngreso;
	}
	
	public Long obtenerTotalReferidos(DoctorExterno doc)
	{
		Long suma=0l;
		
		
		String filtFecha="";
				
				if(fechaInicio != null && fechaFin == null) 
					filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
				else if(fechaInicio == null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
				else if(fechaInicio != null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
				else {
					filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
					Calendar calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
					calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
					calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
					setFechaFin(resetTimeDate(calTmp.getTime(), 2));
				}
				

		try {
			suma=(Long) entityManager.createQuery("SELECT COUNT(c) FROM Cliente c where c.doctorRef.id="+doc.getId()+" "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getSingleResult();
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getLocalizedMessage());
		}
		
		
		return suma;
	}
	
	
	public void cargarReferenciaMedios()
	{
		
		
		listaMedios.clear();
		
		//Verificamos si pusieron alguna fecha
		setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
		setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
		
		String filtFecha="";
		
		if(fechaInicio != null && fechaFin == null) 
			filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
		else {
			filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
			Calendar calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
			calTmp = new GregorianCalendar();
			calTmp.set(Calendar.DATE, 1);
			calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
			calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
			setFechaFin(resetTimeDate(calTmp.getTime(), 2));
		}
		
		
		listaMedios=entityManager.createQuery("SELECT med FROM MedioDifusion med").getResultList();

		Double sumaIngreso=0d;
		
		referenciasMedios.clear();
		
		for(MedioDifusion medio:listaMedios)
		{
			Object[] ref= new Object[4];
			sumaIngreso=0d;
			ref[0]=medio.getId();
			ref[1]=medio.getNombre();

			
			listaClientes=entityManager.createQuery("SELECT c FROM Cliente c where c.mdif.id="+medio.getId()+" "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();
			//Solucion. Contar el total de medios de difusion y referencia en 2 consultas aparte y agregar a la suma
			
			for(Cliente c:listaClientes)
			{
				sumaIngreso+=obtenerIngresoConsulta(c);
			}
			
			ref[2]=obtenerTotalReferidosMed(medio);
			ref[3]=sumaIngreso;
			
			referenciasMedios.add(ref);
			
		}
		
		
	}
	
	public Long obtenerTotalReferidosMed(MedioDifusion medio)
	{
		Long suma=0l;
		
		
		String filtFecha="";
				
				if(fechaInicio != null && fechaFin == null) 
					filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
				else if(fechaInicio == null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
				else if(fechaInicio != null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
				else {
					filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
					Calendar calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
					calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
					calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
					setFechaFin(resetTimeDate(calTmp.getTime(), 2));
				}
				

		try {
			suma=(Long) entityManager.createQuery("SELECT COUNT(c) FROM Cliente c where c.mdif.id="+medio.getId()+" "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getSingleResult();
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			System.out.println(e.getLocalizedMessage());
		}
		
		
		return suma;
	}
	
	
	
	public void excelReferenciaDoc() throws IOException
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
				"attachment;filename=RepReferenciaDoctores-"
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
					celda.setCellValue("Doctor");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Pacientes Referidos");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Ingreso");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					for(Object[] ref: referenciasDoctores)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//doctor
						celda.setCellValue(ref[1].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //Pacientes referidos
						celda.setCellValue((Long)ref[2]);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Total Ingreso
						celda.setCellValue((Double)ref[3]);
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
	
	
	public void obtenerPacientesSinRef()
	{
		
		
		//Verificamos si pusieron alguna fecha
				setFechaInicio(fechaInicio!=null?resetTimeDate(fechaInicio, 1):null);
				setFechaFin(fechaFin!=null?resetTimeDate(fechaFin, 2):null);
				
				String filtFecha="";
				
				if(fechaInicio != null && fechaFin == null) 
					filtFecha = " AND c.fechaCreacion >= :f1 AND :f2 IS NULL ";
				else if(fechaInicio == null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion <= :f2 AND :f1 IS NULL ";
				else if(fechaInicio != null && fechaFin != null)
					filtFecha = " AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
				else {
					filtFecha = "  AND c.fechaCreacion BETWEEN :f1 AND :f2 ";
					Calendar calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					setFechaInicio(resetTimeDate(calTmp.getTime(), 1));
					calTmp = new GregorianCalendar();
					calTmp.set(Calendar.DATE, 1);
					calTmp.set(Calendar.MONTH, calTmp.get(Calendar.MONTH) + 1);
					calTmp.set(Calendar.DAY_OF_YEAR, calTmp.get(Calendar.DAY_OF_YEAR) - 1);
					setFechaFin(resetTimeDate(calTmp.getTime(), 2));
				}
				
				
				listaClientes=entityManager.createQuery("SELECT c FROM Cliente c where c.mdif=null and c.doctorRef=null and c.referidoPor=null "+filtFecha+" ").setParameter("f1", fechaInicio).setParameter("f2", fechaFin).getResultList();

	}
	
	
	public String obtenerUser(Integer idUsuario)
	{
		String user="";
		
		if(idUsuario>0 && idUsuario!=null)
			user=(String) entityManager.createQuery("SELECT u.nombreUsuario FROM Usuario u where u.id="+idUsuario+"").getSingleResult();
		
		return user;
	}
	
	public Date fechaPrimeraConsulta(Cliente c)
	{
		Date fecha=null;
		try
		{
			fecha=(Date) entityManager.createQuery("SELECT ap.dateTime FROM MedicalAppointment ap where ap.cliente.id="+c.getId()+" and ap.id=(SELECT min(ap2.id) FROM MedicalAppointment ap2 where ap2.cliente.id="+c.getId()+")").getSingleResult();
		}
		catch (Exception e) {
			fecha=null;
		}
		
		return fecha;
	}
	
	
	
	public void excelReferenciaMedios() throws IOException
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
				"attachment;filename=RepReferenciaMedios-"
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
					celda.setCellValue("Medio de difusion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Pacientes");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Ingreso");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					for(Object[] ref: referenciasMedios)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//doctor
						celda.setCellValue(ref[1].toString());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1); //Pacientes referidos
						celda.setCellValue((Long)ref[2]);
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						 
						celda=fila.createCell(2); //Total Ingreso
						celda.setCellValue((Double)ref[3]);
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


	public List<Object[]> getReferenciasDoctores() {
		return referenciasDoctores;
	}


	public void setReferenciasDoctores(List<Object[]> referenciasDoctores) {
		this.referenciasDoctores = referenciasDoctores;
	}


	public List<Object[]> getReferenciasMedios() {
		return referenciasMedios;
	}


	public void setReferenciasMedios(List<Object[]> referenciasMedios) {
		this.referenciasMedios = referenciasMedios;
	}


	public List<Cliente> getListaClientes() {
		return listaClientes;
	}


	public void setListaClientes(List<Cliente> listaClientes) {
		this.listaClientes = listaClientes;
	}



	public int getIdDoctorSelected() {
		return idDoctorSelected;
	}



	public void setIdDoctorSelected(int idDoctorSelected) {
		this.idDoctorSelected = idDoctorSelected;
	}



	public List<Object[]> getListaServiciosReferidos() {
		return listaServiciosReferidos;
	}



	public void setListaServiciosReferidos(List<Object[]> listaServiciosReferidos) {
		this.listaServiciosReferidos = listaServiciosReferidos;
	}



	
	
	
}
