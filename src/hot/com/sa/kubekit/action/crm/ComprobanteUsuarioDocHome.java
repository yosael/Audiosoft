package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.ComprobanteUsuarioDoc;

@Name("comprobanteUsuarioDocHome")
@Scope(ScopeType.CONVERSATION)
public class ComprobanteUsuarioDocHome extends KubeDAO<ComprobanteUsuarioDoc> {

	private static final long serialVersionUID = 1L;
	private Integer cpusrdcId;

	private List<ComprobanteUsuarioDoc> resultlist = new ArrayList<ComprobanteUsuarioDoc>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("comprobanteUsuarioDoc_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("comprobanteUsuarioDoc_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("comprobanteUsuarioDoc_deleted")));
	}
		
	@SuppressWarnings("unchecked")
	public void cargarListaComprobanteUsuarioDocs(){
		resultlist = getEntityManager().createQuery("select c from ComprobanteUsuarioDoc c ")
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ComprobanteUsuarioDoc.class, cpusrdcId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ComprobanteUsuarioDoc());
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

	public Integer getCpusrdcId() {
		return cpusrdcId;
	}

	public void setCpusrdcId(Integer cpusrdcId) {
		this.cpusrdcId = cpusrdcId;
	}

	public List<ComprobanteUsuarioDoc> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<ComprobanteUsuarioDoc> resultlist) {
		this.resultlist = resultlist;
	}	
}
