package com.sa.kubekit.action.inventory;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.inventory.Currency;
import com.sa.model.inventory.Producto;

@Name("currencyHome")
@Scope(ScopeType.CONVERSATION)
public class CurrencyHome extends KubeDAO<Currency>{

	private static final long serialVersionUID = 1L;
	private Integer currencyId;
	private List<Currency> resultList = new ArrayList<Currency>();
	
	@In(required=false, create=true)
	private ProductoHome productoHome;
	
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
			setInstance(getEntityManager().find(Currency.class, currencyId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new Currency());
		}
	}
	
	public void getMonedasList() {
		resultList = getEntityManager().createQuery("SELECT c FROM Currency c").getResultList();
	}

	@Override
	public boolean preSave() {
		if(instance.getMinVal() != null && instance.getMinVal() > instance.getMaxVal()){
			FacesMessages.instance().add(
					sainv_messages.get("currency_error_minGreat"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		if(instance.getMinVal() != null && instance.getMinVal() > instance.getMaxVal()){
			FacesMessages.instance().add(
					sainv_messages.get("currency_error_minGreat"));
			return false;
		}
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
			if(tmpPrd.getMoneda().getId().equals(instance.getId())) {
				productoHome.actualizarMontoPrd(tmpPrd);
				getEntityManager().merge(tmpPrd);
				
			}
		}
		getEntityManager().flush();
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
		
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public List<Currency> getResultList() {
		return resultList;
	}

	public void setResultList(List<Currency> resultList) {
		this.resultList = resultList;
	}



}
