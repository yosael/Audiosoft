package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.ProveedorDoc;
  
@Name("proveedorDocHome")  
@Scope(ScopeType.CONVERSATION)
public class ProveedorDocHome extends KubeDAO<ProveedorDoc> {

	private static final long serialVersionUID = 1L;
	private Integer provDocId;

	private List<ProveedorDoc> proveedoresDoc = new ArrayList<ProveedorDoc>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("proveedorDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("proveedorDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("proveedorDocHome_deleted")));
	}
		
	@SuppressWarnings("unchecked")
	public void cargarListaProveedores(){
		proveedoresDoc = getEntityManager().createQuery("select c from ProveedorDoc c ")
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ProveedorDoc.class, provDocId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ProveedorDoc());
		} 
	}

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
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

	public Integer getProvDocId() {
		return provDocId;
	}

	public void setProvDocId(Integer provDocId) {
		this.provDocId = provDocId;
	}

	public List<ProveedorDoc> getProveedoresDoc() {
		return proveedoresDoc;
	}

	public void setProveedoresDoc(List<ProveedorDoc> proveedoresDoc) {
		this.proveedoresDoc = proveedoresDoc;
	}
}
