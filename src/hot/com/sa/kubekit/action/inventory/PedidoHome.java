package com.sa.kubekit.action.inventory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.sa.model.inventory.Compra;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.ItemPedido;
import com.sa.model.inventory.Pedido;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.inventory.id.ItemPedidoId;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;

@Name("pedidoHome")
@Scope(ScopeType.CONVERSATION)
public class PedidoHome extends KubeDAO<Pedido> {

	private static final long serialVersionUID = 1L;

	@In
	@Out
	private Controller controller;
	
	@In(required=false,create=true)
	@Out(required=false)
	private ProductoHome productoHome;
	
	@In(required = true)
	protected KubeBundle sainv_view_messages;
	
	@In
	private LoginUser loginUser;
	
	@In(required=false,create=true)
	private PedidoPendienteList pedidosPendienteList;
	
	@In(required = false, create = true)
	private ItemPedidoHome itemPedidoHome;

	@In(required = false, create = true)
	@Out(required=false)
	private CompraHome compraHome;

	private Empresa empresaSeleccionada;
	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private List<Pedido> resultList = new ArrayList<Pedido>();
	private Integer pedidoId;
	private List<ItemPedido> itemsAgregados = new ArrayList<ItemPedido>();
	private List<Inventario> productosAgregados = new ArrayList<Inventario>();
	private List<String> archivos = new ArrayList<String>();
	private String nombreArchivo;

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("pedidoHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("pedidoHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("pedidoHome_deleted")));
	}

	public void load() {
		try {
			setInstance(getEntityManager().find(Pedido.class, pedidoId));
			itemsAgregados = new ArrayList<ItemPedido>(instance.getItems());
			productosAgregados = new ArrayList<Inventario>();
			for (ItemPedido item : itemsAgregados) {
				productosAgregados.add(item.getInventario());
			}
			empresaSeleccionada = instance.getSucursal().getEmpresa();
		} catch (Exception e) {
			clearInstance();
			setInstance(new Pedido());
			if (loginUser.getUser() != null) {
				instance.setUsuario(loginUser.getUser());
				if (loginUser.getUser().getSucursal() != null) {
					instance.setSucursal(loginUser.getUser().getSucursal());
				}
			}
		}
		if (empresaSeleccionada != null)
			sucursales = new ArrayList<Sucursal>(
					empresaSeleccionada.getSucursales());
	}

	public void asignarSucursal(){
		this.productoHome.setSucursalSeleccionada(instance.getSucursal());
	}
	
	public void getPedidosPendientesList() {
		if(loginUser.getUser().getSucursal() != null) {
			List<Sucursal> subSucFlt = getEntityManager()
					.createQuery("SELECT s FROM Sucursal s WHERE s = :suc OR s.sucursalSuperior = :suc or s.sucursalSuperior = :otraSuc")
					.setParameter("suc", loginUser.getUser().getSucursal())
					.setParameter("otraSuc", loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior())
					.getResultList(); 
			
			if(subSucFlt == null || subSucFlt.size() <= 0) 
				subSucFlt = new ArrayList<Sucursal>();
			
			subSucFlt.add(loginUser.getUser().getSucursal());
			subSucFlt.add(loginUser.getUser().getSucursal().getSucursalSuperior() == null?loginUser.getUser().getSucursal():loginUser.getUser().getSucursal().getSucursalSuperior());
			
			if(subSucFlt == null || subSucFlt.size() <= 0) {
				resultList = getEntityManager()
						.createQuery("select p from Pedido p where p.estado LIKE 'P' AND " +
								" (p.sucursal = :suc ) " +
								"	order by p.fechaInicio desc, p.proveedor.razonSocial ")
						.setParameter("suc", loginUser.getUser().getSucursal())
						.getResultList();
			} else {
				resultList = getEntityManager()
						.createQuery("select p from Pedido p where p.estado LIKE 'P' AND " +
								" (p.sucursal = :suc OR p.sucursal IN (:subSuc) ) " +
								"	order by p.fechaInicio desc, p.proveedor.razonSocial ")
						.setParameter("suc", loginUser.getUser().getSucursal())
						.setParameter("subSuc", subSucFlt)
						.getResultList();
			}
			
		} else {
			resultList = getEntityManager()
					.createQuery("select p from Pedido p where p.estado = 'P' " +
							"	order by p.fechaInicio desc, p.proveedor.razonSocial ")
					.getResultList();
		}
	}
	
	public void agregarProducto(Inventario producto) {
		if (productosAgregados.contains(producto)) {
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("pedidoHome_error_additem"));
			return;
		}

		ItemPedido item = new ItemPedido();
		item.setCantidad(1);
		item.setCostoUnitario(producto.getProducto().getPrcNormal());
		item.setInventario(producto);
		item.setItemPedidoId(new ItemPedidoId());
		item.getItemPedidoId().setInventarioId(producto.getId());
		itemsAgregados.add(item);
		productosAgregados.add(producto);
		actualizarSubtotal();
	}

	public void actualizarSubtotal() {
		Float subtotal = new Float(0);
		for (ItemPedido item : itemsAgregados) {
			subtotal = subtotal
					+ (item.getCantidad() * item.getCostoUnitario());
		}
		instance.setSubtotal(subtotal);
	}

	public void removerItem(ItemPedido item) {
		itemsAgregados.remove(item);
		actualizarSubtotal();
		// productosAgregados.remove(item.getProducto());
	}

	@Override
	public boolean preSave() {
		if (instance.getSucursal() == null) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("pedidoHome_error_save1"));
			return false;
		}
		if (itemsAgregados.isEmpty()) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("pedidoHome_error_save2"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		if (instance.getSucursal() == null) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("pedidoHome_error_save1"));
			return false;
		}
		if (itemsAgregados.isEmpty()) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("pedidoHome_error_save2"));
			load();
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		if(instance.getEstado().equals("P")){
			for(ItemPedido item : instance.getItems()){
				item.setPedido(null);
				itemPedidoHome.setInstance(item);
				itemPedidoHome.modify();
				itemPedidoHome.delete();
			}
			instance.setItems(new java.util.HashSet<ItemPedido>());
			FacesMessages.instance().clear();
			return true;
		}else{
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("pedidoHome_error_delete2"));
			return false;
		}
		
	}

	@Override
	public void posSave() {
		for (ItemPedido item : itemsAgregados) {
			item.setPedido(instance);
			item.getItemPedidoId().setPedidoId(instance.getId());
			itemPedidoHome.setInstance(item);
			itemPedidoHome.save();
		}
		getEntityManager().flush();
		getEntityManager().refresh(instance);
	}
	
	@Override
	public void posModify() {
		List<ItemPedido> itemsRemover = new ArrayList<ItemPedido>(instance.getItems());
		itemsRemover.removeAll(itemsAgregados);
		
		for(ItemPedido item : itemsRemover){
			itemPedidoHome.setInstance(item);
			itemPedidoHome.delete();
		}
		
		for (ItemPedido item : itemsAgregados) {
			item.setPedido(instance);
			item.getItemPedidoId().setPedidoId(instance.getId());
			itemPedidoHome.setInstance(item);
			if(itemPedidoHome.isManaged()){
				itemPedidoHome.modify();
			}else{
				itemPedidoHome.save();
			}
		}
		
		getEntityManager().flush();
		getEntityManager().refresh(instance);

	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub

	}
	
	public boolean generarReportePedidosPendientes(){
		
		UtilDate util = new UtilDate();
		String fechaReporte = util.convertDateTime(new Date()).replace(":", "_");
		String archivo = "report_pending_order"+fechaReporte.replace(" ", "_")+".xls";
		
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
		
		getPedidosPendientesList();
		
		for(Pedido pedido : resultList){
			
			Row row = sheet.createRow(rowNum);
			Cell cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("order_pending_list_col1")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(util.convertDate(pedido.getFechaInicio()));
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("order_pending_list_col2")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(pedido.getProveedor().getRazonSocial());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("order_pending_list_col3")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(pedido.getItems().size());
			
			rowNum++;
			colNum = 0;
			
			row = sheet.createRow(rowNum);
			cell = row.createCell(colNum++);
			cell.setCellStyle(cs);
			cell.setCellValue(createHelper
					.createRichTextString(sainv_view_messages.get("order_pending_list_col4")));
			
			cell = row.createCell(colNum);
			cell.setCellStyle(cs2);
			cell.setCellValue(pedido.getUsuario().getNombreUsuario());
			
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
			
			for(ItemPedido item : pedido.getItems()){
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
		this.archivos.add(ruta);
		controller.agregarArchivoTemp(ruta);
		this.setNombreArchivo(ruta);
		
		String filename = session.getServletContext().getRealPath("/")
			+ "sainv" + System.getProperty("file.separator") + "pedidoPendiente" + System.getProperty("file.separator") 
			+ "downloads" + System.getProperty("file.separator") + archivo;
		
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
	
	public List<ItemPedido> obtenerItemsPedido(Pedido pedido){
		return new ArrayList<ItemPedido>(pedido.getItems());
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

	public Integer getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Integer pedidoId) {
		this.pedidoId = pedidoId;
	}

	public List<ItemPedido> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<ItemPedido> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public void cargarSucursales() {
		if (empresaSeleccionada != null)
			setSucursales(new ArrayList<Sucursal>(
					empresaSeleccionada.getSucursales()));
		else
			setSucursales(new ArrayList<Sucursal>());
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}
	
	public boolean approve() {
		List<Item> items = new ArrayList<Item>();
		for (ItemPedido item : instance.getItems()) {
			Item itemCompra = new Item();
			itemCompra.setCantidad(item.getCantidad());
			itemCompra.setCostoUnitario(item.getCostoUnitario());
			itemCompra.setInventario(item.getInventario());
			itemCompra.setItemId(new ItemId());
			itemCompra.getItemId()
					.setInventarioId(item.getInventario().getId());
			items.add(itemCompra);
		}

		Compra compra = new Compra();

		// Compra
		compra.setFormaPago("E");
		compra.setNumeroFactura("PED-" + instance.getId());
		compra.setSubTotal(instance.getSubtotal());
		compra.setPedido(instance);
		compra.setSucursal(instance.getSucursal());
		compra.setUsuario(instance.getUsuario());

		// Movimiento
		compra.setObservacion("Compra generada automaticamente como aprobacion del pedido "
				+ instance.getId());
		compra.setTipoMovimiento("E");
		compra.setFecha(new Date());
		compra.setRazon("P");

		compraHome.setInstance(compra);
		compraHome.setItemsAgregados(items);
		if (compraHome.save()) {
			instance.setEstado("A");
			instance.setFechaFinalizo(new Date());
			instance.setCompra(getEntityManager().merge(
					compraHome.getInstance()));
			return modify();
		}
		return false;
	}

	public boolean reject() {
		instance.setEstado("R");
		return modify();
	}

	public CompraHome getCompraHome() {
		return compraHome;
	}

	public void setCompraHome(CompraHome compraHome) {
		this.compraHome = compraHome;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	
	public String getOrderPDF(){
		return "/sainv/sainv/pedidoPendiente/orderPendingPDF.sa?docId=1";
	}

	public List<Pedido> getResultList() {
		return resultList;
	}

	public void setResultList(List<Pedido> resultList) {
		this.resultList = resultList;
	}
	
}
