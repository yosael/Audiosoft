package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.DiagnosticoMed;

@Name("diagnosticoMedHome")
@Scope(ScopeType.CONVERSATION)
public class DiagnosticoMedHome extends KubeDAO<DiagnosticoMed>{

	private static final long serialVersionUID = 1L;
	private Integer diagId;
	private List<DiagnosticoMed> resultList = new ArrayList<DiagnosticoMed>();
	private String nomCoinci="";
	private boolean cerrarMod;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("diagnos_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("diagnos_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("diagnos_deleted")));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(DiagnosticoMed.class, diagId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new DiagnosticoMed());
		}
	}
	
	public void getDiagnostList() {
		resultList = getEntityManager().createQuery("SELECT d FROM DiagnosticoMed d ORDER BY d.codigo ASC ").getResultList();
	}
	
	
	public void getDiagnostListByName() {
		//Conversation.instance().begin();
		
		resultList = getEntityManager().createQuery("SELECT d FROM DiagnosticoMed d WHERE (UPPER(d.nombre) LIKE :nom) ORDER BY d.codigo ASC ")
				.setParameter("nom", "%"+this.nomCoinci.toUpperCase() + "%")
				.getResultList();
		
	}
	
	public void iniciarNuevoDiagnostico()
	{
		setInstance(new DiagnosticoMed());
	}

	

	@Override
	public boolean preSave() {
		
		if(instance.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el codigo");
			return false;
		}
		
		if(instance.getNombre()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el nombre");
			return false;
		}
		
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		
		instance.setNombre(instance.getNombre().toUpperCase());
		
	
		
		//Verificamos que no se repita
		List<DiagnosticoMed> coinList = getEntityManager()
				.createQuery("SELECT d FROM DiagnosticoMed d " +
						"	WHERE UPPER(d.nombre) = UPPER(:rec) ")
				.setParameter("rec", instance.getNombre())
				.getResultList();
		
		//System.out.println("Ingreso al diagnostico");
		
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("diagnos_name_dupl"));
			return false;
		}
		return true;
	}

	@Override
	public boolean preModify() {
		
		if(instance.getCodigo()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el codigo");
			return false;
		}
		
		if(instance.getNombre()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingresar el nombre");
			return false;
		}
		
		instance.setNombre(instance.getNombre().replaceAll("  ", " "));
		
		instance.setNombre(instance.getNombre());
		
		//Verificamos que no se repita
		List<DiagnosticoMed> coinList = getEntityManager()
				.createQuery("SELECT d FROM DiagnosticoMed d " +
						"	WHERE UPPER(d.nombre) = UPPER(:dia) AND d.id <> :idD")
				.setParameter("dia", instance.getNombre())
				.setParameter("idD", instance.getId())
				.getResultList();
		if(coinList != null && coinList.size() > 0) {
			FacesMessages.instance().add(
					sainv_messages.get("diagnos_name_dupl"));
			return false;
		}
		return true;
	}
	
	
	public void registrarNuevoDiagnosticoModal()
	{
		cerrarMod = false;
		
		if(save())
		{
			cerrarMod = true;
			System.out.println("Cerrar modal true");
		}
		else
		{
			return;
		}
	}
	

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		// TODO Auto-generated method stub
		
		System.out.println("Posave Diagnostico");
	}

	@Override
	public void posModify() {
		// TODO Auto-generated method stub
	}

	@Override
	public void posDelete() {
		// TODO Auto-generated method stub
	}

	public List<DiagnosticoMed> getResultList() {
		return resultList;
	}

	public void setResultList(List<DiagnosticoMed> resultList) {
		this.resultList = resultList;
	}

	public Integer getDiagId() {
		return diagId;
	}

	public void setDiagId(Integer diagId) {
		this.diagId = diagId;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}

	public boolean isCerrarMod() {
		return cerrarMod;
	}

	public void setCerrarMod(boolean cerrarMod) {
		this.cerrarMod = cerrarMod;
	}
	
	
	

}
