package com.sa.kubekit.action.medical;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.NoInitialContextException;
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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.Service;
import com.sa.model.workshop.ServicioReparacion;

@Name("serviceDAO")
@Scope(ScopeType.CONVERSATION)
public class ServiceDAO extends KubeDAO<Service> {

	private static final long serialVersionUID = 1L;

	private Integer serviceId;
	private List<Service> resultList = new ArrayList<Service>();
	private List<Service> resultListExa = new ArrayList<Service>();
	private String nomCoinci="";
	private String filterEstado;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("serviceDAO_deleted")));
	}
	
	public void loadServiciosList() {
		resultList = getEntityManager()
				.createQuery("select e from Service e order by e.estado ASC,e.codigo ASC ")//SELECT s FROM Service s ORDER BY s.codigo ASC
				.getResultList();
	}
	
	public void loadServiciosTallersList() {
		resultList = getEntityManager()
				.createQuery("select e from Service e where e.estado='ACT' order by e.estado ASC,e.codigo ASC ")//SELECT s FROM Service s ORDER BY s.codigo ASC
				.getResultList();
	}
	
	public void buscadorServicios(){
		
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE ((UPPER(s.name) LIKE UPPER(:nom) OR " +
						"UPPER(s.codigo) LIKE UPPER(:cod)) AND" +
						" (s.tipoServicio = 'CMB' OR s.tipoServicio = 'TLL')) order by s.codigo,s.name")
				.setParameter("cod","%"+nomCoinci.toUpperCase()+"%" )
				.setParameter("nom","%"+nomCoinci.toUpperCase()+"%")
				.getResultList();
		
	}
	
	public void buscadorServiciosGral(){
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE ((UPPER(s.name) LIKE UPPER(:nom) OR " +
						"UPPER(s.codigo) LIKE UPPER(:cod)))")
				.setParameter("cod","%"+nomCoinci+"%" )
				.setParameter("nom","%"+nomCoinci+"%")
				.getResultList();
		
	}
	
	public void loadServiciosList(String tipoServ) {
		nomCoinci="";
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps", tipoServ)
				.getResultList();
	}
	
	public void loadServiciosListByName(String tipoServ) {
			
			resultList = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' AND (UPPER(s.name) like :nom) ORDER BY s.codigo ASC")
					.setParameter("tps", tipoServ)
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
	
	
	//Esta lista debe haber al menos un registro para validar
	public void loadServiciosExa() {
		
		nomCoinci="";
		System.out.println("ENtro a buscar examen");
		
		resultListExa = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps", "EXA")
				.getResultList();
		
		System.out.println("Tam" + resultListExa.size());
	}
	
	public void loadServiciosExaByName() {
		
		System.out.println("Var coinci "+nomCoinci);
		
		resultListExa = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps AND s.estado = 'ACT' AND (UPPER(s.name) like :nom)  ORDER BY s.codigo ASC")
				.setParameter("tps", "EXA")
				.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
				.getResultList();
		
		System.out.println("TamDos" + resultListExa.size());
	}
	
	public List<Object[]> findServiciosByName(Object o){
		System.out.println("entré a a findServiciosByName y o.tostring es: "+ o.toString());  
		return getEntityManager()
			.createQuery("SELECT s.codigo, s.name,s FROM Service s WHERE (s.tipoServicio = :tps1 OR s.tipoServicio = :tps2)" +
					" AND s.estado = 'ACT' AND (UPPER(s.name) LIKE UPPER(:nom)) OR (UPPER(s.codigo) LIKE UPPER(:nom)) ORDER BY s.codigo ASC")
			.setParameter("tps1", "EXA")
			.setParameter("tps2", "MED")
			.setParameter("nom","%"+o.toString()+"%")
			.getResultList();
		
	}
	
	public void loadServiciosList(String tipoServ1, String tipoServ2) {
		resultList = getEntityManager()
				.createQuery("SELECT s FROM Service s WHERE s.tipoServicio = :tps1 OR s.tipoServicio = :tps2 AND s.estado = 'ACT' ORDER BY s.codigo ASC")
				.setParameter("tps1", tipoServ1)
				.setParameter("tps2", tipoServ2)
				.getResultList();
	}

	public void loadExa() {
		load();
		instance.setTipoServicio("EXA");
	}
	
	public void load() {
		try {
			setInstance(getEntityManager().find(Service.class, serviceId));
		} catch (Exception e) {
			clearInstance();
			instance = new Service();
		}
	}

	private boolean validar() {
		//Evaluamos el codigo del combo que no se repita
		List<Service> serviciosCoinci = new ArrayList<Service>();
		
		if(!isManaged()) { 
			serviciosCoinci = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.codigo = :cod AND s.estado='ACT'")
					.setParameter("cod", instance.getCodigo())
					.getResultList();
			
		} else {
			serviciosCoinci = getEntityManager()
					.createQuery("SELECT s FROM Service s WHERE s.codigo = :cod AND s.id <> :idSvc AND s.estado='ACT'")
					.setParameter("cod", instance.getCodigo())
					.setParameter("idSvc", instance.getId())
					.getResultList();
		}
		
		if(serviciosCoinci != null && serviciosCoinci.size() > 0) {
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("serviceDAO_codrep"));
			return false;
		}
		
		return true;
	}
	
	
	public void exportarExcel() throws IOException
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
				"attachment;filename=listaServicios-"
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
					celda.setCellValue("Codigo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(1);
					celda.setCellValue("Servicio");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Costo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Tipo");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Estado");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					loadServiciosList();
					
					for(Service serv: resultList)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//Codigo
						celda.setCellValue(serv.getCodigo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1);//Nombre
						celda.setCellValue(serv.getName());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2); //Costo
						celda.setCellValue((Double)serv.getCosto());
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(2);
						
						
						celda=fila.createCell(3);//Tipo servicio
						celda.setCellValue(serv.getTipoServicio());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(3);
						
						celda=fila.createCell(4);//Estado
						if(serv.getEstado().equals("ACT"))
							celda.setCellValue("Activo");
						else
							celda.setCellValue("Inactivo");
						
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(4);
						
						contFila++;
						
					}
					
					hoja.createFreezePane(3, 0);

					OutputStream os = response.getOutputStream();
					libro.write(os);
					os.close();
					
					
					FacesContext.getCurrentInstance().responseComplete();
	}
	
	
	@Override
	public void posDelete() {

	}

	@Override
	public void posModify() {

	}

	@Override
	public void posSave() {
	}
	
	public boolean desactivarServicio() {
		instance.setEstado("INA");
		return modify();
	}

	@Override
	public boolean preDelete() {
		/*if (!getInstance().getServiceClinicalHistories().isEmpty()
				|| !getInstance().getMedicalAppointmentServices().isEmpty()) {
			FacesMessages.instance().add(
					sainv_messages.get("serviceDAO_error1"));
			return false;
		}*/
		return true;
	}

	@Override
	public boolean preModify() {
		return validar();
		//return true;
	}

	@Override
	public boolean preSave() {
		instance.setEstado("ACT");
		return validar();
		//return true;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public List<Service> getResultList() {
		return resultList;
	}

	public void setResultList(List<Service> resultList) {
		this.resultList = resultList;
	}

	public List<Service> getResultListExa() {
		return resultListExa;
	}

	public void setResultListExa(List<Service> resultListExa) {
		this.resultListExa = resultListExa;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public String getFilterEstado() {
		return filterEstado;
	}

	public void setFilterEstado(String filterEstado) {
		this.filterEstado = filterEstado;
	}
	
	
}