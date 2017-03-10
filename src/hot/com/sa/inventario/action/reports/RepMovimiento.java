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
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.ReparacionCliente;

@Name("repMovimiento")
@Scope(ScopeType.CONVERSATION)
public class RepMovimiento extends MasterRep implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String hql;
	private String hqlCond;
	private String hqlOrder;
	@In
	private EntityManager entityManager;
	
	@In
	private LoginUser loginUser;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	
	private List<Inventario> inventarios = new ArrayList<Inventario>();
	private Inventario inventario;
	private Integer inventarioId;
	
	private List<Movimiento> movimientos = new ArrayList<Movimiento>();
	private List<Item> items = new ArrayList<Item>();
	private List<Object> itemsObj = new ArrayList<Object>();
	private List<ReparacionCliente> reparacionesPend = new ArrayList<ReparacionCliente>();
	private List<ReparacionCliente> etapasRetr = new ArrayList<ReparacionCliente>();
	
	private Empresa empresaSeleccionada;
	private Sucursal sucursalSeleccionada;
	private List<Sucursal> sucursales=new ArrayList<Sucursal>();
	
	private Categoria categoriaSeleccionada;
	private Producto productoSeleccionado;
	private List<Producto> productos=new ArrayList<Producto>();
	
	private String busquedaNumSerie="";
	
	private Object infoVenta[] = new Object[9];
	private Object infoCoti[] = new Object[6];
	private Object infoCompra[] = new Object[9];
	
	//Creados por ROD para manejar las variables de filtrado de items <= movimento
	private String tipoMovimiento;
	private String razon;
	private String orderBy = "movimiento.fecha";
	private String orderDir = "asc";
	
	public void resetClass() {
		fechaInicio = null;
		fechaFin = null;
		
		inventarios = new ArrayList<Inventario>();
		inventario = null;
		inventarioId = null;
		
		movimientos = new ArrayList<Movimiento>();
		items = new ArrayList<Item>();
		reparacionesPend = new ArrayList<ReparacionCliente>();
		etapasRetr = new ArrayList<ReparacionCliente>();
		
		empresaSeleccionada = null;
		sucursalSeleccionada = null;
		sucursales=new ArrayList<Sucursal>();
		
		categoriaSeleccionada = null;
		productoSeleccionado = null;
		productos=new ArrayList<Producto>();
		tipoMovimiento = "";
		razon = "";
	}
	
	
	public void buscarNumeroSerie()
	{
		
		//String busquedaNumSerie="";
		
		try{
			infoVenta = (Object[]) entityManager.createQuery("SELECT det.numSerie,det.detalle,vta.estado,det.monto,vta.cliente,vta.fechaVenta,vta.sucursal.nombre,vta.id,vta.usrEfectua.nombreUsuario FROM DetVentaProdServ det,VentaProdServ vta where det.venta=vta and det.numSerie='"+busquedaNumSerie+"'").getSingleResult();
		}
		catch(Exception e)
		{
			infoVenta=null;
		}
		
		//System.out.println("NumSerie: "+ infoVenta[0] + " detalle: "+infoVenta[1]);
		
		try{
			infoCoti = (Object[]) entityManager.createQuery("SELECT det.numSerie,det.venta.cotizacion.id,det.venta.id,det.venta.cliente,det.venta.cotizacion.fechaIngreso,det.venta.cotizacion,det.venta.cotizacion.usuarioGenera.nombreUsuario,det.venta.cotizacion.estado,det.venta.cotizacion.sucursal.nombre FROM DetVentaProdServ det where det.numSerie='"+busquedaNumSerie+"'").getSingleResult();
		}
		catch (Exception e) {
			infoCoti=null;
		}
		
		//System.out.println("Cotizacion " + infoCoti[3]);
		
		try
		{
			//NumSerie,producto,fechaIngreso,SucursalIngreso,Usuario
			infoCompra = (Object[]) entityManager.createQuery("SELECT c.numSerie,c.movimiento.tipoMovimiento,c.movimiento.fecha,c.movimiento.usuario.nombreUsuario,c.movimiento.sucursal.nombre,c.movimiento.razon,c.inventario.sucursal.nombre,c.movimiento.desde,c.movimiento.hacia FROM CodProducto c where c.numSerie='"+busquedaNumSerie+"' ").getSingleResult();//Consultar desde codProducto y movimiento la fecha de entrada y la sucursal
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			infoCompra=null;
		}
		
		
		//System.out.println("TipoMovimiento: "+infoCompra[0]);
		
	}
	
	
	public void cargarInventario(){
		inventario = entityManager.find(Inventario.class, inventarioId);
		this.consultarMovimientos();
	}
	
	@SuppressWarnings("unchecked")
	public void consultaInventarios(){
		inventarios = entityManager.createQuery("select i from Inventario i where i.sucursal = :sucursal " +
				"order by i.producto.categoria")
				.setParameter("sucursal", loginUser.getUser().getSucursal())
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public void verReparacionesPendientes(){
		Date date1 = null;
		Date date2 = null;
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		hql = "SELECT r FROM ReparacionCliente r WHERE r.estado = 'PEN' ";
		hqlCond = "";
		hqlCond +=(fechaInicio != null)?" AND r.fechaIngreso >= :fechaInicio ":"";
		hqlCond +=(fechaFin != null)?" AND r.fechaIngreso <= :fechaFin ":"";
		
		if(fechaInicio != null && fechaFin != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaInicio", date1)
					.setParameter("fechaFin", date2)
					.getResultList();
		else if(fechaInicio != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaInicio", date1)
					.getResultList();
		else if(fechaFin != null)
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.setParameter("fechaFin", date2)
					.getResultList();
		else
			reparacionesPend = entityManager.createQuery(hql + hqlCond)
					.getResultList();
		
	}
	
	@SuppressWarnings("unchecked")
	public void verEtapasRetrasadas(){
		Date date1 = null;
		Date date2 = null;
		Date dateToday = null;
		try{
			
			Calendar cal3 = Calendar.getInstance();
			cal3.set(Calendar.HOUR_OF_DAY, 23);
			cal3.set(Calendar.MINUTE, 59);
			cal3.set(Calendar.SECOND, 59);
			dateToday = cal3.getTime();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		hql = "SELECT e FROM EtapaRepCliente e WHERE e.reparacionCli.estado = 'PEN'  ";
		hqlCond = "";
		hqlCond += " AND e.fechaEstFin < :fechaHoy ";
		hqlCond +=(fechaInicio != null)?" AND e.reparacionCli.fechaIngreso >= :fechaInicio ":"";
		hqlCond +=(fechaFin != null)?" AND e.reparacionCli.fechaIngreso <= :fechaFin ":"";
		hqlOrder = " ORDER BY e.reparacionCli.fechaIngreso ASC, e.etapaRep.orden ASC ";
		
		if(fechaInicio != null && fechaFin != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaInicio", date1)
					.setParameter("fechaFin", date2)
					.getResultList();
		else if(fechaInicio != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaInicio", date1)
					.getResultList();
		else if(fechaFin != null)
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.setParameter("fechaFin", date2)
					.getResultList();
		else
			etapasRetr = entityManager.createQuery(hql + hqlCond + hqlOrder)
					.setParameter("fechaHoy", dateToday)
					.getResultList();
		
	}
	
	private String str1;
	private String str2;
	private Double totalMontoCheques;
	List<ChequeDoc> chequesDoc = new ArrayList<ChequeDoc>();	
	
	
	
	public void excelRepMovimiento() throws IOException
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
				"attachment;filename=ReporteMovimientos-"
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
					celda.setCellValue("Fecha");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Codigo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Categoria");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Producto");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Cantidad");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(5);
					celda.setCellValue("Precio Compra");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(6);
					celda.setCellValue("Precio Venta");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(7);
					celda.setCellValue("Tipo Movimiento");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(8);
					celda.setCellValue("Tipo Transaccion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(9);
					celda.setCellValue("Remitente");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(10);
					celda.setCellValue("Destinatario");
					celda.setCellStyle(stTitles);
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT r.estado,r.fechaEntrega,r.fechaIngreso,cond.condAparato.nombre,CONCAT(r.cliente.nombres,' ',r.cliente.apellidos),
					 * r.aparatoRep.marca,r.aparatoRep.modelo,r.aparatoRep.numSerie,r.id,r.costo,r,r.proceso.nombre FROM ReparacionCliente r,
					 * CondAparatoRep cond where r.id=cond.repCliente.id";*/
					
					int contFila=2;//,contCelda=0;
					
					for(Item rep: items)
					{
						fila = hoja.createRow(contFila);
						System.out.println("Fila "+contFila);
						
						celda=fila.createCell(0);//Fecha 
						celda.setCellValue((Date)rep.getMovimiento().getFecha());
						celda.setCellStyle(stDate);
						hoja.autoSizeColumn(0);
						 
						celda=fila.createCell(1); //Codigo
						celda.setCellValue(rep.getInventario().getProducto().getReferencia());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Categoria
						celda.setCellValue(rep.getInventario().getProducto().getCategoria().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //Producto
						celda.setCellValue(rep.getInventario().getProducto().getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4); //Cantidad 
						celda.setCellValue(rep.getCantidad());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						
												
						celda=fila.createCell(5); //Precio Compra
						
						if(rep.getCostoUnitario()!=null)
						{
							//if(rep.getMovimiento().getRazon().equals("V"))
								celda.setCellValue((Float)rep.getCostoUnitario());
							/*else
								celda.setCellValue("--");*/
						}
						/*else
							celda.setCellValue("--");*/
						
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(5);
						
						
						
						celda=fila.createCell(6); //Precio Venta
						
						if(rep.getPrecioVenta()!=null)
						{
							if(rep.getMovimiento().getRazon().equals("V"))
								celda.setCellValue((Float)rep.getPrecioVenta());
							/*else
								celda.setCellValue("");*/
						}
						/*else
							celda.setCellValue("");*/
						
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(6);
						
						
						
						celda=fila.createCell(7); //Tipo Movimiento	
						celda.setCellValue(obtenerTipoMovimiento(rep));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(7);
						
						celda=fila.createCell(8); //Tipo Transaccion
						celda.setCellValue(obtenerTipoTransaccion(rep));
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(8);
						
						celda=fila.createCell(9); //Remitente
						if(rep.getMovimiento().getDesde()!=null)
							celda.setCellValue(rep.getMovimiento().getDesde());
						/*else
							celda.setCellValue("");*/
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(9);
						
						celda=fila.createCell(10); //Destinatario
						
						if(rep.getMovimiento().getHacia()!=null)
							celda.setCellValue(rep.getMovimiento().getHacia());
						/*else
							celda.setCellValue("");*/
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(10);
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
		
	}
	
	
	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public Double getTotalMontoCheques() {
		return totalMontoCheques;
	}

	public void setTotalMontoCheques(Double totalMontoCheques) {
		this.totalMontoCheques = totalMontoCheques;
	}

	public List<ChequeDoc> getChequesDoc() {
		return chequesDoc;
	}

	public void setChequesDoc(List<ChequeDoc> chequesDoc) {
		this.chequesDoc = chequesDoc;
	}

	@SuppressWarnings("unchecked")
	public void chequesEmitidos(){

		if(fechaInicio == null){
			fechaInicio = new Date();
		}
		
		if(fechaFin == null){
			fechaFin = new Date();
		}					
		
		hql = "SELECT e FROM ChequeDoc e WHERE 1 = 1 AND (e.fecha >= :F1 AND e.fecha <= :F2)";
		
		
		if( str2 == null || str2.equals("") ){
			hql += " AND :P1 = :P1";
		}else{ 
			hql += " AND e.comprobante.empresaDoc.nombre = :P1";
		}
		
		if( str1 == null || str1.equals("") ){
			hql += " AND :P2 = :P2";
		}else{
			hql += " AND e.proveedor.razonSocial = :P2";
		}	
		
		chequesDoc = new ArrayList<ChequeDoc>();
		chequesDoc = entityManager.createQuery(hql)
				.setParameter("F1", fechaInicio)
				.setParameter("F2", fechaFin)
				.setParameter("P1", str2)
				.setParameter("P2", str1)
				.getResultList(); 
		
		System.out.println("Obteniendo listado de cheques");
		
		hql = "SELECT SUM(e.monto) FROM ChequeDoc e WHERE 1 = 1 AND (e.fecha >= :F1 AND e.fecha <= :F2)";
		
		if( str2 == null || str2.equals("") ){
			hql += " AND :P1 = :P1";
		}else{ 
			hql += " AND e.comprobante.empresaDoc.nombre = :P1";
		}
		
		if( str1 == null || str1.equals("") ){
			hql += " AND :P2 = :P2";
		}else{
			hql += " AND e.proveedor.razonSocial = :P2";
		}		
		
		totalMontoCheques = 0.0;
		totalMontoCheques = (Double) entityManager.createQuery(hql)
				.setParameter("F1", fechaInicio)
				.setParameter("F2", fechaFin)
				.setParameter("P1", str2)
				.setParameter("P2", str1)
				.getSingleResult();
	}	
	
	@SuppressWarnings("unchecked")
	public void consultarMovimientos(){
		Date date1 = null;
		Date date2 = null;
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(fechaInicio);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			date1 = cal.getTime();
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(fechaFin);
			cal2.set(Calendar.HOUR_OF_DAY, 23);
			cal2.set(Calendar.MINUTE, 59);
			cal2.set(Calendar.SECOND, 59);
			date2 = cal2.getTime();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		 
		if(fechaInicio==null && fechaFin==null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId")
					.setParameter("inventarioId", inventarioId)
					.getResultList();
		}else if(fechaInicio!=null && fechaFin==null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha >= :fechaInicio")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fechaInicio", date1)
					.getResultList();
		}else if(fechaInicio==null && fechaFin!=null){
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha <= :fechaFin")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fechaFin", date2)
					.getResultList();
		}else{
			System.out.println("ENTRO AL ULTIMO ELSE");
			items = entityManager.createQuery("select distinct(i) from Item i " +
					"where i.itemId.inventarioId = :inventarioId and i.movimiento.fecha between :fecha1 and :fecha2")
					.setParameter("inventarioId", inventarioId)
					.setParameter("fecha1", date1)
					.setParameter("fecha2", date2)
					.getResultList();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void listarMovimientos(){
		String condGnr = "";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.movimiento.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha>=:f1 AND x.movimiento.fecha<=:f2 ";//condGnr += " AND x.movimiento.fecha BETWEEN :f1 AND :f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		if(sucursalSeleccionada != null) {
			condGnr += " AND x.movimiento.sucursal = :suc ";
		} else
			condGnr += " AND (:suc = :suc OR 1 = 1) ";
		
		if(tipoMovimiento != null && tipoMovimiento!=" " && tipoMovimiento!="") {
			System.out.println("Movimiento vacio o nulo");
			condGnr += " AND x.movimiento.tipoMovimiento = '"+tipoMovimiento+"' ";
		} else
		{
			condGnr += " AND x.movimiento.tipoMovimiento !='x' "; //condGnr += " AND (:tipm = :tipm OR 1 = 1) ";
			//tipoMovimiento="x";
		}
		//!razon.trim().equals("")
		if(razon != null && razon!="" && razon!=" ") {
			System.out.println("razon llena  ****");
			condGnr += " AND x.movimiento.razon = '"+razon+"' ";
		} else
		{
			System.out.println("razon vacia");
			condGnr += " AND x.movimiento.razon != 'x' ";//condGnr += " AND (:razn = :razn OR 1 = 1) ";
			//razon="x";
		}
		
		if(categoriaSeleccionada != null && !categoriaSeleccionada.equals("") && !categoriaSeleccionada.equals(" ")) {
			condGnr += " AND x.inventario.producto.categoria.categoriaPadre = :cat ";
		} else
			condGnr += " AND (:cat = :cat OR 1 = 1) ";
		
		if(productoSeleccionado != null) {
			condGnr += " AND x.inventario.producto = :prd ";
		} else
			condGnr += " AND (:prd = :prd OR 1 = 1) ";
		
		condGnr+=" order by x.movimiento.fecha desc";
		
		
		String sQuery = "select x from Item x where 1 = 1 ";
		
		items = entityManager.createQuery(sQuery + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.setParameter("suc", sucursalSeleccionada)
				.setParameter("cat", categoriaSeleccionada)
				.setParameter("prd", productoSeleccionado)
				.getResultList();
		System.out.println("");
	}
	
	
	@SuppressWarnings("unchecked")
	public void listarMovimientosSuc(){
		
		
		String condGnr = "";
		
		if(fechaInicio != null && fechaFin == null) 
			condGnr += " AND x.movimiento.fecha >= :f1 AND :f2 IS NULL ";
		else if(fechaInicio == null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha <= :f2 AND :f1 IS NULL ";
		else if(fechaInicio != null && fechaFin != null)
			condGnr += " AND x.movimiento.fecha>=:f1 AND x.movimiento.fecha<=:f2 ";
		else
			condGnr = "  AND (:f1 = :f2 OR 1 = 1) ";
		
		/*if(sucursalSeleccionada != null) {
			condGnr += " AND x.movimiento.sucursal = :suc ";
		} else
			condGnr += " AND (:suc = :suc OR 1 = 1) ";*/
		
		if(tipoMovimiento != null && tipoMovimiento!=" " && tipoMovimiento!="") {
			System.out.println("Movimiento lleno");
			condGnr += " AND x.movimiento.tipoMovimiento = '"+tipoMovimiento+"' ";
		} else
		{
			condGnr += " AND x.movimiento.tipoMovimiento !='x' "; //condGnr += " AND (:tipm = :tipm OR 1 = 1) ";
			//tipoMovimiento="x";
		}
		//!razon.trim().equals("")
		if(razon != null && !razon.equals("") && !razon.equals(" ")) {
			System.out.println(razon);
			System.out.println("razon llena  ****");
			condGnr += " AND x.movimiento.razon = '"+razon+"' ";
		} else
		{
			System.out.println("razon vacia");
			condGnr += " AND x.movimiento.razon != 'x' ";//condGnr += " AND (:razn = :razn OR 1 = 1) ";
			//razon="x";
		}
		
		/*if(categoriaSeleccionada != null) {
			condGnr += " AND x.inventario.producto.categoria.categoriaPadre = :cat ";
		} else
			condGnr += " AND (:cat = :cat OR 1 = 1) ";
		
		if(productoSeleccionado != null) {
			condGnr += " AND x.inventario.producto = :prd ";
		} else
			condGnr += " AND (:prd = :prd OR 1 = 1) ";*/
		
		//condGnr+=" order by x.movimiento.fecha desc";
		condGnr+=" group by x.movimiento.sucursal.nombre,x.movimiento.tipoMovimiento,x.movimiento.razon order by x.movimiento.sucursal.nombre,x.movimiento.tipoMovimiento,x.movimiento.razon";
		
		//String sQuery = "select x from Item x where 1 = 1 ";
		
		String sQuery = "select x.movimiento.sucursal.nombre,SUM(x.cantidad),x.movimiento.tipoMovimiento,x.movimiento.razon from Item x where 1=1 ";
		//String sQuery = "select x.sucursal.nombre,SUM(x.cantidad),x.movimiento.tipoMovimiento,x.movimiento.razon from Movimiento x where 1=1 ";
		
		itemsObj = entityManager.createQuery(sQuery + condGnr)
				.setParameter("f1", fechaInicio)
				.setParameter("f2", fechaFin)
				.getResultList();
		System.out.println("");
	}
	
	public void vaciarVariablesListaMovimiento()
	{
		razon="";
		tipoMovimiento="";
	}
	
	public void cargarProductos(){
		if(categoriaSeleccionada!=null){
			System.out.print("Categoria :" + categoriaSeleccionada.getNombre());
			productos = new ArrayList<Producto>(categoriaSeleccionada.getProductos());
		}
		this.listarMovimientos();
	}
	
	@SuppressWarnings("unchecked")
	public void cargarSucursales(){
		if(empresaSeleccionada==null){
			if(loginUser.getUser().getSucursal()!=null){
				this.empresaSeleccionada=loginUser.getUser().getSucursal().getEmpresa();
				this.sucursalSeleccionada=loginUser.getUser().getSucursal();
			}
		}
		
		if(empresaSeleccionada!=null){
			sucursales=entityManager.createQuery("select s from Sucursal s where s.empresa = :empresa")
												.setParameter("empresa", empresaSeleccionada)
												.getResultList();
		}
		
	}
	
	public String obtenerTipoMovimiento(Item item){
		if(item.getMovimiento().getTipoMovimiento().equals("E")){
			return sainv_messages.get("report_movement_type_e");
		}else{
			return sainv_messages.get("report_movement_type_s");
		}
	}
	
	public String obtenerTipoMovimientoObj(String item){
		if(item.endsWith("E")){
			return sainv_messages.get("report_movement_type_e");
		}else{
			return sainv_messages.get("report_movement_type_s");
		}
	}
	
	public String obtenerTipoTransaccion(Item item){
		
		/*if(item.getMovimiento().getRazon().equals("O")){
			return sainv_messages.get("report_movement_reason_o");
		}else if(item.getMovimiento().getRazon().equals("T")){
			return sainv_messages.get("report_movement_reason_t");
		}else if(item.getMovimiento().getRazon().equals("P")){
			return sainv_messages.get("report_movement_reason_p");
		}else{
			return sainv_messages.get("report_movement_reason_c");
		}*/
		
		if(item.getMovimiento().getRazon().equals("T")){
			return sainv_messages.get("report_movement_reason_t");
		}else if(item.getMovimiento().getRazon().equals("P")){
			return sainv_messages.get("report_movement_reason_p");
		}else if(item.getMovimiento().getRazon().equals("C")){
			return sainv_messages.get("report_movement_reason_c");
		}
		else if(item.getMovimiento().getRazon().equals("V")){
			return sainv_messages.get("report_movement_reason_v");
		}
		else{
			return sainv_messages.get("report_movement_reason_o");
		}
	}
	
	public String obtenerTipoTransaccionObj(String item){
	
		if(item.equals("T")){
			return sainv_messages.get("report_movement_reason_t");
		}else if(item.equals("P")){
			return sainv_messages.get("report_movement_reason_p");
		}else if(item.equals("C")){
			return sainv_messages.get("report_movement_reason_c");
		}
		else if(item.equals("V")){
			return sainv_messages.get("report_movement_reason_v");
		}
		else{
			return sainv_messages.get("report_movement_reason_o");
		}
	}
	
	public Integer pdfColsNumber(){
		Integer cols=7;
		if(this.tipoMovimiento==null)
			cols+=1;
		if(loginUser.getUser().getSucursal()==null)
			cols+=1;
		return cols;
	}
	
	public List<Inventario> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<Inventario> inventarios) {
		this.inventarios = inventarios;
	}

	public Inventario getInventario() {
		return inventario;
	}

	public void setInventario(Inventario inventario) {
		this.inventario = inventario;
	}

	public Integer getInventarioId() {
		return inventarioId;
	}

	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}

	public List<Movimiento> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(List<Movimiento> movimientos) {
		this.movimientos = movimientos;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<ReparacionCliente> getReparacionesPend() {
		return reparacionesPend;
	}

	public void setReparacionesPend(List<ReparacionCliente> reparacionesPend) {
		this.reparacionesPend = reparacionesPend;
	}

	public List<ReparacionCliente> getEtapasRetr() {
		return etapasRetr;
	}

	public void setEtapasRetr(List<ReparacionCliente> etapasRetr) {
		this.etapasRetr = etapasRetr;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public Categoria getCategoriaSeleccionada() {
		return categoriaSeleccionada;
	}

	public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
		this.categoriaSeleccionada = categoriaSeleccionada;
	}

	public Producto getProductoSeleccionado() {
		return productoSeleccionado;
	}

	public void setProductoSeleccionado(Producto productoSeleccionado) {
		this.productoSeleccionado = productoSeleccionado;
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public Sucursal getSucursalSeleccionada() {
		return sucursalSeleccionada;
	}

	public void setSucursalSeleccionada(Sucursal sucursalSeleccionada) {
		this.sucursalSeleccionada = sucursalSeleccionada;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}
	
	public String getInvPDF(){
		return "/sainv/sainv/reportes/inventarios/invPDF.sa?docId=1";
	}
	
	public String getDetailPDF(){
		return "/sainv/sainv/reportes/inventarios/detailInvPDF.sa?docId=1";
	}
	
	public String getMovPDF(){
		return "/sainv/sainv/reportes/movimientos/movPDF.sa?docId=1";
	}
	
	public String getCurrRepsPDF(){
		return "/sainv/sainv/reportes/taller/currRepsPDF.sa?docId=1";
	}
	
	public String getretrEtasPDF(){
		return "/sainv/sainv/reportes/taller/retrEtasPDF.sa?docId=1";
	}
	
	public String getRepChequesPDF(){
		return "/sainv/sainv/reportes/crm/repChequesPDF.sa?docId=1";
	}


	public List<Object> getItemsObj() {
		return itemsObj;
	}


	public void setItemsObj(List<Object> itemsObj) {
		this.itemsObj = itemsObj;
	}


	public String getBusquedaNumSerie() {
		return busquedaNumSerie;
	}


	public void setBusquedaNumSerie(String busquedaNumSerie) {
		this.busquedaNumSerie = busquedaNumSerie;
	}


	public Object[] getInfoVenta() {
		return infoVenta;
	}


	public void setInfoVenta(Object[] infoVenta) {
		this.infoVenta = infoVenta;
	}


	public Object[] getInfoCoti() {
		return infoCoti;
	}


	public void setInfoCoti(Object[] infoCoti) {
		this.infoCoti = infoCoti;
	}


	public Object[] getInfoCompra() {
		return infoCompra;
	}


	public void setInfoCompra(Object[] infoCompra) {
		this.infoCompra = infoCompra;
	}	
	
	
	
	

}
