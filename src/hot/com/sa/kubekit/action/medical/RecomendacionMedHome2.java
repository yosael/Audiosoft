package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.RecomendacionMed;

@Name("recomendacionMedHome2")
@Scope(ScopeType.CONVERSATION)
public class RecomendacionMedHome2 extends KubeDAO<RecomendacionMed>{

	private static final long serialVersionUID = 1L;
	private Integer recmId;
	private List<RecomendacionMed> resultList = new ArrayList<RecomendacionMed>();
	private String nomCoinci;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("recomed_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("recomed_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("recomed_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(RecomendacionMed.class, recmId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new RecomendacionMed());
		}
	}
	
	public void getRecomenList() {
		resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r").getResultList();
	}
	
	public void getRecomenListByName() {
		
		resultList = getEntityManager().createQuery("SELECT r FROM RecomendacionMed r where UPPER(r.nombre) like :nom ")
					.setParameter("nom","%"+this.nomCoinci.toUpperCase()+"%")
					.getResultList();
		
	}

	@Override
	public boolean preSave() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<RecomendacionMed> coinList = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r WHERE UPPER(r.nombre) = UPPER(:rec)")
				.setParameter("rec", instance.getNombre())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("recomed_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		//Verificamos que no se repita
		List<RecomendacionMed> coinList = getEntityManager()
				.createQuery("SELECT r FROM RecomendacionMed r " +
						"	WHERE UPPER(r.nombre) = UPPER(:rec) AND r.id <> :idR")
				.setParameter("rec", instance.getNombre())
				.setParameter("idR", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("recomed_name_dupl"));
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

	public List<RecomendacionMed> getResultList() {
		return resultList;
	}

	public void setResultList(List<RecomendacionMed> resultList) {
		this.resultList = resultList;
	}

	public Integer getRecmId() {
		return recmId;
	}

	public void setRecmId(Integer recmId) {
		this.recmId = recmId;
	}
	

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}
	
	

}
