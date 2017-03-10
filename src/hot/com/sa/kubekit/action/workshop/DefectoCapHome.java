package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.workshop.DefectoCapsula;

@Name("defectoCapHome")
@Scope(ScopeType.CONVERSATION)
public class DefectoCapHome extends KubeDAO<DefectoCapsula>{

	private static final long serialVersionUID = 1L;
	private Integer dfcpId;
	private List<DefectoCapsula> resultList = new ArrayList<DefectoCapsula>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("defcap_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("defcap_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("defcap_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(DefectoCapsula.class, dfcpId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new DefectoCapsula());
		}
	}
	
	public void getDefectosList() {
		resultList = getEntityManager().createQuery("SELECT d FROM DefectoCapsula d ORDER BY d.nombre ASC ").getResultList();
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<DefectoCapsula> coinList = getEntityManager()
				.createQuery("SELECT d FROM DefectoCapsula d " +
						"	WHERE UPPER(d.nombre) = UPPER(:rec) ")
				.setParameter("rec", instance.getNombre())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("defcap_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<DefectoCapsula> coinList = getEntityManager()
				.createQuery("SELECT d FROM DefectoCapsula d " +
						"	WHERE UPPER(d.nombre) = UPPER(:dia) AND d.id <> :idD")
				.setParameter("dia", instance.getNombre())
				.setParameter("idD", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("defcap_name_dupl"));
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
		// TODO Auto-generated method stub
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
	}

	public List<DefectoCapsula> getResultList() {
		return resultList;
	}

	public void setResultList(List<DefectoCapsula> resultList) {
		this.resultList = resultList;
	}

	public Integer getDfcpId() {
		return dfcpId;
	}

	public void setDfcpId(Integer dfcpId) {
		this.dfcpId = dfcpId;
	}



}
