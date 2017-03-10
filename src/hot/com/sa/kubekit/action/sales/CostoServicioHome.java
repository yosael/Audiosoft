package com.sa.kubekit.action.sales;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.CostoServicio;

@Name("costoServicioHome")
@Scope(ScopeType.CONVERSATION)
public class CostoServicioHome extends KubeDAO<CostoServicio>{

	private static final long serialVersionUID = 1L;
	private Integer cssId;
	private List<CostoServicio> resultList = new ArrayList<CostoServicio>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("currency_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("currency_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("currency_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(CostoServicio.class, cssId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new CostoServicio());
		}
	}
	
	public void getCostosList() {
		resultList = getEntityManager().createQuery("SELECT c FROM CostoServicio c").getResultList();
	}

	@Override
	public boolean preSave() {
		
		return true;
	}

	@Override
	public boolean preModify() {
		/*if(instance.getMinVal() != null && instance.getMinVal() > instance.getMaxVal()){
			FacesMessages.instance().add(
					sainv_messages.get("currency_error_minGreat"));
			return false;
		}*/
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public List<CostoServicio> getResultList() {
		return resultList;
	}

	public void setResultList(List<CostoServicio> resultList) {
		this.resultList = resultList;
	}

	public Integer getCssId() {
		return cssId;
	}

	public void setCssId(Integer cssId) {
		this.cssId = cssId;
	}

}
