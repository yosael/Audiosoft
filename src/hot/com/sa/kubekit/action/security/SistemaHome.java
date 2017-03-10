package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Sistema;

@Name("sistemaHome")
@Scope(ScopeType.CONVERSATION)
public class SistemaHome extends KubeDAO<Sistema>{

	private static final long serialVersionUID = 1L;
	private String sistemaId;
	private String estado;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("sistemaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("sistemaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("sistemaHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance((Sistema) getEntityManager().createQuery("select s from Sistema s where s.codigo like :code")
					.setParameter("code", sistemaId).getSingleResult());
			estado = instance.getEstado();
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
		instance.setEstado(estado);
		return true;
	}

	@Override
	public boolean preDelete() {
		if((instance.getMenus()==null || instance.getMenus().isEmpty()) 
				&& (instance.getRoles()==null || instance.getRoles().isEmpty())){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("sistemaHome_error_delete1"));
			return false;
		}
		
	}

	@Override
	public void posSave() {
	}

	@Override
	public void posModify() {
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public String getSistemaId() {
		return sistemaId;
	}

	public void setSistemaId(String sistemaId) {
		this.sistemaId = sistemaId;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
