package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.ClaseProducto;
import com.sa.model.inventory.Producto;

@Name("claseProductoHome")
@Scope(ScopeType.CONVERSATION)
public class ClaseProductoHome extends KubeDAO<ClaseProducto>{

	private static final long serialVersionUID = 1L;
	private Integer claseProdId;
	private List<ClaseProducto> resultList = new ArrayList<ClaseProducto>();
	
	@In
	private LoginUser loginUser;
	
	@In(required=false, create=true)
	private ProductoHome productoHome;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("clsprd_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("clsprd_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("clsprd_deleted")));
		getClasesProductoList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ClaseProducto.class, claseProdId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ClaseProducto());
			if(loginUser.getUser().getSucursal() != null)
				instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
		}
	}
	
	
	public void getClasesProductoList() {
		resultList = getEntityManager().createQuery("SELECT c FROM ClaseProducto c ORDER BY c.nombre ASC").getResultList();
	}

	@Override
	public boolean preSave() {
		/*if(instance.getMinVal() != null && instance.getMinVal() > instance.getMaxVal()){
			FacesMessages.instance().add(
					sainv_messages.get("clsprd_error_minGreat"));
			return false;
		}*/
		
		return true;
	}

	@Override
	public boolean preModify() {
		
		return true;
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posModify() {
		//Actualizamos los montos de todos los productos en base a la nueva generacion de porcentajes
		List<Producto> lstPrds = getEntityManager()
				.createQuery("SELECT p FROM Producto p ")
				.getResultList();
		for(Producto tmpPrd : lstPrds) {
			productoHome.actualizarMontoPrd(tmpPrd);
			getEntityManager().merge(tmpPrd);
		}
		getEntityManager().flush();
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<ClaseProducto> getResultList() {
		return resultList;
	}

	public void setResultList(List<ClaseProducto> resultList) {
		this.resultList = resultList;
	}

	public Integer getClaseProdId() {
		return claseProdId;
	}

	public void setClaseProdId(Integer claseProdId) {
		this.claseProdId = claseProdId;
	}

	

}
