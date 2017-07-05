package com.sa.kubekit.action.sales;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.inventory.ItemHome;
import com.sa.kubekit.action.inventory.MovimientoHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.kubekit.action.workshop.AparatoClienteHome;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.sales.ComboAparato;
import com.sa.model.sales.DetVentaProdServ;
import com.sa.model.sales.ItemComboApa;
import com.sa.model.sales.Service;
import com.sa.model.sales.SolicitudImpresion;
import com.sa.model.sales.VentaProdServ;
import com.sa.model.security.Sucursal;
import com.sa.model.workshop.AparatoCliente;

@Name("ventaItemHome")
@Scope(ScopeType.CONVERSATION)
public class VentaItemHome extends KubeDAO<VentaProdServ> {

	private static final long serialVersionUID = 1L;

	@In
	private LoginUser loginUser;

	@In(required = false, create = true)
	@Out
	private ItemHome itemHome;

	@In(required = false, create = true)
	@Out
	private MovimientoHome movimientoHome;

	@In(required = false, create = true)
	@Out
	private AparatoClienteHome aparatoClienteHome;

	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private List<VentaProdServ> resultList = new ArrayList<VentaProdServ>();
	private Item selectedItem = new Item();
	private List<CodProducto> currCodigos = new ArrayList<CodProducto>();
	private Map<String, ArrayList<CodProducto>> lstCodsProductos = new HashMap<String, ArrayList<CodProducto>>();
	private Sucursal sucursalFlt;

	private Integer vtiId;
	private Float subTotal;
	private Float subTotalSrv;
	private Integer codigosRestantes;

	private List<Item> itemsAgregados = new ArrayList<Item>();
	private List<Service> serviciosAgregados = new ArrayList<Service>();

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("vtaitm_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("vtaitm_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("vtaitm_deleted")));
	}

	public void load() {
		try {
			setInstance(getEntityManager().find(VentaProdServ.class, vtiId));
			// itemsAgregados = new ArrayList<Item>(instance.getItems());
			// empresaSeleccionada = instance.getSucursal().getEmpresa();
		} catch (Exception e) {

		}
		clearInstance();
		setInstance(new VentaProdServ());
		instance.setSucursal(loginUser.getUser().getSucursal());
		instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
		instance.setFechaVenta(new Date());
		instance.setTipoVenta("ITM");
		instance.setUsrEfectua(loginUser.getUser());
	}

	public String comprobarUser() {

		if (loginUser.getUser().isAccionEspecial()) {
			// getVentasItemList();
			return "/vtas/vtaitem/listVtaProd.xhtml";
		} else {
			return "/vtas/vtaitem/master.xhtml";
		}

	}

	public void getVentasItemList() {
		if (sucursalFlt == null)
			sucursalFlt = loginUser.getUser().getSucursal();
		resultList = getEntityManager()
				.createQuery(
						"SELECT v FROM VentaProdServ v WHERE v.tipoVenta = 'ITM' AND v.sucursal = :suc")
				.setParameter("suc", sucursalFlt).getResultList();

		if (loginUser.getUser().getSucursal() == null
				&& (sucursales == null && sucursales.size() <= 0))
			sucursales = getEntityManager().createQuery(
					"SELECT s FROM Sucursal s").getResultList();
	}

	public void agregarProducto(Inventario producto) {
		
		for(Item item : itemsAgregados)
		{
			if(item.getInventario().getProducto().getId()==producto.getProducto().getId())
			{
				FacesMessages.instance().add(Severity.WARN,"El producto ya fue seleccionado");
				return;
			}
		}
		
		Item item = new Item();
		item.setCantidad(1);
		//item.setCostoUnitario(producto.getProducto().getPrcNormal()); cambiado el 06/12/2016
		
		item.setCostoUnitario(producto.getProducto().getCosto());
		item.setPrecioVenta(producto.getProducto().getPrcNormal());
		
		item.setInventario(producto);
		item.setItemId(new ItemId());
		AparatoCliente ac = new AparatoCliente();
		item.setAparato(ac);
		item.getItemId().setInventarioId(producto.getId());
		itemsAgregados.add(0, item);
		actualizarSubtotal();
	}

	public void agregarServicio(Service srv) {
		srv.setCantidad(srv.getCantidad() + 1);
		if (!serviciosAgregados.contains(srv))
			serviciosAgregados.add(0, srv);
		actualizarSubtotalSrv();
	}

	public void actualizarSubtotalSrv() {
		subTotalSrv = new Float(0);

		for (Service srv : serviciosAgregados) {
			subTotalSrv = subTotalSrv
					+ (srv.getCantidad() * srv.getCosto().floatValue());
		}
		setSubTotalSrv(subTotalSrv);
	}

	public void actualizarSubtotal() {
		Float subtotal = new Float(0);
		for (Item item : itemsAgregados) {
			if (item.getTipoPrecio().equals("NRM")
					&& item.getInventario().getProducto().getPrcNormal() != null)
				subtotal = subtotal
						+ (item.getCantidad() * item.getInventario()
								.getProducto().getPrcNormal());
			else if (item.getTipoPrecio().equals("MIN")
					&& item.getInventario().getProducto().getPrcMinimo() != null)
				subtotal = subtotal
						+ (item.getCantidad() * item.getInventario()
								.getProducto().getPrcMinimo());
			else if (item.getTipoPrecio().equals("OFE")
					&& item.getInventario().getProducto().getPrcOferta() != null)
				subtotal = subtotal
						+ (item.getCantidad() * item.getInventario()
								.getProducto().getPrcOferta());
		}
		setSubTotal(subtotal);
	}

	public void removerItem(Item item) {
		itemsAgregados.remove(item);
		lstCodsProductos.remove(item.getInventario().getProducto()
				.getReferencia());
		actualizarSubtotal();
	}

	public void removerServicio(Service srv) {
		serviciosAgregados.remove(srv);
		actualizarSubtotalSrv();
	}

	// Para limitar los items que seleccionan
	public void limitSelectedCodes(CodProducto chkCod) {
		if (chkCod.isTransferido()) {
			ArrayList<CodProducto> codsProds = lstCodsProductos
					.get(selectedItem.getInventario().getProducto()
							.getReferencia());
			int cntChecked = 0;
			for (CodProducto tmpCod : codsProds)
				cntChecked += tmpCod.isTransferido() ? 1 : 0;

			if (cntChecked > selectedItem.getCantidad())
				chkCod.setTransferido(false);
			else
				codigosRestantes++;
		} else
			codigosRestantes--;
	}

	public void setCodigosItm(CodProducto cdp) {
		selectedItem.setCodProducto(cdp);
		selectedItem = null;
	}

	public void ponerSelected(Item prdItm) {
		selectedItem = prdItm;
	}

	public void cargarListaCodigos(Item prdItm) {
		selectedItem = prdItm;
		codigosRestantes = 0;
		ArrayList<CodProducto> codsProds = null;
		// Si aun no se ha cargado la lista para ese item, la cargamos
		if (lstCodsProductos.get(prdItm.getInventario().getProducto()
				.getReferencia()) == null) {
			codsProds = (ArrayList<CodProducto>) getEntityManager()
					.createQuery(
							"SELECT c FROM CodProducto c "
									+ "	WHERE c.inventario = :inv AND c.estado = 'ACT' ")
					.setParameter("inv", prdItm.getInventario())
					.getResultList();
		} else {
			codsProds = lstCodsProductos.get(prdItm.getInventario()
					.getProducto().getReferencia());
			// contamos cuantos habian dejado chequeados
			for (CodProducto tmpCod : codsProds)
				if (tmpCod.isTransferido())
					codigosRestantes++;
		}

		lstCodsProductos.put(prdItm.getInventario().getProducto()
				.getReferencia(), codsProds);
		currCodigos = codsProds;
	}

	public Image getImgPrd(Item prd) {
		try {
			BufferedImage imag;
			if (prd.getInventario().getProducto() != null
					&& prd.getInventario().getProducto().getImage() != null
					&& prd.getInventario().getProducto().getImage().length > 0) {
				System.out.println("Entré a imagen del producto "
						+ prd.getInventario().getProducto().getNombre());
				imag = ImageIO.read(new ByteArrayInputStream(prd
						.getInventario().getProducto().getImage()));
				return imag;
			} else {
				System.out.println("Entre a No Image");
				ClassLoader classLoader = Thread.currentThread()
						.getContextClassLoader();
				System.out.println("Tengo el Class loader");
				String path = classLoader.getResource(
						"../../kubeImg/noimage.png").getPath();
				System.out.println(path);
				File pathToFile = new File(path);
				Image image = ImageIO.read(pathToFile);
				return image;
			}
		} catch (IOException e) {
			System.out.println("No se pudo convertir la imagen: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean preSave() {
		try {
			instance.setEstado("PEN");
			instance.setMonto(getSubTotal());
			instance.setIdDetalle(0);
			instance.setTipoVenta("ITM");

			if (instance.getSucursal() == null) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("vtaitm_error_save1"));
				return false;
			}

			if (instance.getCliente() == null) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("vtaitm_error_saveb"));
				return false;
			}

			if (itemsAgregados.isEmpty()) {
				System.out.println("ENtro a detalles vacios");
				FacesMessages.instance().add(Severity.WARN,"Debe agregar al menos un item");
				return false;
			}
			

			// Validamos que hayan ingresado el mismo numero de codigos que de
			// items a comprar
			for (Item tmpItm : itemsAgregados) {
				// Validamos que para todos los productos de la venta que lleven
				// codigo de lote o de serie, lleven uno
				// Y que lleven el mismo numero de codigos que de items
				ArrayList<CodProducto> codsProds = lstCodsProductos.get(tmpItm
						.getInventario().getProducto().getReferencia());

				// validamos que se haya armado el aparato si el item es
				// realmente un aparato
				if (tmpItm.getInventario().getProducto().getCategoria()
						.isTieneNumLote()
						|| tmpItm.getInventario().getProducto().getCategoria()
								.isTieneNumSerie()) {
					if (tmpItm.getAparato().getLadoAparato() == null
							|| tmpItm.getAparato().getPeriodoGarantia() == null) {
						FacesMessages.instance().add(Severity.WARN,sainv_messages.get("vtaitm_error_apadet"));
						return false;
					}
				}

				if (tmpItm.getInventario().getProducto().getCategoria()
						.isTieneNumLote()
						&& (codsProds == null || codsProds.size() < tmpItm
								.getCantidad())) {
					FacesMessages.instance().add(Severity.WARN,sainv_messages.get("vtaitm_error_itmnolot"));
					return false;
				}

				if (tmpItm.getInventario().getProducto().getCategoria()
						.isTieneNumSerie()
						&& (codsProds == null || codsProds.size() < tmpItm
								.getCantidad())) {
					FacesMessages.instance().add(Severity.WARN,sainv_messages.get("vtaitm_error_itmnoser"));
					return false;
				}

				// Verificamos que no se superen las existencias
				if (tmpItm.getInventario().getCantidadActual() < tmpItm
						.getCantidad()) {
					FacesMessages.instance().add(Severity.WARN,sainv_messages.get("vtaitm_error_noexisvta"));
					return false;
				}

				if (tmpItm.getCantidad() <= 0) {
					FacesMessages.instance().add(Severity.WARN,sainv_messages.get("vtaitm_error_itmnumcero"));
					return false;
				}
			}
			instance.setDetalle("Venta de productos - " + instance.getDetalle());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean preModify() {
		if (instance.getSucursal() == null) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("vtaitm_error_save1"));
			return false;
		}
		if (itemsAgregados.isEmpty()) {
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("vtaitm_error_save2"));
			load();
			return false;
		}
		return true;
	}

	public void crearSolicitudImpresion() {

		System.out.println("Entro al metodo solicitud de impresion");

		SolicitudImpresion soli = new SolicitudImpresion();

		soli.setFecha(new Date());
		soli.setUsuario(loginUser.getUser());
		soli.setVenta(instance);

		// Guardando la solicitud de impresion
		if (instance.getId() != null) {
			getEntityManager().merge(soli);
			getEntityManager().flush();
		}
	}

	@Override
	public void posSave() {
		
		System.out.println("Entro al possave");
		// Reducimos las existencias del inventario y los numeros de serie o de
		// lote
		// Generamos el movimiento del inventario
		Movimiento mov = new Movimiento();
		mov.setFecha(new Date());
		mov.setSucursal(loginUser.getUser().getSucursal());
		mov.setTipoMovimiento("S");
		mov.setUsuario(loginUser.getUser());
		mov.setRazon("V");
		mov.setObservacion("Salida por la venta generada automaticamente de productos, detalle de la venta:"
				+ instance.getDetalle());
		movimientoHome.select(mov);
		movimientoHome.setItemsAgregados(itemsAgregados);
		movimientoHome.save();
		
		getEntityManager().refresh(movimientoHome.getInstance());
		instance.setMovimiento(movimientoHome.getInstance());

		for (Item item : itemsAgregados) {
			
			if(item.getTipoPrecio().equals("NRM"))
				item.setPrecioVenta(item.getInventario().getProducto().getPrcNormal());
			else if(item.getTipoPrecio().equals("MIN"))
				item.setPrecioVenta(item.getInventario().getProducto().getPrcMinimo());
			else if(item.getTipoPrecio().equals("OFE"))
				item.setPrecioVenta(item.getInventario().getProducto().getPrcOferta());
			
			
			//item.getItemId().setMovimientoId(instance.getId());//No deberia de ser getIdMovimiento?
			item.getItemId().setMovimientoId(instance.getMovimiento().getId());// Esta es la asignacion correcta. 03/01/2017
			item.setMovimiento(mov);
			
			System.out.println("Id de objeto movimiento"+ mov.getId());
			System.out.println("id de ItemId-> idMovimiento "+instance.getMovimiento().getId());
			
			System.out.println("Tipo de precio seleccionado"+ item.getTipoPrecio());
			System.out.println("Precio de venta asignado en paso anterior"+item.getPrecioVenta());
			System.out.println("Precio cotizado "+ item.getPrecioCotizado());
			System.out.println("Precio minimo" +item.getInventario().getProducto().getPrcMinimo());
			
			System.out.println("itemId->Id inventadio" + item.getItemId().getInventarioId());
			System.out.println("itemId->Id Movimiento "+ item.getItemId().getMovimientoId());
			System.out.println("Id Inventario"+ item.getInventario().getId());
			
			itemHome.setInstance(item);
			itemHome.save();

			// Guardamos los codigos si es que tienen codigos y generamos
			// aparato
			if (item.getInventario().getProducto().getCategoria()
					.isTieneNumSerie()
					|| item.getInventario().getProducto().getCategoria()
							.isTieneNumLote()) {
				// formamos el aparato
				item.getAparato().setCliente(instance.getCliente());
				item.getAparato().setCostoVenta(item.getCostoUnitario());
				item.getAparato().setCustomApa(false);
				item.getAparato().setEstado("ACT");
				item.getAparato().setActivo(true);
				item.getAparato().setFechaAdquisicion(new Date());
				item.getAparato().setHechoMedida(false);
				item.getAparato().setIdPrd(
						item.getInventario().getProducto().getId());
				item.getAparato().setMarca(
						item.getInventario().getProducto().getMarca()
								.getNombre());
				item.getAparato().setModelo(
						item.getInventario().getProducto().getModelo());
				item.getAparato().setNombre(
						item.getInventario().getProducto().getNombre());

				// guardamos codigos
				ArrayList<CodProducto> codsProds = lstCodsProductos.get(item
						.getInventario().getProducto().getReferencia());
				for (CodProducto tmpCd : codsProds) {
					if (tmpCd.isTransferido()) {
						tmpCd.setEstado("USD");
						if (tmpCd.getNumLote() != null) {
							item.getAparato().setNumLote(tmpCd.getNumLote());
						} else if (tmpCd.getNumSerie() != null) {
							item.getAparato().setNumLote(tmpCd.getNumSerie());
						}
						item.setCodProducto(tmpCd);
						getEntityManager().merge(tmpCd);
						getEntityManager().flush();
						item.setPrincipal(true);
					}
					item.setCodsProducto(codsProds);
				}
				
				aparatoClienteHome.setInstance(item.getAparato());
				aparatoClienteHome.getItems().add(item);
				aparatoClienteHome.save();
			}

			// Generamos el detalle de la venta
			DetVentaProdServ detVta = new DetVentaProdServ();
			detVta.setCantidad(item.getCantidad());
			if (item.getTipoPrecio().equals("NRM")
					&& item.getInventario().getProducto().getPrcNormal() != null)
				detVta.setMonto(item.getInventario().getProducto()
						.getPrcNormal());
			else if (item.getTipoPrecio().equals("MIN")
					&& item.getInventario().getProducto().getPrcMinimo() != null)
				detVta.setMonto(item.getInventario().getProducto()
						.getPrcMinimo());
			else if (item.getTipoPrecio().equals("OFE")
					&& item.getInventario().getProducto().getPrcOferta() != null)
				detVta.setMonto(item.getInventario().getProducto()
						.getPrcOferta());
			detVta.setVenta(instance);
			detVta.setCodExacto(item.getInventario().getProducto()
					.getReferencia());
			detVta.setCodClasifVta("PRD");
			detVta.setCosto(item.getInventario().getProducto().getCosto());

			String numsSeries = "", numsLotes = "";
			ArrayList<CodProducto> codsProds = getLstCodsProductos().get(
					item.getInventario().getProducto().getReferencia());
			if (codsProds != null && codsProds.size() > 0)
				for (CodProducto tmpCd : codsProds) {
					if (tmpCd.isTransferido() && tmpCd.getNumSerie() != null && !tmpCd.getNumSerie().equals(""))
						numsSeries = numsSeries.concat(tmpCd.getNumSerie().trim() + ",");
					if (tmpCd.isTransferido() && tmpCd.getNumLote() != null&& !tmpCd.getNumLote().equals(""))
						numsLotes = numsLotes.concat(tmpCd.getNumLote().trim()+ ",");
				}
			detVta.setNumSerie(detVta.getNumSerie().concat(numsSeries));
			detVta.setNumLote(detVta.getNumLote().concat(numsLotes));
			

			// Formamos el detalle
			StringBuilder bld = new StringBuilder();
			bld.append(item.getInventario().getProducto().getNombre());
			if (item.getInventario().getProducto().getModelo() != null)
				bld.append(", Modelo "
						+ item.getInventario().getProducto().getModelo());
			if (item.getInventario().getProducto().getMarca() != null)
				bld.append(", Marca "
						+ item.getInventario().getProducto().getMarca()
								.getNombre());
			if (detVta.getNumSerie() != null
					&& !detVta.getNumSerie().equals(""))
				bld.append(", Series " + detVta.getNumSerie());
			if (detVta.getNumLote() != null && !detVta.getNumLote().equals(""))
				bld.append(", Lote " + detVta.getNumLote());
			
			detVta.setProducto(item.getInventario().getProducto()); //nuevo agregado 09/11/2016

			detVta.setDetalle(bld.toString());
			getEntityManager().persist(detVta);
		}
		
		
		//crearSolicitudImpresion(); comentado el 02/05/2016

		getEntityManager().flush();
		getEntityManager().refresh(instance);
		
		itemsAgregados.clear();
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub

	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}

	public List<Item> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<Item> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public List<CodProducto> getCurrCodigos() {
		return currCodigos;
	}

	public void setCurrCodigos(List<CodProducto> currCodigos) {
		this.currCodigos = currCodigos;
	}

	public Item getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<VentaProdServ> getResultList() {
		return resultList;
	}

	public void setResultList(List<VentaProdServ> resultList) {
		this.resultList = resultList;
	}

	public Float getSubTotal() {
		if (subTotal != null)
			return subTotal;
		else
			return 0f;
	}

	public void setSubTotal(Float subTotal) {
		this.subTotal = subTotal;
	}

	public Integer getVtiId() {
		return vtiId;
	}

	public void setVtiId(Integer vtiId) {
		this.vtiId = vtiId;
	}

	public Integer getCodigosRestantes() {
		return codigosRestantes;
	}

	public void setCodigosRestantes(Integer codigosRestantes) {
		this.codigosRestantes = codigosRestantes;
	}

	public Map<String, ArrayList<CodProducto>> getLstCodsProductos() {
		return lstCodsProductos;
	}

	public void setLstCodsProductos(
			Map<String, ArrayList<CodProducto>> lstCodsProductos) {
		this.lstCodsProductos = lstCodsProductos;
	}

	public Sucursal getSucursalFlt() {
		return sucursalFlt;
	}

	public void setSucursalFlt(Sucursal sucursalFlt) {
		this.sucursalFlt = sucursalFlt;
	}

	public List<Service> getServiciosAgregados() {
		return serviciosAgregados;
	}

	public void setServiciosAgregados(List<Service> serviciosAgregados) {
		this.serviciosAgregados = serviciosAgregados;
	}

	public Float getSubTotalSrv() {
		if (subTotalSrv != null)
			return subTotalSrv;
		else
			return 0f;
	}

	public void setSubTotalSrv(Float subTotalSrv) {
		this.subTotalSrv = subTotalSrv;
	}

}
