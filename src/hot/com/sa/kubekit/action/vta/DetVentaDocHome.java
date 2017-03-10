package com.sa.kubekit.action.vta;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.vta.DetVentaDoc;

@Name("detVentaDocHome")  
@Scope(ScopeType.CONVERSATION) 
public class DetVentaDocHome extends KubeDAO<DetVentaDoc>{

	private static final long serialVersionUID = 1L;
	private Integer dtvtadocId; 
	private List<DetVentaDoc> resultlist = new ArrayList<DetVentaDoc>();
	
	@In
	private LoginUser loginUser;	
	
	@Override
	public void create() { 
		setCreatedMessage(createValueExpression(sainv_messages
				.get("detVentaDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("detVentaDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("detVentaDocHome_deleted")));
	}
		
	@SuppressWarnings("unchecked") 
	public void cargarListaClientes(){
		resultlist = getEntityManager().createQuery("select c from DetVentaDoc c ")
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(DetVentaDoc.class, dtvtadocId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new DetVentaDoc());
		}
	}

	@Override
	public boolean preSave() {
		System.out.println("Guardando");
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

	public Integer getDtvtadocId() {
		return dtvtadocId;
	}

	public void setDtvtadocId(Integer dtvtadocId) {
		this.dtvtadocId = dtvtadocId;
	}

	public List<DetVentaDoc> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<DetVentaDoc> resultlist) {
		this.resultlist = resultlist;
	}	
}
