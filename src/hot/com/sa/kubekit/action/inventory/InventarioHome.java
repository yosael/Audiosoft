package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Inventario;

@Name("inventarioHome")
@Scope(ScopeType.CONVERSATION)
public class InventarioHome extends KubeDAO<Inventario>{

	private static final long serialVersionUID = 1L;
	private Integer inventarioId;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(""));
		setUpdatedMessage(createValueExpression(""));
		setDeletedMessage(createValueExpression(""));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Inventario.class, inventarioId));
		}catch (Exception e) {
			clearInstance();
		}
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
		
		if((instance.getItems()==null || instance.getItems().isEmpty()) 
				&& (instance.getItemsPedidos()==null || instance.getItemsPedidos().isEmpty())){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("sucursalHome_error_delete1"));
			return false;
		}
		
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

	public Integer getInventarioId() {
		return inventarioId;
	}

	public void setInventarioId(Integer inventarioId) {
		this.inventarioId = inventarioId;
	}


}
