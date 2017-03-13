package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.crm.PaisHome;
import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Proveedor;

@Name("proveedorHome")
@Scope(ScopeType.CONVERSATION)
public class ProveedorHome extends KubeDAO<Proveedor>{

	private static final long serialVersionUID = 1L;
	private Integer proveedorId;
	private List<Proveedor> resultList;
	
	@In
	private LoginUser loginUser;
	
	@In(required=false, create=true)
	private PaisHome paisHome;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("proveedorHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("proveedorHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("proveedorHome_deleted")));
	}
	
	public void load(){
		try{
			paisHome.loadResultList();
			setInstance(getEntityManager().find(Proveedor.class, proveedorId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new Proveedor());
			if(loginUser.getUser()!=null && loginUser.getUser().getSucursal()!=null){
				instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
			}
		}
	}
	
	public void cargarProveedores()
	{
		resultList = new ArrayList<Proveedor>();
		
		resultList = getEntityManager().createQuery("SELECT p FROM Proveedor p").getResultList();
	}
	
	public void seleccionarProveedor(Proveedor proveedor)
	{
		setInstance(proveedor);
	}

	@Override
	public boolean preSave() {
		if(instance.getEmpresa()==null){
			FacesMessages.instance().add(
					sainv_messages.get("proveedorHome_error_save1"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		if(instance.getEmpresa()==null){
			FacesMessages.instance().add(
					sainv_messages.get("proveedorHome_error_save1"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
		if(instance.getProductos()==null ){
			return true;
		}else{
			FacesMessages.instance().add(
					sainv_messages.get("proveedorHome_error_delete1"));
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

	public Integer getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Integer proveedorId) {
		this.proveedorId = proveedorId;
	}

	public List<Proveedor> getResultList() {
		return resultList;
	}

	public void setResultList(List<Proveedor> resultList) {
		this.resultList = resultList;
	}
	
	
	
}
