package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.ClienteCorporativo;

@Name("clienteCorpHome")
@Scope(ScopeType.CONVERSATION)
public class ClienteCorpHome extends KubeDAO<ClienteCorporativo>{

	private static final long serialVersionUID = 1L;
	private Integer cliId;
	private List<ClienteCorporativo> resultList = new ArrayList<ClienteCorporativo>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("clicorp_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("clicorp_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("clicorp_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ClienteCorporativo.class, cliId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ClienteCorporativo());
		}
	}
	
	public void getClientesCorp() {
		resultList = getEntityManager().createQuery("SELECT c FROM ClienteCorporativo c").getResultList();
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<ClienteCorporativo> coinList = getEntityManager()
				.createQuery("SELECT c FROM ClienteCorporativo c " +
						"	WHERE UPPER(c.nombre) = UPPER(:cli)")
				.setParameter("cli", instance.getNombre())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("clicorp_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<ClienteCorporativo> coinList = getEntityManager()
				.createQuery("SELECT c FROM ClienteCorporativo c " +
						"	WHERE UPPER(c.nombre) = UPPER(:dia) AND c.id <> :idC")
				.setParameter("dia", instance.getNombre())
				.setParameter("idC", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("clicorp_name_dupl"));
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

	public List<ClienteCorporativo> getResultList() {
		return resultList;
	}

	public void setResultList(List<ClienteCorporativo> resultList) {
		this.resultList = resultList;
	}

	public Integer getCliId() {
		return cliId;
	}

	public void setCliId(Integer cliId) {
		this.cliId = cliId;
	}

}
