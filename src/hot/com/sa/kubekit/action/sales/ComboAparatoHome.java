package com.sa.kubekit.action.sales;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
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

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Producto;
import com.sa.model.sales.AdaptacionCombo;
import com.sa.model.sales.ComboAparato;
import com.sa.model.sales.ComboAparatoAdaptacion;
import com.sa.model.sales.CostoServicio;
import com.sa.model.sales.CotCmbsItems;
import com.sa.model.sales.CotizacionComboApa;
import com.sa.model.sales.CotizacionComboItem;
import com.sa.model.sales.ItemComboApa;
import com.sa.model.sales.Service;

@Name("comboAparatoHome")
@Scope(ScopeType.CONVERSATION)
public class ComboAparatoHome extends KubeDAO<ComboAparato>{

	private static final long serialVersionUID = 1L;
	private Integer cmbApaId;	
	private List<ComboAparato> resultList = new ArrayList<ComboAparato>();
	private List<CostoServicio> costos = new ArrayList<CostoServicio>();
	private List<ItemComboApa> items = new ArrayList<ItemComboApa>();
	private Float totalCostos;
	private Float totalItems;
	private boolean tieneGarantia;
	private String nomCoinci="";
	private String filterEstado;
	private Float costoEstimado;
	
	private List<ComboAparatoAdaptacion> adaptaciones = new ArrayList<ComboAparatoAdaptacion>();
	private ComboAparatoAdaptacion comboAdaptacionSel = new ComboAparatoAdaptacion();

	/*
	@In(required=false,create=true)
	private CategoriaHome categoriaHome;
	
	@In(required=false,create=true)
	private ProductoHome productoHome;
		*/
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("combapa_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("combapa_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("combapa_deleted")));
	}
	
	@In
	private LoginUser loginUser;
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ComboAparato.class, cmbApaId));
			//Cargamos lista de items y costos
			items = instance.getItemsCombo();
			costos = instance.getCostosCombo();
			adaptaciones = instance.getAdaptaciones();
			
			if(instance.getPeriodoGarantia() != null && instance.getPeriodoGarantia() > 0)
				setTieneGarantia(true);
			else
				setTieneGarantia(false);
			
			
			costoEstimado=0f;
			
			calcularCostoEstimado();
			
		}catch (Exception e) { 
			clearInstance();
			setInstance(new ComboAparato());
		}
	}
	
	public void agregarAdaptacion(AdaptacionCombo adaptacion)
	{
		ComboAparatoAdaptacion comboAdaptacion = new ComboAparatoAdaptacion();
		comboAdaptacion.setAdaptacion(adaptacion);
		comboAdaptacion.setComboAparato(instance);
		
		adaptaciones.add(comboAdaptacion);
	}
	
	public void quitarAdaptacion(ComboAparatoAdaptacion adap)
	{
		if(adap.getId()!=null)
		{
			adaptaciones.remove(adap);
			getEntityManager().remove(adap);
		}
		else
		{
			adaptaciones.remove(adap);
		}
	}
	
		
	public void getCombosList() {
		setInstance(new ComboAparato());
		resultList = getEntityManager().createQuery("SELECT c FROM ComboAparato c ORDER BY c.codigo ASC ").getResultList();
	}
	
	public void cargarCombosActivos() {
		
		setInstance(new ComboAparato());
		resultList = getEntityManager().createQuery("SELECT c FROM ComboAparato c WHERE c.estado = 'ACT' ORDER BY c.codigo ASC ").getResultList();
		
	}
	
	//funcion que retorna resultados según contenido del buscador en tiempo real.
	public void buscadorCombos(){
		
		resultList=getEntityManager().createQuery("SELECT c FROM ComboAparato c " +
				"WHERE (UPPER(c.nombre) LIKE UPPER(:nom) OR UPPER(c.codigo) LIKE UPPER(:cod)) ORDER BY c.estado ASC,c.codigo ASC ")
				.setParameter("cod","%"+nomCoinci.toUpperCase()+"%" )
				.setParameter("nom","%"+nomCoinci.toUpperCase()+"%")
				.getResultList();
		
	}
	
	public void buscadorCombosAct(){
		
			
			resultList=getEntityManager().createQuery("SELECT c FROM ComboAparato c " +
					"WHERE (UPPER(c.nombre) LIKE UPPER(:nom) OR UPPER(c.codigo) LIKE UPPER(:cod)) and c.estado='ACT' ORDER BY c.codigo ASC ")
					.setParameter("cod","%"+nomCoinci.toUpperCase()+"%" )
					.setParameter("nom","%"+nomCoinci.toUpperCase()+"%")
					.getResultList();
			
			
			
		}
	
	public void actualizarPrecio(ItemComboApa selItm) {
		
		
	}
	
	public void setPrincipal(ItemComboApa selItm) {
		for(ItemComboApa tmpItm : items) 
			tmpItm.setPrincipal(false);
		selItm.setPrincipal(true);
	}
	
	
	public void setGeneraRequisicion(ItemComboApa item)
	{
		//for()
	}

	public void agregarCosto(Service pv) {
		boolean existe = false;
		if(!existe) {
			CostoServicio nwCst = new CostoServicio();
			nwCst.setServicio(pv);
			nwCst.setValor(pv.getCosto().floatValue());
			costos.add(nwCst);
		}
		
		calcularCostoEstimado();
	}
	
	public void agregarCategoria(Categoria ct) {
		boolean existe = false;
		for(ItemComboApa tmpItm : items)
			if(tmpItm.getCategoria() != null && tmpItm.getCategoria().equals(ct) && tmpItm.getProducto() == null) {
				existe = true; break;
			}
		
		if(!existe) {
			ItemComboApa nwItm = new ItemComboApa();
			nwItm.setCategoria(ct);
			nwItm.setCantidad(Short.valueOf("1"));
			items.add(nwItm);
		}
		
		calcularCostoEstimado();
	}
	
	public void agregarItem(Producto pr) {
		
		boolean existe = false;
		for(ItemComboApa tmpItm : items)
			if(tmpItm.getProducto() != null && tmpItm.getProducto().equals(pr)) {
				existe = true; break;
			}
				
		if(!existe) {
			ItemComboApa nwItm = new ItemComboApa();
			nwItm.setProducto(pr);
			nwItm.setCategoria(pr.getCategoria());
			nwItm.setCantidad(Short.valueOf("1"));
			if(items.size() <= 0)
				nwItm.setPrincipal(true);
			items.add(nwItm);
		}
		
		calcularCostoEstimado();
	}
	
	public void delItem(ItemComboApa itm) {
		
		if(itm.getId()!=null)
		{
			items.remove(itm);
			eliminiarItem(itm);
		}
		else
		{
			items.remove(itm);
		}
		
		
		calcularCostoEstimado();
	}
	
	public void eliminiarItem(ItemComboApa item)
	{
		List<CotCmbsItems> lsItemsCotizados = getEntityManager().createQuery("SELECT c FROM CotCmbsItems c where c.item.id=:idItem").setParameter("idItem", item.getId()).getResultList();
		if(lsItemsCotizados.size()>0)
		{
			for(CotCmbsItems itemCot: lsItemsCotizados)
			{
				getEntityManager().remove(itemCot);
			}
			
			System.out.println("Elimino los items cotizados");
		}
		
		
		getEntityManager().remove(item);
		System.out.println("Elimino los items");
		
	}
	
	public void delCosto(CostoServicio cst) {
		costos.remove(cst);
		calcularCostoEstimado();
	}
	
	@Override
	public boolean preSave() {
		if(!validar())
			return false;
		
		if(!isTieneGarantia())
			instance.setPeriodoGarantia(0);
			
		instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
		
		return true;
	}
	
	private boolean validar(){
		if(costos == null || costos.size() <= 0) {
		} else {
			for(CostoServicio tmpCst : costos) 
				if(tmpCst.getValor() == null || (tmpCst.getValor() <= 0 )) {
					FacesMessages.instance().add(
							sainv_messages.get("combapa_error_valowcst"));
					return false;
				}
		}
		boolean hayPrincipal = false;
		if(items == null || items.size() <= 0) {
			FacesMessages.instance().add(
					sainv_messages.get("combapa_error_noitm"));
			return false;
		} else {
			for(ItemComboApa tmpItm : items) {
				if(tmpItm.getCantidad() == null || tmpItm.getCantidad() <= 0) {
					FacesMessages.instance().add(
							sainv_messages.get("combapa_error_valowitm"));
					return false;
				}
				if(tmpItm.isPrincipal())
					hayPrincipal = true;
			}
			if(!hayPrincipal) {
				FacesMessages.instance().add(
						sainv_messages.get("combapa_error_noprpal"));
				return false;
			}
				
		}
		
		//Evaluamos el codigo del combo que no se repita
		List<ComboAparato> combosCoinci = new ArrayList<ComboAparato>();
		if(!isManaged()) { 
			combosCoinci = getEntityManager()
					.createQuery("SELECT c FROM ComboAparato c WHERE c.codigo = :cod AND c.estado='ACT' ")
					.setParameter("cod", instance.getCodigo())
					.getResultList();
			
		} else {
			combosCoinci = getEntityManager()
					.createQuery("SELECT c FROM ComboAparato c WHERE c.codigo = :cod AND c.id <> :idCmb AND c.estado='ACT'")
					.setParameter("cod", instance.getCodigo())
					.setParameter("idCmb", instance.getId())
					.getResultList();
		}
		
		if(combosCoinci != null && combosCoinci.size() > 0) {
			sainv_messages.clear();
			FacesMessages.instance().add(
					sainv_messages.get("combapa_error_codrep"));
			return false;
		}
		
		return true;
	}
	
	private boolean guardarDetalleCombo() {
		//Guardamos el detalle de costos e items
		getEntityManager().refresh(instance);
		
		for(CostoServicio tmpCst : costos) 
			if(!instance.getCostosCombo().contains(tmpCst)) {
				tmpCst.setCombo(instance);
				getEntityManager().persist(tmpCst);
			} else
				getEntityManager().merge(tmpCst);
			
		for(ItemComboApa tmpItm : items) 
			if(!instance.getItemsCombo().contains(tmpItm)) 
			{
				if(tmpItm.getProducto()==null)
					tmpItm.setSelCategoria("si");
				else
					tmpItm.setSelCategoria("no");
				
				tmpItm.setCombo(instance);
				getEntityManager().persist(tmpItm);
			} else
			{
				if(tmpItm.getProducto()==null)
					tmpItm.setSelCategoria("si");
				else
					tmpItm.setSelCategoria("no");
				getEntityManager().merge(tmpItm);
			}
		
		//Si estamos actualizando y no insertando, verificamos si eliminaron alguno
		if(instance.getId() != null && instance.getId() > 0) {
			for(CostoServicio tmpCst2 : instance.getCostosCombo()) 
				if(!costos.contains(tmpCst2)) 
					getEntityManager().remove(tmpCst2);
			
			for(ItemComboApa tmpItm2 : instance.getItemsCombo()) 
				if(!items.contains(tmpItm2)) 
					getEntityManager().remove(tmpItm2);
		}
		
		getEntityManager().flush();
		return true;
	}
	
	
	public void calcularCostoEstimado()
	{
		
		costoEstimado=0f;
		
		System.out.println("Costo inicial "+costoEstimado);
		
		if(costos.size()>0)
		{
			for(CostoServicio costoSer:costos)
			{
				costoEstimado+=costoSer.getValor();
			}
			
		}
		
		if(items.size()>0)
		{
			for(ItemComboApa itemCombo: items)
			{
					
				if(itemCombo.getCategoria()!=null && itemCombo.getProducto()==null)
				{
					
					Producto producto=(Producto)itemCombo.getCategoria().getProductos().toArray()[0];
					
					costoEstimado+=(producto.getPrcNormal()*itemCombo.getCantidad());
					
					System.out.println("Es categoria");
				}
				else
				{
					costoEstimado+=(itemCombo.getProducto().getPrcNormal()*itemCombo.getCantidad());
				}
			}
		}
		
		costoEstimado= super.moneyDecimal(costoEstimado).floatValue();
		
		System.out.println("Costo final "+costoEstimado);
	}
	
	
	public Float calcularCostoEstimadoPorCombo(ComboAparato combo)
	{
		
		Float costoEstimadoCombo=0f;
		
		System.out.println("Costo inicial "+costoEstimadoCombo);
		
		if(combo.getCostosCombo().size()>0)
		{
			for(CostoServicio costoSer:combo.getCostosCombo())
			{
				costoEstimadoCombo+=costoSer.getValor();
			}
			
		}
		
		if(combo.getItemsCombo().size()>0)
		{
			for(ItemComboApa itemCombo: combo.getItemsCombo())
			{
					
				if(itemCombo.getCategoria()!=null && itemCombo.getProducto()==null)
				{
					
					System.out.println("NOMBRE del combo "+itemCombo.getDescripcion());
					System.out.println("NOMBRE la categoria  "+itemCombo.getCategoria().getNombre());
					
					if(itemCombo.getCategoria().getProductos().size()>0)
					{
						Producto producto=(Producto)itemCombo.getCategoria().getProductos().toArray()[0];
						
						costoEstimadoCombo+=(producto.getPrcNormal()*itemCombo.getCantidad());
					}
					
					System.out.println("Es categoria");
				}
				else
				{
					costoEstimadoCombo+=(itemCombo.getProducto().getPrcNormal()*itemCombo.getCantidad());
				}
			}
		}
		
		costoEstimadoCombo = super.moneyDecimal(costoEstimadoCombo).floatValue();
		
		System.out.println("Costo final "+costoEstimadoCombo);
		
		
		
		return costoEstimadoCombo;
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
				"attachment;filename=listaCombos-"
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
					celda.setCellValue("Nombre");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(2);
					celda.setCellValue("Descripcion");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(3);
					celda.setCellValue("Precio estimado");
					celda.setCellStyle(stTitles);
					
					celda = fila.createCell(4);
					celda.setCellValue("Estado");
					celda.setCellStyle(stTitles);
					
					
					
					//celda = fila.createCell(7);
					
					//						0    1              2      3    4                5                                  6
					/*String jpql="SELECT c.id,c.fechaIngreso,c.estado,c,c.cliente.nombres,c.usuarioGenera.nombreCompleto,SUM(cotCmbsItm.precioCotizado) FROM " +
							" CotCmbsItems cotCmbsItm,CotizacionCombos cotcm,CotizacionComboApa c where cotCmbsItm.ctCmbs=cotcm AND cotcm.cotizacion=c ";*/
					
					int contFila=2;//,contCelda=0;
					
					
					for(ComboAparato combo: resultList)
					{
						fila = hoja.createRow(contFila);
						
						celda=fila.createCell(0);//Codigo
						celda.setCellValue(combo.getCodigo());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(0);
						
						celda=fila.createCell(1);//Nombre
						celda.setCellValue(combo.getNombre());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(1);
						
						celda=fila.createCell(2);//Descripcion
						celda.setCellValue(combo.getDescripcion());
						celda.setCellStyle(stList);
						hoja.autoSizeColumn(2);
						
						celda=fila.createCell(3); //Costo
						celda.setCellValue(calcularCostoEstimadoPorCombo(combo));
						celda.setCellStyle(stFinal);
						hoja.autoSizeColumn(3);
						
						
						
						
						celda=fila.createCell(4);//Estado
						if(combo.getEstado().equals("ACT"))
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
	public boolean preModify() {
		if(!validar())
			return false;
		
		if(!isTieneGarantia())
			instance.setPeriodoGarantia(0);
		
		return true;
	}

	@Override
	public boolean preDelete() {
		//Borramos las cotizaciones a las que esta amarrado un combo y a sus detalles
		if(instance.getItemsCombo() != null)
		for(ItemComboApa itm : instance.getItemsCombo()) 
			getEntityManager().remove(itm);
		if(instance.getCostosCombo() != null)
		for(CostoServicio cst : instance.getCostosCombo()) 
			getEntityManager().remove(cst);
		
		List<CotizacionComboApa> lstCots = getEntityManager()
				.createQuery("SELECT c FROM CotizacionComboApa c WHERE c.combo = :cmb")
				.setParameter("cmb", instance)
				.getResultList();
		
		if(lstCots != null)
		for(CotizacionComboApa tmpCot : lstCots) {
			for(CotizacionComboItem tmpItmCot : tmpCot.getItemsCotizacion())
				getEntityManager().remove(tmpItmCot);
			if(tmpCot.getHijoBin() != null && tmpCot.getHijoBin().size() > 0)
				getEntityManager().remove(tmpCot.getHijoBin().get(0));
			getEntityManager().remove(tmpCot);
		}
		getEntityManager().refresh(instance);
		
		return true;
	}
	
	public void guardarAdaptaciones()
	{
		if(adaptaciones.size()>0)
		{
			for(ComboAparatoAdaptacion cmbAdaptacion: adaptaciones)
			{
				if(cmbAdaptacion.getId()==null)
				{
					getEntityManager().persist(cmbAdaptacion);
				}
			}
		}
	}
	
	public void verContenidoAdaptacion(ComboAparatoAdaptacion adap)
	{
		comboAdaptacionSel = adap;
	}

	@Override
	public void posSave() {
		guardarDetalleCombo();
		guardarAdaptaciones();
		
	}

	@Override
	public void posModify() {
		guardarDetalleCombo();
		guardarAdaptaciones();
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Integer getCmbApaId() {
		return cmbApaId;
	}

	public void setCmbApaId(Integer cmbApaId) {
		this.cmbApaId = cmbApaId;
	}

	public List<ComboAparato> getResultList() {
		return resultList;
	}

	public void setResultList(List<ComboAparato> resultList) {
		this.resultList = resultList;
	}

	public List<CostoServicio> getCostos() {
		return costos;
	}

	public void setCostos(List<CostoServicio> costos) {
		this.costos = costos;
	}

	public List<ItemComboApa> getItems() {
		return items;
	}

	public void setItems(List<ItemComboApa> items) {
		this.items = items;
	}

	public Float getTotalCostos() {
		return totalCostos;
	}

	public void setTotalCostos(Float totalCostos) {
		this.totalCostos = totalCostos;
	}

	public Float getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Float totalItems) {
		this.totalItems = totalItems;
	}

	public boolean isTieneGarantia() {
		return tieneGarantia;
	}

	public void setTieneGarantia(boolean tieneGarantia) {
		this.tieneGarantia = tieneGarantia;
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



	public Float getCostoEstimado() {
		return costoEstimado;
	}



	public void setCostoEstimado(Float costoEstimado) {
		this.costoEstimado = costoEstimado;
	}



	public List<ComboAparatoAdaptacion> getAdaptaciones() {
		return adaptaciones;
	}

	public void setAdaptaciones(List<ComboAparatoAdaptacion> adaptaciones) {
		this.adaptaciones = adaptaciones;
	}

	public ComboAparatoAdaptacion getComboAdaptacionSel() {
		return comboAdaptacionSel;
	}

	public void setComboAdaptacionSel(ComboAparatoAdaptacion comboAdaptacionSel) {
		this.comboAdaptacionSel = comboAdaptacionSel;
	}
	
	
	
	
	
}
