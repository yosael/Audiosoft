package com.sa.kubekit.action.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.security.Empresa;

@Name("empresaHome")
@Scope(ScopeType.CONVERSATION)
public class EmpresaHome extends KubeDAO<Empresa>{

	private static final long serialVersionUID = 1L;
	private String empresaId;
	private String estado;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("empresaHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("empresaHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("empresaHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance((Empresa) getEntityManager().createQuery("select e from Empresa e where e.codigo like :code")
					.setParameter("code", empresaId).getSingleResult());
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
		if((instance.getSucursales()==null || instance.getSucursales().isEmpty()) 
				&& (instance.getMarcas()==null || instance.getMarcas().isEmpty()) 
				&& (instance.getUnidadesMedida()==null || instance.getUnidadesMedida().isEmpty()) 
				&& (instance.getCategorias()==null || instance.getCategorias().isEmpty()) 
				&& (instance.getProductos()==null || instance.getProductos().isEmpty())){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("empresaHome_error_delete1"));
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

	public String getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(String empresaId) {
		this.empresaId = empresaId;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
