package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Inventario;
import com.sa.model.inventory.Item;
import com.sa.model.inventory.Movimiento;
import com.sa.model.inventory.id.ItemId;
import com.sa.model.security.Empresa;
import com.sa.model.security.Sucursal;

@Name("movimientoHome")
@Scope(ScopeType.CONVERSATION)
public class MovimientoHome extends KubeDAO<Movimiento>{

	private static final long serialVersionUID = 1L;
	
	@In
	private LoginUser loginUser;
	
	@In (create= true, required=false)
	private ItemHome itemHome;
	
	@In(required=false,create=true)
	@Out(required=false)
	private ProductoHome productoHome;
	
	private Empresa empresaSeleccionada;
	private List<Sucursal> sucursales = new ArrayList<Sucursal>();
	
	private String tipoMovimiento = "0";
	private String razon = "0";
	
	
	private List<Item> itemsAgregados = new ArrayList<Item>();
	private List<Inventario> productosAgregados = new ArrayList<Inventario>();
	private List<Movimiento> resultList = new ArrayList<Movimiento>();
	
	private Integer movimientoId;
	

	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("movimientoHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("movimientoHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("movimientoHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Movimiento.class, movimientoId));
			itemsAgregados = new ArrayList<Item>(instance.getItems());
			productosAgregados = new ArrayList<Inventario>();
			for(Item producto: instance.getItems()){
				productosAgregados.add(producto.getInventario());
			}
			empresaSeleccionada = instance.getSucursal().getEmpresa();
		}catch (Exception e) {
			clearInstance();
			setInstance(new Movimiento());
			if(loginUser.getUser()!=null){
				instance.setUsuario(loginUser.getUser());
				if(loginUser.getUser().getSucursal()!=null){
					instance.setSucursal(loginUser.getUser().getSucursal());
				}
			}
		}
		if(empresaSeleccionada!=null)
			sucursales = new ArrayList<Sucursal>(empresaSeleccionada.getSucursales());
	}
	
	public void cargarMovimientos() {
		String fltFch = " AND (:fch1 = :fch1 OR :fch2 = :fch2 OR 1 = 1) ";
		if(getFechaPFlt1() != null && getFechaPFlt2() != null) {
			setFechaPFlt1(truncDate(getFechaPFlt1(), false));
			setFechaPFlt2(truncDate(getFechaPFlt2(), true));
			fltFch = " AND e.fecha BETWEEN :fch1 AND :fch2 ";
		}
		
		if(loginUser.getUser().getSucursal()==null){
			resultList = getEntityManager().createQuery("select e from Movimiento e WHERE 1 = 1 " + fltFch + 
					"	order by e.fecha desc, e.tipoMovimiento, e.razon")
					.setParameter("fch1", getFechaPFlt1())
					.setParameter("fch2", getFechaPFlt2())
					.getResultList();
		}else{
			resultList = getEntityManager().createQuery("select e from Movimiento e " +
					"	where e.sucursal.id = " + loginUser.getUser().getSucursal().getId() + fltFch + 
					" order by e.fecha desc, e.tipoMovimiento, e.razon")
					.setParameter("fch1", getFechaPFlt1())
					.setParameter("fch2", getFechaPFlt2())
					.getResultList();
		}		
	}
	
	public void asignarSucursal(){
		this.productoHome.setSucursalSeleccionada(instance.getSucursal());
	}
	
	public void clearItems(){
		this.itemsAgregados = new ArrayList<Item>();
		this.productosAgregados = new ArrayList<Inventario>();
	}
	
	public void agregarProducto(Inventario producto){
		if(productosAgregados.contains(producto)){
			FacesMessages.instance().add(Severity.WARN,
				sainv_messages.get("movimientoHome_error_additem"));
			return;
		}
		
		Item item = new Item();
		item.setCantidad(1);
		item.setCostoUnitario(producto.getProducto().getPrcNormal());
		item.setInventario(producto);
		item.setItemId(new ItemId());
		item.getItemId().setInventarioId(producto.getId());
		itemsAgregados.add(item);
		productosAgregados.add(producto);
		
	}
	
	public void removerItem(Item item){
		itemsAgregados.remove(item);
		productosAgregados.remove(item.getInventario());
	}
	
	
	@Override
	public boolean preSave() 
	{
		for(Item item : itemsAgregados)
		{
			if(instance.getTipoMovimiento().equals("S"))
			{
				if(item.getCantidad()>item.getInventario().getCantidadActual())
				{
					FacesMessages.instance().add(Severity.WARN,
							sainv_messages.get("movimientoHome_error_save0"));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean preModify() {
		return true;
	}

	@Override
	public boolean preDelete() {
		for(Item item : instance.getItems()){
			System.out.print(item.getInventario().getProducto().getNombre());
			itemHome.setInstance(item);
			if(item.getMovimiento().getTipoMovimiento().equals("E")){
				System.out.print("Aumentando :" + itemHome.getInstance().getCantidad());
				if(item.getCantidad()>item.getInventario().getCantidadActual()){
					item.setCantidad(item.getInventario().getCantidadActual());
				}
				itemHome.disminuirItems(itemHome.getInstance().getCantidad());
			}
			else{
				System.out.print("Disminuyendo :" + itemHome.getInstance().getCantidad());
				itemHome.retornarItems(itemHome.getInstance().getCantidad());
			}
			itemHome.delete();
		}
		return true;
		
	}

	@Override
	public void posSave() {
		
		for(Item item: itemsAgregados){
			
			if(instance.getTipoMovimiento().equals("S")){
				if(item.getCantidad()>item.getInventario().getCantidadActual()){
					item.setCantidad(item.getInventario().getCantidadActual());
				}
			}
			//System.out.println("Id ITEM em POSAVE"+item.getItemId())
			item.getItemId().setMovimientoId(instance.getId());
			item.setMovimiento(instance);
			itemHome.setInstance(item);
			itemHome.modificarCantidadInventario();
			itemHome.save();
		}
		getEntityManager().flush();
		getEntityManager().refresh(instance);
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Empresa getEmpresaSeleccionada() {
		return empresaSeleccionada;
	}

	public void setEmpresaSeleccionada(Empresa empresaSeleccionada) {
		this.empresaSeleccionada = empresaSeleccionada;
	}

	public List<Sucursal> getSucursales() {
		return sucursales;
	}

	public void setSucursales(List<Sucursal> sucursales) {
		this.sucursales = sucursales;
	}
	
	public void cargarSucursales(){
		if(empresaSeleccionada!=null)
			setSucursales(new ArrayList<Sucursal>(empresaSeleccionada.getSucursales()));
		else
			setSucursales(new ArrayList<Sucursal>());
	}



	public String getRazon() {
		return razon;
	}

	public void setRazon(String razon) {
		this.razon = razon;
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
	

	public Integer getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Integer movimientoId) {
		this.movimientoId = movimientoId;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public List<Movimiento> getResultList() {
		return resultList;
	}

	public void setResultList(List<Movimiento> resultList) {
		this.resultList = resultList;
	}

	
}
