package com.sa.kubekit.action.workshop;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.workshop.ComponenteAparato;

@Name("componenteDefHome")
@Scope(ScopeType.CONVERSATION)
public class ComponenteDefHome extends KubeDAO<ComponenteAparato>{

	private static final long serialVersionUID = 1L;
	private Integer cmpdId;
	private List<ComponenteAparato> resultList = new ArrayList<ComponenteAparato>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("comdef_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("comdef_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("comdef_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ComponenteAparato.class, cmpdId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ComponenteAparato());
		}
	}
	
	public void getComponentesList() {
		resultList = getEntityManager().createQuery("SELECT d FROM ComponenteAparato d ORDER BY d.nombre ASC ").getResultList();
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<ComponenteAparato> coinList = getEntityManager()
				.createQuery("SELECT d FROM ComponenteAparato d " +
						"	WHERE UPPER(d.nombre) = UPPER(:rec) ")
				.setParameter("rec", instance.getNombre())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("comdef_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<ComponenteAparato> coinList = getEntityManager()
				.createQuery("SELECT d FROM ComponenteAparato d " +
						"	WHERE UPPER(d.nombre) = UPPER(:dia) AND d.id <> :idD")
				.setParameter("dia", instance.getNombre())
				.setParameter("idD", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("comdef_name_dupl"));
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

	public List<ComponenteAparato> getResultList() {
		return resultList;
	}

	public void setResultList(List<ComponenteAparato> resultList) {
		this.resultList = resultList;
	}

	public Integer getCmpdId() {
		return cmpdId;
	}

	public void setCmpdId(Integer cmpdId) {
		this.cmpdId = cmpdId;
	}



}
