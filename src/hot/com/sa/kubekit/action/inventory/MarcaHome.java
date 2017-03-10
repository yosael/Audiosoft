package com.sa.kubekit.action.inventory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Marca;

@Name("marcaHome")
@Scope(ScopeType.CONVERSATION)
public class MarcaHome extends KubeDAO<Marca>{

	private static final long serialVersionUID = 1L;
	private Integer marcaId;
	
	@In
	private LoginUser loginUser;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("marcaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("marcaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("marcaHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Marca.class, marcaId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new Marca());
			if(loginUser.getUser()!=null && loginUser.getUser().getSucursal()!=null){
				instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
			}
		}
	}

	@Override
	public boolean preSave() {
		if(instance.getEmpresa()==null){
			FacesMessages.instance().add(
					sainv_messages.get("marcaHome_error_save1"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		if(instance.getEmpresa()==null){
			FacesMessages.instance().add(
					sainv_messages.get("marcaHome_error_save1"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		if(instance.getProductos() == null || instance.getProductos().isEmpty()){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("marcaHome_error_delete1"));
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

	public Integer getMarcaId() {
		return marcaId;
	}

	public void setMarcaId(Integer marcaId) {
		this.marcaId = marcaId;
	}



}
