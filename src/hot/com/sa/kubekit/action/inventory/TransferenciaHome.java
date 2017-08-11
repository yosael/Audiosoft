package com.sa.kubekit.action.inventory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.Controller;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.kubekit.action.util.UtilDate;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.Transferencia;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;

@Name("transferenciaHome")
@Scope(ScopeType.CONVERSATION)
public class TransferenciaHome extends KubeDAO<Transferencia>{

	private static final long serialVersionUID = 1L;
	private Integer transferenciaId;
	private Empresa empresaSeleccionada;
	private List<Transferencia> resultList = new ArrayList<Transferencia>();
	private List<Item> itemsAgregados = new ArrayList<Item>();
	private List<Inventario> productosAgregados = new ArrayList<Inventario>();
	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private Map<Integer, Integer> mapaItems = new HashMap<Integer, Integer>();
	private List<CodProducto> currCodigos = new ArrayList<CodProducto>();
	private Item selectedItem = new Item();
	private String nomCoinci="";
	private String estadoFilter;
	
	private List<Transferencia> resultListGen;
	
	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	private Integer codigosRestantes = new Integer(0);
	private Map<String, ArrayList<CodProducto>> lstCodsProductos = new HashMap<String, ArrayList<CodProducto>>();
	
	@In
	private LoginUser loginUser;
	
	@In(required = true)
	protected KubeBundle sainv_view_messages;
	
	@In(required=false,create=true)
	@Out(required=false)
	private ProductoHome productoHome;
	
	@In(required=false,create=true)
	private ItemHome itemHome;
	
	@In(required=false,create=true)
	private MovimientoHome movimientoHome;
	
	@In(required=false,create=true)
	private TransferenciasSolicitadasList transferenciasSolicitadasList;
	
	@In
	@Out
	private Controller controller;
	private List<String> archivos = new ArrayList<String>();
	private String nombreArchivo;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("transferenciaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("transferenciaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("transferenciaHome_deleted")));
	}
	
	@SuppressWarnings("unchecked")
	public void load()
	{
		
		try{
			selectedItem = null;
			setInstance(getEntityManager().find(Transferencia.class, transferenciaId));
			itemsAgregados = new ArrayList<Item>(instance.getItems());
			productosAgregados = new ArrayList<Inventario>();
			empresaSeleccionada=instance.getSucursal().getEmpresa();
			this.cargarSucursales();
			for(Item item : itemsAgregados){
				mapaItems.put(item.getItemId().getInventarioId(), item.getCantidad());
				productosAgregados.add(item.getInventario());
			}
			
		}catch (Exception e) {
			clearInstance();
			setInstance(new Transferencia());
			if(loginUser.getUser()!=null){
				System.out.println("EL USUARIO ES DIFERENTE DE NULL");
				instance.setUsuarioGenera(loginUser.getUser());
				if(loginUser.getUser().getSucursal()!=null){
					System.out.println("LA SUCURSAL DEL USUARIO ES DIFERENTE DE NULL");
					instance.setSucursalDestino(loginUser.getUser().getSucursal());
				}
			}
		}
		if(loginUser.getUser().getSucursal()!=null){
			sucursales = getEntityManager().createQuery("select e from Sucursal e where e.estado = 'ACT' and " +
					"e.empresa= :empresa order by e.nombre")
					.setParameter("empresa",  loginUser.getUser().getSucursal().getEmpresa())
					.getResultList();
		}
		
		
		if(instance.getSucursalDestino()!=null){
			sucursales.remove(getEntityManager().merge(instance.getSucursalDestino()));
		}
	}
	
	
	//@SuppressWarnings("unchecked")
	public void getTransferenciasGeneradas()
	{
		
		resultListGen= new ArrayList<Transferencia>();
		resultListGen= (List<Transferencia>)getEntityManager().createQuery("select t from Transferencia t where " +
					"t.usuarioGenera.id = " + loginUser.getUser().getId() + 
					"AND t.estado != 'A' order by t.fecha desc").getResultList();
		
		System.out.println("**** transferencia generada");
		System.out.println("tmanan "+ resultListGen.size());
		
		
	}
	
	@SuppressWarnings("unchecked")
	public void getTransferenciasList() {
		if(loginUser.getUser().getSucursal() != null) {
			List<Sucursal> subSucFlt = getEntityManager()
					.createQuery("SELECT s FROM Sucursal s WHERE s.sucursalSuperior = :suc ")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.getResultList();
			if(subSucFlt == null || subSucFlt.size() <= 0) {
				resultList = getEntityManager()
						.createQuery("select t from Transferencia t where t.estado = 'G' AND " +
								" (t.sucursalDestino = :suc ) " +
								"	order by t.fecha desc, t.estado ")
						.setParameter("suc", loginUser.getUser().getSucursal())
						.getResultList();
			} else {
				resultList = getEntityManager()
						.createQuery("select t from Transferencia t where t.estado = 'G' AND " +
								" (t.sucursalDestino = :suc OR t.sucursalDestino IN (:subSuc) ) " +
								"	order by t.fecha desc, t.estado ")
						.setParameter("suc", loginUser.getUser().getSucursal())
						.setParameter("subSuc", subSucFlt)
						.getResultList();
			}
			
		} else {
			resultList = getEntityManager()
					.createQuery("select t from Transferencia t where t.estado = 'G' " +
							"	order by t.fecha desc, t.estado ")
					.getResultList();
		}
		
	}
		
	@SuppressWarnings("unchecked")
	public void cargarSucursales(){
		sucursales = getEntityManager().createQuery("select e from Sucursal e where e.estado = 'ACT' and " +
				"e.empresa= :empresa order by e.nombre")
				.setParameter("empresa",  empresaSeleccionada)
				.getResultList();
	}
	
	public void asignarSucursal(){
		this.productoHome.setSucursalSeleccionada(instance.getSucursal());
	}
	
	public void agregarProducto(Inventario inventario){
		if (inventario.getCantidadActual()<=0){
			FacesMessages.instance().add(Severity.WARN,sainv_messages.get("transferenciaHome_error_outofstock"));
			return;
		}
		if(productosAgregados.contains(inventario)){
			FacesMessages.instance().add(Severity.WARN,
				sainv_messages.get("transferenciaHome_error_additem"));
			return;
		}
		Inventario inventarioOrigen = null;
		try{
			inventarioOrigen = (Inventario) getEntityManager().createQuery("select i from Inventario i where " +
					"i.sucursal = :sucursal and " +
					"i.producto = :producto")
					.setParameter("sucursal", instance.getSucursal())
					.setParameter("producto", inventario.getProducto())
					.getSingleResult();
		}catch (Exception e) {
		}
		
		Item item = new Item();
		item.setCantidad(1);
		item.setCostoUnitario(inventario.getProducto().getPrcNormal());
		item.setInventario(inventarioOrigen);
		
		item.setItemId(new ItemId());
		item.getItemId().setInventarioId(inventarioOrigen.getId());
		itemsAgregados.add(item);
		productosAgregados.add(inventarioOrigen);
	}
	
	public void removerItem(Item item){
		itemsAgregados.remove(item);
		productosAgregados.remove(item.getInventario());
	}
	
	public boolean approveRequest(){
		instance.setEstado("P");
		this.modify();
		return true;
	}
	
	public boolean rejectRequest(){
		instance.setEstado("D");
		this.modify();
		return true;
	}
	
	public boolean approve(){
		for(Item item : itemsAgregados){
			if(item.getInventario().getCantidadActual()-item.getCantidad()<0){
				itemHome.setInstance(item);
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("transferenciaHome_error_save3"));
				return false;
			}
			//Verificamos si este item utiliza codigos de serie o lote y validamos
			if (item.getInventario().getProducto().getCategoria().isTieneNumSerie()
					|| item.getInventario().getProducto().getCategoria().isTieneNumLote()) {
				System.out.println("Utiliza numeros de seria o lote****");
				ArrayList<CodProducto> codsProds = lstCodsProductos.get(item.getInventario().getProducto().getReferencia());
				int cntChecked = 0;
				if (codsProds == null || codsProds.size() <= 0) {
					FacesMessages.instance().add(Severity.WARN,
							sainv_messages.get("transferencia_err_faltacods"));
					return false;
				}

				item.setCodsSerie("");
				int i = 0;
				for (CodProducto tmpCod : codsProds) {
					i++;
					System.out.println("codigo producto"+tmpCod.getNumLote());
					cntChecked += tmpCod.isTransferido() ? 1 : 0;
					if (tmpCod.isTransferido() && tmpCod.getNumSerie() != null
							&& !tmpCod.getNumSerie().trim().equals("")) {
						tmpCod.setEnTransferencia(true);
						item.setCodsSerie(item.getCodsSerie().concat(
								tmpCod.getNumSerie() + ","));
					}
					
					if(tmpCod.isTransferido() && tmpCod.getNumLote()!=null)
					{
						System.out.println("Entro a nuevo if de numLote **********");
						tmpCod.setEnTransferencia(true);
					}
					
				}

				if (cntChecked < item.getCantidad()) {
					FacesMessages.instance().add(Severity.WARN,
							sainv_messages.get("transferencia_err_faltacods"));
					return false;
				}
			}
		}		
		instance.setItems(itemsAgregados);
		instance.setUsuario(loginUser.getUser());
		instance.setFechaRecibo(new Date());
		instance.setEstado("S");
		FacesMessages.instance().clear();
		System.out.println("Se aprobo y se envio el producto solicitado****");
		return this.modify();
		
	}
	
	public boolean productosRecibidos() {
		for(Item item : itemsAgregados){
			if(item.getInventario().getCantidadActual()-item.getCantidad()<0){
				itemHome.setInstance(item);
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("transferenciaHome_error_save3"));
				return false;
			}
			
		}
		this.createInComing();
		this.createOutComing();
		instance.setFechaRecibo(new Date());
		instance.setEstado("A");
		instance.setEntrada(movimientoHome.getInstance());
		FacesMessages.instance().clear();
		return this.modify();
	}
	
	private void createInComing() {
		/*System.out.println("ESTE ES EL USUARIO QUE GENERO LA TRANSFERENCIA: " + instance.getUsuarioGenera().getNombreUsuario());
		System.out.println("ESTA ES LA SUCURSAL DESTINO: " + instance.getSucursalDestino().getNombre());
		for(Item item : instance.getItems()){
			System.out.println("ESTE ES EL PRODUCTO DEL ITEM: " + item.getInventario().getProducto().getNombre());
		}
		System.out.println("--------------------------------------------------------------------------------");
		List<Inventario> inventarios = getEntityManager().createQuery("select i from Inventario i where " +
				"i.sucursal = :sucursal")
				.setParameter("sucursal", instance.getSucursalDestino())
				.getResultList();
		System.out.println("EL NUMERO DE INVENTARIOS DE LA SUCURSAL ES: " + inventarios.size());
		for(Inventario i : inventarios){
			System.out.println("PRODUCTO: " + i.getProducto().getNombre());
		}*/
		Movimiento movimiento = new Movimiento();
		movimiento.setFecha(new Date());
		movimiento.setObservacion(instance.getObservacionDestino());
		movimiento.setTipoMovimiento("E");
		movimiento.setRazon("T");
		movimiento.setSucursal(instance.getSucursalDestino());
		movimiento.setUsuario(instance.getUsuarioGenera());
		
		//nuevo 25/11/2016
		/*if(instance.getSucursal().getSucursalSuperior()!=null)
			movimiento.setDesde(instance.getSucursal().getSucursalSuperior().getNombre());
		else*/
			movimiento.setDesde(instance.getSucursal().getNombre());
		
		
		
		movimiento.setHacia(instance.getSucursalDestino().getNombre());
		
		
		
		
		movimientoHome.setInstance(movimiento);
		movimientoHome.save();
		for(Item item : instance.getItems()){
			Item item2 = item.clone();
			System.out.println("1 El codigo de serie es:"+ item.getCodsSerie() );
			System.out.println("2 El codigo de serie es:"+ item.getCodsSerie() );
			Inventario inventario = null; 
			try{
				inventario = (Inventario) getEntityManager().createQuery("select i from Inventario i where " +
						"i.sucursal = :sucursal and " +
						"i.producto = :producto")
						.setParameter("sucursal", instance.getSucursalDestino())
						.setParameter("producto", item.getInventario().getProducto())
						.getSingleResult();
			}catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("EL INVENTARIO ES: " + inventario.getId());
			item2.setItemId(new ItemId());
			item2.getItemId().setInventarioId(inventario.getId());
			item2.getItemId().setMovimientoId(movimientoHome.getInstance().getId());
			item2.setInventario(inventario);
			item2.setMovimiento(movimientoHome.getInstance());
			System.out.println("INVENTARIO QUE TIENE ITEM2 "+ item2.getInventario().getId());
			System.out.println("EL PRODUCTO que tiene ITEM2 "+ item2.getInventario().getProducto().getNombre());
			System.out.println("LA REFERENCIA que tiene ITEM2 "+ item2.getInventario().getProducto().getReferencia());
			itemHome.setInstance(item2);
			itemHome.modificarCantidadInventario();
			itemHome.save();
			
			//Pasamos el codigo a la sucursal que solicita
			cargarListaCodigos(item);
			System.out.println("\n Tamaño del mapa:" + lstCodsProductos.size());
			for (String s: lstCodsProductos.keySet()){
				System.out.println("     Key: " + s);
			}
			if(lstCodsProductos.get(item2.getInventario().getProducto().getReferencia()) != null) {
				System.out.println("item2 referencia diff de NULL");
				ArrayList<CodProducto> codsProds = lstCodsProductos.get(item2.getInventario().getProducto().getReferencia());
				for (CodProducto tmpCod : codsProds) {
					if (tmpCod.getEnTransferencia() != null
							&& tmpCod.getEnTransferencia() == true) {
						System.out.println("transfiriendocodigo a nuevo inventario****");
						tmpCod.setInventario(inventario);
						tmpCod.setMovimiento(movimientoHome.getInstance());
						tmpCod.setEnTransferencia(false);
						getEntityManager().merge(tmpCod);
					}
				}
			}
		}
		
	}

	private void createOutComing() {
		instance.setDesde(instance.getSucursal().getNombre());
		instance.setHacia(instance.getSucursalDestino().getNombre());
		
		for(Item item : instance.getItems()){
			itemHome.setInstance(item);
			itemHome.modificarCantidadInventario();
		}
		this.modify();
		
	}

	public boolean reject(){
		instance.setEstado("D");
		System.out.println("Se declino el envio ... no se envio**");
		this.modify();
		return true;
	}
	
	public void cargarListaCodigos(Item prdItm) {
		
		selectedItem = prdItm;
		codigosRestantes = 0;
		ArrayList<CodProducto> codsProds = null;
		//Si aun no se ha cargado la lista para ese item, la cargamos
		if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia()) == null) {
			System.out.println("ah! Referencia == NULL ");
			System.out.println("Selected item codsSerie "+prdItm.getCodsSerie());
			System.out.println("selected item CodProducto "+prdItm.getCodProducto()) ;
			codsProds = (ArrayList<CodProducto>)getEntityManager().createQuery("SELECT c FROM CodProducto c " +
				"	WHERE c.inventario = :inv AND c.estado = 'ACT' ")
				.setParameter("inv", prdItm.getInventario())
				.getResultList();
		} else {
			System.out.println("Referencia != Null OH!");
			codsProds = lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia());
			//contamos cuantos habian dejado chequeados
			for(CodProducto tmpCod : codsProds)
				if(tmpCod.isTransferido())
					codigosRestantes++;
		}
		
		lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
		currCodigos = codsProds;
	}
	
	//Para limitar los items que seleccionan
	public void limitSelectedCodes(CodProducto chkCod) {
		if(chkCod.isTransferido()) {
			ArrayList<CodProducto> codsProds = lstCodsProductos.get(selectedItem.getInventario().getProducto().getReferencia());
			int cntChecked = 0;
			for(CodProducto tmpCod : codsProds) 
				cntChecked+=tmpCod.isTransferido()?1:0;
			
			if(cntChecked > selectedItem.getCantidad())
				chkCod.setTransferido(false);
			else
				codigosRestantes++;
		} else
			codigosRestantes--;
	}

	@Override
	public boolean preSave() {
		if(instance.getSucursal().equals(instance.getSucursalDestino())){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("transferenciaHome_error_save0"));
			return false;
		}
		if(itemsAgregados.isEmpty()){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("transferenciaHome_error_save2"));
			return false;
		}
		
		instance.setFecha(new Date());
		
		return true;
	}

	@Override
	public boolean preModify() {
		if(itemsAgregados.isEmpty()){
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("transferenciaHome_error_save2"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		for(Item item : instance.getItems()){
			itemHome.setInstance(item);
			itemHome.retornarItems(itemHome.getInstance().getCantidad());
			itemHome.delete();
		}
		FacesMessages.instance().clear();
		return true;
	}

	@Override
	public void posSave() { 
		for(Item item :itemsAgregados){
			item.getItemId().setMovimientoId(instance.getId());
			item.setMovimiento(instance);
			itemHome.setInstance(item);
			itemHome.save();
		}
		getEntityManager().refresh(instance);
		getEntityManager().flush();
	}

	@Override
	public void posModify() {
		List<Item> itemsRemover = new ArrayList<Item>(instance.getItems());
		itemsRemover.removeAll(itemsAgregados);
		
		for(Item item : itemsRemover){
			itemHome.setInstance(item);
			itemHome.delete();
		}
		
		for(Item item : itemsAgregados){
			item.getItemId().setMovimientoId(instance.getId());
			item.setMovimiento(instance);
			itemHome.setInstance(item);
			if(itemHome.isManaged()){
				itemHome.modify();
			}else{
				itemHome.save();
			}
		}
		
		
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		
		
		if(instance.getEstado().equals("A"))
			FacesMessages.instance().add(Severity.INFO,
					sainv_messages.get("transferenciaHome_msg_apli"));
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean generarReporteTransferenciasPendientesEnviar(){
		
		UtilDate util = new UtilDate();
		String fechaReporte = util.convertDateTime(new Date()).replace(":", "_");
		String archivo = "report_pending_send_transfer"+fechaReporte.replace(" ", "_")+".xls";
		
		Workbook wbs = new HSSFWorkbook();
		CreationHelper createHelper = wbs.getCreationHelper();
		
		Font font = wbs.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		Font font2 = wbs.createFont();
		font2.setFontHeightInPoints((short) 12);
		font2.setColor(IndexedColors.BLACK.getIndex());
		font2.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		
		CellStyle cs = wbs.createCellStyle();
		cs.setFont(font);
		cs.setAlignment(CellStyle.ALIGN_LEFT);
		
		cs.setBorderBottom(CellStyle.BORDER_THICK);
		cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderLeft(CellStyle.BORDER_THICK);
		cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderRight(CellStyle.BORDER_THICK);
		cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderTop(CellStyle.BORDER_THICK);
		cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		CellStyle cs2 = wbs.createCellStyle();
		cs2.setFont(font2);
		cs2.setAlignment(CellStyle.ALIGN_LEFT);
		cs2.setBorderBottom(CellStyle.BORDER_THICK);
		cs2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderLeft(CellStyle.BORDER_THICK);
		cs2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderRight(CellStyle.BORDER_THICK);
		cs2.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderTop(CellStyle.BORDER_THICK);
		cs2.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		Sheet sheet = wbs.createSheet();
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 4000);
		
		int rowNum = 0;
		int colNum = 0;
		
		getTransferenciasList();
		
		for(Transferencia transfer : resultList){
			
			Row row = sheet.createRow(rowNum);
			Cell cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_send_list_col1")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getSucursalDestino().getNombre());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_send_list_col2")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getUsuarioGenera().getNombreUsuario());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_send_list_col3")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getItems().size());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_send_list_col4")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(util.convertDate(transfer.getFecha()));
			
			rowNum+=2;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list1")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list2")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list3")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list4")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list5")));
			
			rowNum++;
			colNum=0;
			
			for(Item item : transfer.getItems()){
				row = sheet.createRow(rowNum);
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getNombre());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getCantidad());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getCostoUnitario());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getUnidadMedida().getNombre());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getCategoria().getNombre());
				
				rowNum++;
				colNum=0;
			}
			rowNum+=2;
		}
		
		javax.faces.context.FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		HttpSession session = request.getSession();
		String url = request.getRequestURL().toString();
		
		int index = url.lastIndexOf("/");
		String ruta = url.substring(0, index+1)+"downloads"+System.getProperty("file.separator")+archivo;
		this.setNombreArchivo(ruta);
		
		String filename = session.getServletContext().getRealPath("/")
			+ "sainv" + System.getProperty("file.separator") + "transferSend" + System.getProperty("file.separator") 
			+ "downloads" + System.getProperty("file.separator") + archivo;
		this.archivos.add(filename);
		controller.agregarArchivoTemp(filename);
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			wbs.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean generarReporteTransferenciasPendientesSolicitar(){
		
		UtilDate util = new UtilDate();
		String fechaReporte = util.convertDateTime(new Date()).replace(":", "_");
		String archivo = "report_pending_request_transfer"+fechaReporte.replace(" ", "_")+".xls";
		
		Workbook wbs = new HSSFWorkbook();
		CreationHelper createHelper = wbs.getCreationHelper();
		
		Font font = wbs.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setColor(IndexedColors.BLACK.getIndex());
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		Font font2 = wbs.createFont();
		font2.setFontHeightInPoints((short) 12);
		font2.setColor(IndexedColors.BLACK.getIndex());
		font2.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		
		CellStyle cs = wbs.createCellStyle();
		cs.setFont(font);
		cs.setAlignment(CellStyle.ALIGN_LEFT);
		
		cs.setBorderBottom(CellStyle.BORDER_THICK);
		cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderLeft(CellStyle.BORDER_THICK);
		cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderRight(CellStyle.BORDER_THICK);
		cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cs.setBorderTop(CellStyle.BORDER_THICK);
		cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		CellStyle cs2 = wbs.createCellStyle();
		cs2.setFont(font2);
		cs2.setAlignment(CellStyle.ALIGN_LEFT);
		cs2.setBorderBottom(CellStyle.BORDER_THICK);
		cs2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderLeft(CellStyle.BORDER_THICK);
		cs2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderRight(CellStyle.BORDER_THICK);
		cs2.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cs2.setBorderTop(CellStyle.BORDER_THICK);
		cs2.setTopBorderColor(IndexedColors.BLACK.getIndex());
		
		Sheet sheet = wbs.createSheet();
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 4000);
		
		int rowNum = 0;
		int colNum = 0;
		
		for(Transferencia transfer : this.transferenciasSolicitadasList.getResultList()){
			
			Row row = sheet.createRow(rowNum);
			Cell cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_pending_list_col1")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getSucursal().getNombre());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_pending_list_col2")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getUsuarioGenera().getNombreUsuario());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_pending_list_col3")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(transfer.getItems().size());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transfer_pending_list_col4")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(util.convertDate(transfer.getFecha()));
			
			rowNum+=2;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list1")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list2")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list3")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list4")));
			
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("transferHome_report_pending_send_list5")));
			
			rowNum++;
			colNum=0;
			
			for(Item item : transfer.getItems()){
				row = sheet.createRow(rowNum);
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getNombre());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getCantidad());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getCostoUnitario());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getUnidadMedida().getNombre());
				
				cell = row.createCell(colNum++);
				cell.setCellStyle(cs2);
				cell.setCellValue(item.getInventario().getProducto().getCategoria().getNombre());
				
				rowNum++;
				colNum=0;
			}
			rowNum+=2;
		}
		
		javax.faces.context.FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		HttpSession session = request.getSession();
		String url = request.getRequestURL().toString();
		
		int index = url.lastIndexOf("/");
		String ruta = url.substring(0, index+1)+"downloads"+System.getProperty("file.separator")+archivo;
		this.setNombreArchivo(ruta);
		
		
		String filename = session.getServletContext().getRealPath("/")
			+ "sainv" + System.getProperty("file.separator") + "transferRequest" + System.getProperty("file.separator") 
			+ "downloads" + System.getProperty("file.separator") + archivo;
		this.archivos.add(filename);
		controller.agregarArchivoTemp(filename);
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			wbs.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List<Item> obtenerItemsTransferencia(Transferencia transfer){
		return new ArrayList<Item>(transfer.getItems());
	}
	
	@Destroy
	public void eliminarArchivo() {
		java.io.File f;
		
		for(String a : this.archivos){
			f = new java.io.File(a);
			if(f.exists()){
				f.delete();
				controller.eliminarArchivoTemp(a);
			}
		}
		
	}

	public Integer getTransferenciaId() {
		return transferenciaId;
	}

	public void setTransferenciaId(Integer transferenciaId) {
		this.transferenciaId = transferenciaId;
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public List<Item> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<Item> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	
	public String getTransferPendingSendPDF(){
		return "/sainv/sainv/transferSend/transferPendingSendPDF.sa?docId=1";
	}
	
	public String getTransferPendingRequestPDF(){
		return "/sainv/sainv/transferRequest/transferPendingRequestPDF.sa?docId=1";
	}

	public List<CodProducto> getCurrCodigos() {
		return currCodigos;
	}

	public void setCurrCodigos(List<CodProducto> currCodigos) {
		this.currCodigos = currCodigos;
	}

	public Map<String, ArrayList<CodProducto>> getLstCodsProductos() {
		return lstCodsProductos;
	}

	public void setLstCodsProductos(
			Map<String, ArrayList<CodProducto>> lstCodsProductos) {
		this.lstCodsProductos = lstCodsProductos;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Integer getCodigosRestantes() {
		return codigosRestantes;
	}

	public void setCodigosRestantes(Integer codigosRestantes) {
		this.codigosRestantes = codigosRestantes;
	}

	public List<Transferencia> getResultList() {
		return resultList;
	}

	public void setResultList(List<Transferencia> resultList) {
		this.resultList = resultList;
	}

	public List<Transferencia> getResultListGen() {
		return resultListGen;
	}

	public void setResultListGen(List<Transferencia> resultListGen) {
		this.resultListGen = resultListGen;
	}

	public String getEstadoFilter() {
		return estadoFilter;
	}

	public void setEstadoFilter(String estadoFilter) {
		this.estadoFilter = estadoFilter;
	}
	
	
	
	
}
