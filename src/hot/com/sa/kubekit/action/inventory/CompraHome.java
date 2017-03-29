package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.CodProducto;
import com.sa.model.inventory.Compra;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.ItemPedido;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;


@Name("compraHome")
@Scope(ScopeType.CONVERSATION)
public class CompraHome extends KubeDAO<Compra>{

	private static final long serialVersionUID = 1L;
		
	@In
	private LoginUser loginUser;

	@In(required=false,create=true)
	private ItemHome itemHome;
	private List<Compra> resultList = new ArrayList<Compra>();
	private Empresa empresaSeleccionada;
	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	private String nomCoinci;
	private Map<String, ArrayList<CodProducto>> lstCodsProductos = new HashMap<String, ArrayList<CodProducto>>();
	private Item selectedItem = new Item();
	private List<CodProducto> currCodigos = new ArrayList<CodProducto>();
	private Integer compraId;
	
	private List<Item> itemsAgregados = new ArrayList<Item>();
	private List<Inventario> productosAgregados = new ArrayList<Inventario>();
	private Sucursal sucursalSelected = new Sucursal();
	
	private String formaPago="0";
	
	@In(required=false,create=true)
	private InventarioHome inventarioHome;
	
	@Override
	public void create() {
		setRangoUlt30dias();
		cargarCompras();
		setCreatedMessage(createValueExpression(sainv_messages
				.get("compraHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("compraHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("compraHome_deleted")));
	}
	
	public void load(){
		try{
			selectedItem = null;
			setInstance(getEntityManager().find(Compra.class, compraId));
			itemsAgregados = new ArrayList<Item>(instance.getItems());
			productosAgregados = new ArrayList<Inventario>();
			for(Item producto: instance.getItems()){
				productosAgregados.add(producto.getInventario());
			}
			empresaSeleccionada = instance.getSucursal().getEmpresa();
		}catch (Exception e) {
			clearInstance();
			setInstance(new Compra());
			if(loginUser.getUser()!=null){
				instance.setUsuario(loginUser.getUser());
				instance.setTipoMovimiento("E");
				instance.setFecha(new Date());
				instance.setRazon("C");
				if(loginUser.getUser().getSucursal()!=null){
					instance.setSucursal(loginUser.getUser().getSucursal());
				}
			}
		}
		if(empresaSeleccionada!=null)
			sucursales = new ArrayList<Sucursal>(empresaSeleccionada.getSucursales());
	}
	
	public void agregarProducto(Inventario producto){
		if(productosAgregados.contains(producto)){
			FacesMessages.instance().add(Severity.WARN,
				sainv_messages.get("compraHome_error_additem"));
			return;
		}
		
		Item item = new Item();
		item.setCantidad(1);
		
		//item.setCostoUnitario(producto.getProducto().getPrcNormal()); Cambiado el 06/12/2016
		item.setCostoUnitario(producto.getProducto().getCosto());
		
		item.setInventario(producto);
		item.setItemId(new ItemId());
		item.getItemId().setInventarioId(producto.getId());
		itemsAgregados.add(0, item);
		productosAgregados.add(producto);
		actualizarSubtotal();
	}
	
	public void actualizarSubtotal(){
		Float subtotal = new Float(0);
		for(Item item : itemsAgregados){
			subtotal = subtotal + (item.getCantidad() * item.getCostoUnitario());
		}
		instance.setSubTotal(subtotal);
	}
	
	public void removerItem(Item item){
		itemsAgregados.remove(item);
		productosAgregados.remove(item.getInventario());
		lstCodsProductos.remove(item.getInventario().getProducto().getReferencia());
		actualizarSubtotal();
	}
	
public void cargarListaCodigos(ItemPedido prdItm) {
		Item itemCompra = new Item();
		itemCompra.setCantidad(prdItm.getCantidad());
		itemCompra.setCostoUnitario(prdItm.getCostoUnitario());
		itemCompra.setInventario(prdItm.getInventario());
		cargarListaCodigos(itemCompra);
	}

public void cargarCompras() {
	String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2) ";
	if(getFechaPFlt1() != null && getFechaPFlt2() != null) {
		setFechaPFlt1(truncDate(getFechaPFlt1(), false));
		setFechaPFlt2(truncDate(getFechaPFlt2(), true));
		fltFch = " AND x.fecha BETWEEN :fch1 AND :fch2 ";
	}
	
	if(loginUser.getUser().isAccionEspecial())
		fltFch += " AND (:suc = :suc) ";
	else
		fltFch += " AND x.sucursal = :suc ";
	
	setResultList(getEntityManager()
			.createQuery("SELECT x FROM Compra x WHERE 1 = 1 " + fltFch + " ORDER BY x.fecha DESC ")
				.setParameter("suc", loginUser.getUser().getSucursal())
				.setParameter("fch1", getFechaPFlt1())
				.setParameter("fch2", getFechaPFlt2())
			.getResultList());
}

	public void cargarListaCodigos(Item prdItm){
		//prdItm.getC
		selectedItem = prdItm;
		ArrayList<CodProducto> codsProds = null;
		//Buscamos primero si ya esta la lista en la lista madre
		if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia()) == null && prdItm.getItemId() != null) {
			codsProds = (ArrayList<CodProducto>)getEntityManager().createQuery("SELECT c FROM CodProducto c " +
					"	WHERE c.inventario = :inv AND c.movimiento = :mov ")
					.setParameter("inv", prdItm.getInventario())
					.setParameter("mov", prdItm.getMovimiento())
					.getResultList();
			lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
			System.out.println("Referencia prodycto"+prdItm.getInventario().getProducto().getReferencia());
			System.out.println("Asigno el valor a la lista codigo");
			System.out.println("Tambn codsProds dentro"+codsProds.size());
		} else {
			codsProds = lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia());
			System.out.println("NOO Asigno el valor a la lista codigo");
		}
		
		System.out.println("Producto cantidad antes"+prdItm.getCantidad());
		prdItm.setCantidad( (prdItm.getCantidad() == null?1:prdItm.getCantidad()) );
		System.out.println("Producto cantidad dps"+prdItm.getCantidad());
		System.out.println("Tamanios Cod fuera"+codsProds.size());
		if(codsProds == null) 
			codsProds = new ArrayList<CodProducto>();
		
		while(codsProds.size() < prdItm.getCantidad()) {
			System.out.println("Entro al while");
			CodProducto codPrd = new CodProducto();
			codPrd.setEstado("ACT");
			codPrd.setInventario(prdItm.getInventario());
			codsProds.add(codPrd);
		}
				
		currCodigos = codsProds;
		lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
	}
	
	public void cargarListaCodigosNuevo(Item prdItm,CodProducto c)
	{
		selectedItem = prdItm;
		ArrayList<CodProducto> codsProds = new ArrayList<CodProducto>();
		
		/* nuevo */
		/*lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
		System.out.println("Asigno el valor a la lista codigo");*/
		
		if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia())==null)
		{
			lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), new ArrayList<CodProducto>());
		}
		
		if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia()).size()>0)
		{
			if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia()).contains(c))
			{
				FacesMessages.instance().add(Severity.WARN,"El codigo ya fue agregado");
				return; 
			}	
			else
			{
				System.out.println("Lista de codigos mayor a cero y no repetido, entro al else *******");
				codsProds=lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia());
				
			}
		}
		else
		{
			System.out.println("Inventario con cero codigos ******");
		}
		
		c.setInventario(prdItm.getInventario());
		c.setEstado("ACT");
		codsProds.add(c);
		
		/*CodProducto codPrd = c;
		codPrd.setEstado("ACT");
		codPrd.setInventario(prdItm.getInventario());
		codsProds.add(codPrd);*/
		
				
		currCodigos = codsProds;
		lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
	}
	
	public void cargarListaCodigosReingreso(Item prdItm){
		selectedItem = prdItm;
		ArrayList<CodProducto> codsProds = null;
		//Buscamos primero si ya esta la lista en la lista madre
		if(lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia()) == null && prdItm.getItemId() != null) {
			codsProds = (ArrayList<CodProducto>)getEntityManager().createQuery("SELECT c FROM CodProducto c " +
					"	WHERE c.inventario = :inv AND c.movimiento = :mov ")
					.setParameter("inv", prdItm.getInventario())
					.setParameter("mov", prdItm.getMovimiento())
					.getResultList();
			lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
			System.out.println("Asigno el valor a la lista codigo");
		} else {
			codsProds = lstCodsProductos.get(prdItm.getInventario().getProducto().getReferencia());
			System.out.println("NOO Asigno el valor a la lista codigo");
		}
		
		prdItm.setCantidad( (prdItm.getCantidad() == null?1:prdItm.getCantidad()) );
		
		if(codsProds == null) 
			codsProds = new ArrayList<CodProducto>();
		
		while(codsProds.size() < prdItm.getCantidad()) {
			System.out.println("Entro al while");
			CodProducto codPrd = new CodProducto();
			codPrd.setEstado("ACT");
			codPrd.setInventario(prdItm.getInventario());
			codsProds.add(codPrd);
		}
				
		currCodigos = codsProds;
		lstCodsProductos.put(prdItm.getInventario().getProducto().getReferencia(), codsProds);
	}

	@Override
	public boolean preSave() {
		System.out.println("Tamabio codigos"+lstCodsProductos.size());
		//System.out.println("Codigo serie en lista"+lstCodsProductos.get(0).get(0).getNumSerie());
		
		if(instance.getSucursal()==null){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("compraHome_error_save1"));
			return false;
		}
		if(itemsAgregados.isEmpty()){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("compraHome_error_save2"));
			return false;
		}
		
		if(instance.getNumeroFactura()==null || instance.getNumeroFactura()==""){
			FacesMessages.instance().add(Severity.WARN,"Debe agregar numero de factura");
			return false;
		}
		
		if(instance.getEstado()==null)
			instance.setEstado("Registrada");
		
		
		//Validamos que hayan ingresado el mismo numero de codigos que de items a comprar
		for(Item tmpItm : itemsAgregados) {
			if(lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia()) == null
					&& (tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() ||
							tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote()) )  {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("compra_error_prdnocods"));
				return false;
			} else if( (tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
							tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
						&& tmpItm.getCantidad() > lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia()).size()) {
				FacesMessages.instance().add(Severity.WARN,
						sainv_messages.get("compra_error_prdnocods"));
				return false;
			} 

			Set<String> codsStrPrd = new HashSet<String>();
			if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
					tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote()) {

				tmpItm.setCodsSerie("");
				//Verificamos que no vengan vacios los codigos ni repetidos
				for(CodProducto tmpCod : lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia())) {
					System.out.println("Num Serie for"+tmpCod.getNumSerie());
					if(tmpCod.getNumSerie() != null && !tmpCod.getNumSerie().trim().equals("")) 
						tmpItm.setCodsSerie(tmpItm.getCodsSerie().concat(tmpCod.getNumSerie()+","));
					
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() && 
							(tmpCod.getNumSerie() == null || tmpCod.getNumSerie().trim().equals(""))) {
						
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdnoser"));
						return false;
					}
					else if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() && (tmpCod.getNumSerie() != null || !tmpCod.getNumSerie().trim().equals("")))
					{
						
						if(codsStrPrd.contains(tmpCod.getNumSerie())) {
							System.out.println("Entro a if raro");
							FacesMessages.instance().add(Severity.WARN,
									sainv_messages.get("compra_error_prdcoddupli"));
							return false;
						}
						else
						{
							codsStrPrd.add(tmpCod.getNumSerie().toUpperCase());
						}
						
					}
					
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote() && 
							(tmpCod.getNumLote() == null || tmpCod.getNumLote().trim().equals(""))) {
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdnolot"));
						return false;
					}
					else if (tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote() && (tmpCod.getNumLote() != null || !tmpCod.getNumLote().trim().equals("")))
					{
						if(codsStrPrd.contains(tmpCod.getNumLote())) {
							System.out.println("Entro a if raro lote");
							FacesMessages.instance().add(Severity.WARN,
									sainv_messages.get("compra_error_prdcoddupli"));
							return false;
						}
						else
						{
							codsStrPrd.add(tmpCod.getNumLote().toUpperCase());
						}
					}
					
					/*if(codsStrPrd.contains(tmpCod.getNumSerie())) {
						System.out.println("Entro a if raro");
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdcoddupli"));
						return false;
					} else
						codsStrPrd.add(tmpCod.getNumSerie().toUpperCase());*/
					
				}
							
			}
			if(codsStrPrd != null && codsStrPrd.size() > 0 ) {
				
				if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie())
				{
					//Verificamos en la base si no existen otros codigos iguales
					List coincidencias = getEntityManager().createQuery("SELECT c FROM CodProducto c " +
								"	WHERE UPPER(c.numSerie) IN (:lstCods) AND c.estado = 'ACT' " +
								"	AND c.inventario = :inv")
								.setParameter("lstCods", codsStrPrd)
								.setParameter("inv", tmpItm.getInventario())
								.getResultList();
	
					if(coincidencias != null && coincidencias.size() > 0) {
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdcoddupli"));
						return false;
					}
				}
				
				if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
				{
					//Verificamos en la base si no existen otros codigos iguales
					List coincidencias = getEntityManager().createQuery("SELECT c FROM CodProducto c " +
								"	WHERE UPPER(c.numLote) IN (:lstCods) AND c.estado = 'ACT' " +
								"	AND c.inventario = :inv")
								.setParameter("lstCods", codsStrPrd)
								.setParameter("inv", tmpItm.getInventario())
								.getResultList();
	
					if(coincidencias != null && coincidencias.size() > 0) {
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdcoddupli"));
						return false;
					}
				}
				
			}
			
				
		}System.out.println("Llego al final de presave compra");
		return true;
	}
	
	
	public boolean preGuardar()
	{
		
		instance.setEstado("Pre-Guardada");
		
		
		return save();
		
	}
	
	public void desactualizarItem(Item item)
	{
		System.out.println("Cantidad"+item.getCantidad());
		System.out.println("Nombre"+item.getInventario().getProducto().getNombre());
		
		
		//Solucion
		
		//Compara con la categoria del inventario si este requiere serie o lote y cargar la lista de codigos que tiene con el metodo existente
		//recorrer la lista de codigos y eliminar o desactivar los codigos
	}
	
	
	public void guardarNuevosItems()
	{
		System.out.println("Entro a guardar nuevos items *********");
		if(!validarPreGuardado())
			return;
		
		
		System.out.println("PAso de la prevalidacion********");
		
		for(Item item: itemsAgregados){
			
			if(item.getRegistrado()==null)
			{
			
				item.getItemId().setMovimientoId(instance.getId());
				item.setMovimiento(instance);
				item.setRegistrado(true);//Nuevo el 24/03/2017
				itemHome.setInstance(item);
				itemHome.modificarCantidadInventario();
				itemHome.save();
				int numItemsCods = item.getCantidad();
				//Guardamos los codigos si es que tienen codigos
				if(item.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
						item.getInventario().getProducto().getCategoria().isTieneNumLote()) {
					for(CodProducto tmpCod : lstCodsProductos.get(item.getInventario().getProducto().getReferencia())) {
						if(numItemsCods <= 0)
							break;
						
						tmpCod.setMovimiento(instance);
						if(tmpCod.getId() != null && tmpCod.getId() > 0) 
							getEntityManager().merge(tmpCod);
						else
							getEntityManager().persist(tmpCod);
						numItemsCods--;
					}
					
				}
				
			}
		}
		
		FacesMessages.instance().add(Severity.INFO,"Items agregados");
		
	}
	
	public boolean validarPreGuardado()
	{
		if(instance.getSucursal()==null){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("compraHome_error_save1"));
			return false;
		}
		
		if(itemsAgregados.isEmpty()){
			FacesMessages.instance().add(Severity.WARN,
					sainv_messages.get("compraHome_error_save2"));
			return false;
		}
		
		if(instance.getNumeroFactura()==null || instance.getNumeroFactura()==""){
			FacesMessages.instance().add(Severity.WARN,"Debe agregar numero de factura");
			return false;
		}
		int it=0;System.out.println("Lista Codigos TAM"+lstCodsProductos.size());
		
		//Validamos que hayan ingresado el mismo numero de codigos que de items a comprar
		for(Item tmpItm : itemsAgregados) {
			
			if(tmpItm.getRegistrado()==null)
			{
				it++;System.out.println("ITERACIONES"+it);
					if(lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia()) == null
							&& (tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() ||
									tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote()) )  {
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdnocods"));
						return false;
					} else if( (tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
									tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
								&& tmpItm.getCantidad() > lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia()).size()) {
						FacesMessages.instance().add(Severity.WARN,
								sainv_messages.get("compra_error_prdnocods"));
						return false;
					} 
		
					Set<String> codsStrPrd = new HashSet<String>();
					if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
							tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote()) {
		
						tmpItm.setCodsSerie("");
						//Verificamos que no vengan vacios los codigos ni repetidos
						for(CodProducto tmpCod : lstCodsProductos.get(tmpItm.getInventario().getProducto().getReferencia())) {
							System.out.println("Num Serie for"+tmpCod.getNumSerie());
							if(tmpCod.getNumSerie() != null && !tmpCod.getNumSerie().trim().equals("")) 
								tmpItm.setCodsSerie(tmpItm.getCodsSerie().concat(tmpCod.getNumSerie()+","));
							
							if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() && 
									(tmpCod.getNumSerie() == null || tmpCod.getNumSerie().trim().equals(""))) {
								
								FacesMessages.instance().add(Severity.WARN,
										sainv_messages.get("compra_error_prdnoser"));
								return false;
							}
							else if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie() && (tmpCod.getNumSerie() != null || !tmpCod.getNumSerie().trim().equals("")))
							{
								
								if(codsStrPrd.contains(tmpCod.getNumSerie())) {
									System.out.println("Entro a if raro");
									FacesMessages.instance().add(Severity.WARN,
											sainv_messages.get("compra_error_prdcoddupli"));
									return false;
								}
								else
								{
									codsStrPrd.add(tmpCod.getNumSerie().toUpperCase());
								}
							}
							
							if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote() && 
									(tmpCod.getNumLote() == null || tmpCod.getNumLote().trim().equals(""))) {
								FacesMessages.instance().add(Severity.WARN,
										sainv_messages.get("compra_error_prdnolot"));
								return false;
							}
							else if (tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote() && (tmpCod.getNumLote() != null || !tmpCod.getNumLote().trim().equals("")))
							{
								if(codsStrPrd.contains(tmpCod.getNumLote())) {
									System.out.println("Entro a if raro lote");
									FacesMessages.instance().add(Severity.WARN,
											sainv_messages.get("compra_error_prdcoddupli"));
									return false;
								}
								else
								{
									codsStrPrd.add(tmpCod.getNumLote().toUpperCase());
								}
							}
							
							/*if(codsStrPrd.contains(tmpCod.getNumSerie())) {
								System.out.println("Entro a if raro");
								FacesMessages.instance().add(Severity.WARN,
										sainv_messages.get("compra_error_prdcoddupli"));
								return false;
							} *//*else
								codsStrPrd.add(tmpCod.getNumSerie().toUpperCase());*/
							
						}
									
					}
					if(codsStrPrd != null && codsStrPrd.size() > 0 ) {
						
						System.out.println("Valores repetidos posiblemente********");
						
						if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumSerie())
						{
							//Verificamos en la base si no existen otros codigos iguales
							List coincidencias = getEntityManager().createQuery("SELECT c FROM CodProducto c " +
										"	WHERE UPPER(c.numSerie) IN (:lstCods) AND c.estado = 'ACT' " +
										"	AND c.inventario = :inv")
										.setParameter("lstCods", codsStrPrd)
										.setParameter("inv", tmpItm.getInventario())
										.getResultList();
			
							if(coincidencias != null && coincidencias.size() > 0) {
								FacesMessages.instance().add(Severity.WARN,
										sainv_messages.get("compra_error_prdcoddupli"));
								return false;
							}
						}
						
						if(tmpItm.getInventario().getProducto().getCategoria().isTieneNumLote())
						{
							//Verificamos en la base si no existen otros codigos iguales
							List coincidencias = getEntityManager().createQuery("SELECT c FROM CodProducto c " +
										"	WHERE UPPER(c.numLote) IN (:lstCods) AND c.estado = 'ACT' " +
										"	AND c.inventario = :inv")
										.setParameter("lstCods", codsStrPrd)
										.setParameter("inv", tmpItm.getInventario())
										.getResultList();
			
							if(coincidencias != null && coincidencias.size() > 0) {
								FacesMessages.instance().add(Severity.WARN,
										sainv_messages.get("compra_error_prdcoddupli"));
								return false;
							}
						}
						
					}
			
		
			}		
			
		}
		
		
		System.out.println("Llego al final de presave compra");
		return true;
	}
	
	public boolean finalizarPreGuardado()
	{
		validarPreGuardado();
		
		guardarNuevosItems();
		
		instance.setEstado("Registrada");
		
		modify();
		
		FacesMessages.instance().add(Severity.INFO,"Compra registrada");
		
		return true;
		
	}

	@Override
	public boolean preModify() {
		if(instance.getSucursal()==null){
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("compraHome_error_save1"));
			return false;
		}
		if(itemsAgregados.isEmpty()){
			FacesMessages.instance().add(Severity.ERROR,
					sainv_messages.get("compraHome_error_save2"));
			load();
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		for(Item item : instance.getItems()){
			if(item.getCantidad()>item.getInventario().getCantidadActual()){
				item.setCantidad(item.getInventario().getCantidadActual());
			}
			item.setMovimiento(null);
			itemHome.setInstance(item);
			itemHome.disminuirItems(itemHome.getInstance().getCantidad());
			itemHome.modify();
			itemHome.delete();
		}
		instance.setItems(new ArrayList<Item>());
		FacesMessages.instance().clear();
		return true;
	}

	@Override
	public void posSave() {
		
		
		
		
		System.out.println("Entro al posave de compra");
		for(Item item: itemsAgregados){
			
				item.getItemId().setMovimientoId(instance.getId());
				item.setMovimiento(instance);
				item.setRegistrado(true);//Nuevo el 24/03/2017
				itemHome.setInstance(item);
				itemHome.modificarCantidadInventario();
				itemHome.save();
				int numItemsCods = item.getCantidad();
				//Guardamos los codigos si es que tienen codigos
				if(item.getInventario().getProducto().getCategoria().isTieneNumSerie() || 
						item.getInventario().getProducto().getCategoria().isTieneNumLote()) {
					for(CodProducto tmpCod : lstCodsProductos.get(item.getInventario().getProducto().getReferencia())) {
						if(numItemsCods <= 0)
							break;
						
						tmpCod.setMovimiento(instance);
						if(tmpCod.getId() != null && tmpCod.getId() > 0) 
							getEntityManager().merge(tmpCod);
						else
							getEntityManager().persist(tmpCod);
						numItemsCods--;
					}
					
				}
				
		}
		
		
		
		getEntityManager().flush();
		getEntityManager().refresh(instance);
		cargarCompras();
		System.out.println("Llego al final de possave compra");
	}
	
	
	public void cambiarSucursal()
	{
		Inventario nuevoInventario;
		
		//getEntityManager().getTransaction().begin();
		int contador=0;
		for(Item item: itemsAgregados)
		{
			
			nuevoInventario = new Inventario();
			/*item.getInventario();
			item.getItemId().getInventarioId();
			item.getCantidad();*/
			
			
			//Nuevo Inventario
			//Nuevo idInventario
			
			/*try {*/
				
				//getEntityManager().getTransaction();
				nuevoInventario = obtenerInventarioSucursal(item);
				nuevoInventario.setCantidadActual(nuevoInventario.getCantidadActual()+item.getCantidad());
				//nuevoInventario=getEntityManager().merge(nuevoInventario);
				inventarioHome.setInstance(nuevoInventario);
				inventarioHome.modify();
				System.out.println("nuevo"+nuevoInventario.getCantidadActual());
				System.out.println("nuevo id"+nuevoInventario.getId());
				//getEntityManager().merge(nuevoInventario);
				
				/*getEntityManager().createNativeQuery("update inventario set cantidad_actual = cantidad_actual + :cant " +
						"where id = :id")
				.setParameter("cant", item.getCantidad())
				.setParameter("id", nuevoInventario.getId())
				.executeUpdate();*/
				
				//getEntityManager().flush();
				
				item.getInventario().setCantidadActual(item.getInventario().getCantidadActual()-item.getCantidad());
				//item.setInventario(getEntityManager().merge(item.getInventario()));
				//getEntityManager().merge(item);
				inventarioHome.setInstance(item.getInventario());
				inventarioHome.modify();
				
				/*getEntityManager().createQuery("update item set inventario_id = :idInventario " +
						"where inventario_id = :id and movimiento_id=:id2")
				.setParameter("idInventario", nuevoInventario.getId())
				.setParameter("id", item.getItemId().getInventarioId())
				.setParameter("id2", item.getItemId().getMovimientoId())
				.executeUpdate();*/
				
				
				/*getEntityManager().createNativeQuery("update inventario set cantidad_actual = cantidad_actual - :cant " +
						"where id = :id")
				.setParameter("cant", item.getCantidad())
				.setParameter("id", item.getInventario().getId())
				.executeUpdate();*/
				
			//	getEntityManager().flush();
				
				item.setInventario(nuevoInventario);
				item.getItemId().setInventarioId(nuevoInventario.getId());
				//item=getEntityManager().merge(item);
				//getEntityManager().merge(item);
				
				itemHome.setInstance(item);
				itemHome.modify();
				
				
				
			//	getEntityManager().flush();
				
				
				/*getEntityManager().get
				getEntityManager().flush();*/
				
				
			/*} catch (Exception e) {
				
				FacesMessages.instance().add(Severity.WARN,"Ocurrio un problema, intente de nuevo");
				System.out.println(e.getCause());
				return;

			}*/
			contador++;
		}
		
		instance.setSucursal(sucursalSelected);
		modify();
		
		//getEntityManager().getTransaction().commit();
		//getEntityManager().close();
		System.out.println("Contador"+contador);
		FacesMessages.instance().add(Severity.INFO,"Actualizado con exito");
		
	}
	
	public Inventario obtenerInventarioSucursal(Item item)
	{
		Inventario inv = (Inventario) getEntityManager()
				.createQuery(
						"SELECT i FROM Inventario i WHERE i.sucursal = :suc AND i.producto = :prd")
				.setParameter("suc", sucursalSelected)
				.setParameter("prd", item.getInventario().getProducto()).getSingleResult();
		
		return inv;
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		cargarCompras();
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		cargarCompras();
	}


	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}
	
	public void cargarSucursales(){
		if(empresaSeleccionada!=null)
			setSucursales(new ArrayList<Sucursal>(empresaSeleccionada.getSucursales()));
		else
			setSucursales(new ArrayList<Sucursal>());
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}
	
	public Integer getCompraId() {
		return compraId;
	}

	public void setCompraId(Integer compraId) {
		this.compraId = compraId;
	}

	public List<Item> getItemsAgregados() {
		return itemsAgregados;
	}

	public void setItemsAgregados(List<Item> itemsAgregados) {
		this.itemsAgregados = itemsAgregados;
	}

	public List<Inventario> getProductosAgregados() {
		return productosAgregados;
	}

	public void setProductosAgregados(List<Inventario> productosAgregados) {
		this.productosAgregados = productosAgregados;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
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

	public Map<String, ArrayList<CodProducto>> getLstCodsProductos() {
		return lstCodsProductos;
	}

	public void setLstCodsProductos(
			Map<String, ArrayList<CodProducto>> lstCodsProductos) {
		this.lstCodsProductos = lstCodsProductos;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public List<Compra> getResultList() {
		return resultList;
	}

	public void setResultList(List<Compra> resultList) {
		this.resultList = resultList;
	}

	public Sucursal getSucursalSelected() {
		return sucursalSelected;
	}

	public void setSucursalSelected(Sucursal sucursalSelected) {
		this.sucursalSelected = sucursalSelected;
	}
	
	

}
