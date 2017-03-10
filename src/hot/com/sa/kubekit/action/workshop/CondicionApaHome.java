package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.workshop.CondicionAparato;

@Name("condicionApaHome")
@Scope(ScopeType.CONVERSATION)
public class CondicionApaHome extends KubeDAO<CondicionAparato>{

	private static final long serialVersionUID = 1L;
	private Integer cndapId;
	private List<CondicionAparato> resultList = new ArrayList<CondicionAparato>();
	
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
			setInstance(getEntityManager().find(CondicionAparato.class, cndapId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new CondicionAparato());
		}
	}
	
	public void getCondicionesList() {
		resultList = getEntityManager().createQuery("SELECT d FROM CondicionAparato d ORDER BY d.nombre ASC ").getResultList();
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<CondicionAparato> coinList = getEntityManager()
				.createQuery("SELECT d FROM CondicionAparato d " +
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
		List<CondicionAparato> coinList = getEntityManager()
				.createQuery("SELECT d FROM CondicionAparato d " +
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

	public List<CondicionAparato> getResultList() {
		return resultList;
	}

	public void setResultList(List<CondicionAparato> resultList) {
		this.resultList = resultList;
	}

	public Integer getCndapId() {
		return cndapId;
	}

	public void setCndapId(Integer cndapId) {
		this.cndapId = cndapId;
	}



}
