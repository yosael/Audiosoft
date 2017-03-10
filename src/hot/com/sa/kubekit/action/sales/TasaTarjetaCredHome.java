package com.sa.kubekit.action.sales;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.TasaTarjetaCred;

@Name("tasaTarjetaCredHome")
@Scope(ScopeType.CONVERSATION)
public class TasaTarjetaCredHome extends KubeDAO<TasaTarjetaCred>{

	private static final long serialVersionUID = 1L;
	private Integer tstId;
	private List<TasaTarjetaCred> resultList = new ArrayList<TasaTarjetaCred>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("tasaTarjetaCredHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("tasaTarjetaCredHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("tasaTarjetaCredHome_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(TasaTarjetaCred.class, tstId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new TasaTarjetaCred());
		}
	}
	
	public void getTasasList() {
		resultList = getEntityManager().createQuery("SELECT d FROM TasaTarjetaCred d ORDER BY d.nombre ASC ").getResultList();
	}
	
	public void getTasasCmb() {
		resultList = getEntityManager().createQuery("SELECT d FROM TasaTarjetaCred d ORDER BY d.porcentaje ASC ").getResultList();
		
		System.out.println("Tammmmm Targetas ******** "+resultList.size());
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<TasaTarjetaCred> coinList = getEntityManager()
				.createQuery("SELECT d FROM TasaTarjetaCred d " +
						"	WHERE UPPER(d.nombre) = UPPER(:nom) ")
				.setParameter("nom", instance.getNombre())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("tstrjcrd_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<TasaTarjetaCred> coinList = getEntityManager()
				.createQuery("SELECT d FROM TasaTarjetaCred d " +
						"	WHERE UPPER(d.nombre) = UPPER(:nom) AND d.id <> :idD")
				.setParameter("nom", instance.getNombre())
				.setParameter("idD", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("tstrjcrd_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preDelete() {
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

	public List<TasaTarjetaCred> getResultList() {
		return resultList;
	}

	public void setResultList(List<TasaTarjetaCred> resultList) {
		this.resultList = resultList;
	}

	public Integer getTstId() {
		return tstId;
	}

	public void setTstId(Integer tstId) {
		this.tstId = tstId;
	}

}
