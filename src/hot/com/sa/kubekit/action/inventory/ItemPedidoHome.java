package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.ItemPedido;

@Name("itemPedidoHome")
@Scope(ScopeType.CONVERSATION)
public class ItemPedidoHome extends KubeDAO<ItemPedido>{

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
