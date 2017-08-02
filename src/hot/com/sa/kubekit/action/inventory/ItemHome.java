package com.sa.kubekit.action.inventory;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Item;

@Name("itemHome")
@Scope(ScopeType.CONVERSATION)
public class ItemHome extends KubeDAO<Item>{

	private static final long serialVersionUID = 1L;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("")));
	}
	
	public void modificarCantidadInventario()
	{
		System.out.println("EL TIPO DE MOVIMIENTO ES: " + instance.getMovimiento().getTipoMovimiento());
		System.out.println("VA DISMINUIR EL NUMERO DE ITEMS DEL INVENTARIO: " + instance.getInventario().getId());
		
		if(instance.getMovimiento().getTipoMovimiento().equals("E"))
		{
			getEntityManager().createQuery("update Inventario set cantidadActual = cantidadActual + :cant " +"where id = :id")
			.setParameter("cant", instance.getCantidad())
			.setParameter("id", instance.getInventario().getId())
			.executeUpdate();
			
		}
		else
		{
			System.out.println("VA A MODIFICAR LAS EXISTENCIA DEL INVENTARIO: " + instance.getInventario().getId());
			System.out.println("LA CANTIDAD DEL ITEM ES: " + instance.getCantidad());
			
			getEntityManager().createQuery("update Inventario set cantidadActual = cantidadActual - :cant " +"where id = :id")
					.setParameter("cant", instance.getCantidad())
					.setParameter("id", instance.getInventario().getId())
					.executeUpdate();
		}
		
		getEntityManager().refresh(instance.getInventario());
	}
	
	public void retornarItems(Integer cantidad)
	{
		getEntityManager().createQuery("update Inventario set cantidadActual = cantidadActual + :cant " +
				"where id = :id")
		.setParameter("cant", cantidad)
		.setParameter("id", instance.getInventario().getId())
		.executeUpdate();		
		getEntityManager().refresh(instance.getInventario());
	}
	
	public void disminuirItems(Integer cantidad)
	{
		getEntityManager().createQuery("update Inventario set cantidadActual = cantidadActual - :cant " +
				"where id = :id")
		.setParameter("cant", cantidad)
		.setParameter("id", instance.getInventario().getId())
		.executeUpdate();
		getEntityManager().refresh(instance.getInventario());
	}
	
	@Override
	public boolean preSave() {
		return true;
	}

	@Override
	public boolean preModify() {
		return true;
	}
	
	@Override
	public boolean preDelete() {
		return true;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

}
