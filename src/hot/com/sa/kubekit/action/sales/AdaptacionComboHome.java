package com.sa.kubekit.action.sales;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Categoria;
import com.sa.model.inventory.Producto;
import com.sa.model.sales.AdaptacionCombo;
import com.sa.model.sales.ItemAdaptacion;
import com.sa.model.sales.Service;


@Name("adaptacionComboHome")
@Scope(ScopeType.CONVERSATION)
public class AdaptacionComboHome extends KubeDAO<AdaptacionCombo> {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private List<AdaptacionCombo> resultList;
	private List<ItemAdaptacion> itemsAdaptacion = new ArrayList<ItemAdaptacion>();
	private Integer idAdaptacion;
	private String filterEstado;
	private String nomCoinci;
	
	
	@In(required = false, create = true)
	@Out
	private ItemAdaptacionHome itemAdaptacionHome;
	
	public void buscarTodos()
	{
		resultList = new ArrayList<AdaptacionCombo>();
		resultList = getEntityManager().createQuery("SELECT a FROM AdaptacionCombo a").getResultList();
	}
	
	public void buscarTodosActivos()
	{
		resultList = new ArrayList<AdaptacionCombo>();
		resultList = getEntityManager().createQuery("SELECT a FROM AdaptacionCombo a where a.estado='ACT'").getResultList();
	}
	
	public void cargar()
	{
		
		if(idAdaptacion!=null)
		{
			try
			{
				setInstance(getEntityManager().find(AdaptacionCombo.class, idAdaptacion));
				
				itemsAdaptacion = instance.getItemsAdaptacion();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			setInstance(new AdaptacionCombo());
			itemsAdaptacion = new ArrayList<ItemAdaptacion>();
		}
		
	}
	
	public void agregarProductoItemAdaptacion(Producto producto)
	{
		
		
		if(producto.getCategoria().isTieneNumLote() || producto.getCategoria().isTieneNumSerie())
		{
			FacesMessages.instance().add(Severity.WARN,"El item no debe ser un aparato");
			return;
		}
		
		
		ItemAdaptacion itemAdaptacion = new ItemAdaptacion();
		
		itemAdaptacion.setAdaptacion(instance);
		itemAdaptacion.setCategoria(producto.getCategoria());
		itemAdaptacion.setProducto(producto);
		
		itemsAdaptacion.add(itemAdaptacion);
	}
	
	public void agregarCategoriaItemAdaptacion(Categoria categoria)
	{
		
		if(categoria.isTieneNumLote() || categoria.isTieneNumSerie())
		{
			FacesMessages.instance().add(Severity.WARN,"El item no debe ser un aparato");
			return;
		}
		
		ItemAdaptacion itemAdaptacion = new ItemAdaptacion();
		
		itemAdaptacion.setAdaptacion(instance);
		itemAdaptacion.setCategoria(categoria);	
		
		itemsAdaptacion.add(itemAdaptacion);
	}
	
	public void agregarServicioItemAdaptacion(Service servicio)
	{
		ItemAdaptacion itemAdaptacion = new ItemAdaptacion();
		
		itemAdaptacion.setAdaptacion(instance);
		itemAdaptacion.setServicio(servicio);	
		
		itemsAdaptacion.add(itemAdaptacion);
	}
	
	public void quitarItem(ItemAdaptacion item)
	{
		if(item.getId()==null)
		{
			itemsAdaptacion.remove(item);
		}
		else
		{
			itemsAdaptacion.remove(item);
			getEntityManager().remove(item);
			
		}
	}
	
	
	public void buscarAdaptacionCombos()
	{
		//TODO: metodo para buscar adaptaciones por nombres
	}
	
	
	

	@Override
	public boolean preSave() {

		
		if(instance.getNombre()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar el nombre");
			return false;
		}
		
		if(itemsAdaptacion.size()<=0)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar un item a la adaptacion");
		}
		
		instance.setEstado("ACT");
		
		
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
		
		for(ItemAdaptacion item: itemsAdaptacion)
		{
			itemAdaptacionHome.setInstance(item);
			
			if(!itemAdaptacionHome.save())
			{
				return;
			}
		}
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
		for(ItemAdaptacion item: itemsAdaptacion)
		{
			if(item.getId()==null)
			{
				itemAdaptacionHome.setInstance(item);
				
				if(!itemAdaptacionHome.save())
				{
					return;
				}
			}
			else
			{
				itemAdaptacionHome.setInstance(item);
				
				if(!itemAdaptacionHome.modify())
				{
					return;
				}
			}
		}
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}



	public List<AdaptacionCombo> getResultList() {
		return resultList;
	}



	public void setResultList(List<AdaptacionCombo> resultList) {
		this.resultList = resultList;
	}



	public List<ItemAdaptacion> getItemsAdaptacion() {
		return itemsAdaptacion;
	}



	public void setItemsAdaptacion(List<ItemAdaptacion> itemsAdaptacion) {
		this.itemsAdaptacion = itemsAdaptacion;
	}

	public Integer getIdAdaptacion() {
		return idAdaptacion;
	}

	public void setIdAdaptacion(Integer idAdaptacion) {
		this.idAdaptacion = idAdaptacion;
	}

	public String getFilterEstado() {
		return filterEstado;
	}

	public void setFilterEstado(String filterEstado) {
		this.filterEstado = filterEstado;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}
	
	
	
	
	
	

}
