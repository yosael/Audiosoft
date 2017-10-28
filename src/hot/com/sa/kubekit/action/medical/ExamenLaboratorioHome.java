package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.ExamenLaboratorio;

@Name("examenLaboratorioHome")
@Scope(ScopeType.CONVERSATION)
public class ExamenLaboratorioHome extends KubeDAO<ExamenLaboratorio> {
	
	private Integer examId;
	private List<ExamenLaboratorio> resultList = new ArrayList<ExamenLaboratorio>();
	
	private String nomCoinci = "";

	
	@Override
	public void create() {
		
		setCreatedMessage(createValueExpression("El examen se ha registrado satisfactoriamente"));
		setUpdatedMessage(createValueExpression("El examen se ha modificado satisfactoriamente"));
		setDeletedMessage(createValueExpression("El examen ha sido eliminado"));
	}
	
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ExamenLaboratorio.class, examId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ExamenLaboratorio());
		}
	}
	
	public void cargarExamenes()
	{
		resultList = getEntityManager().createQuery("SELECT e FROM ExamenLaboratorio e").getResultList();
	}
	
	
	public void buscarExamenPorNombre()
	{
		if(nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT e FROM ExamenLaboratorio e where UPPER(e.nombre) like :busqueda or UPPER(e.codigo) like :busqueda or UPPER(e.categoria) like :busqueda")
					.setParameter("busqueda", "%"+nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
	}
	
	
	@Override
	public boolean preSave() {
		
		if(!validarInstancia())
			return false;
		
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	public boolean validarInstancia()
	{
		
		if(instance.getNombre()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Debe agregar el nombre");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean preDelete() {
		// TODO Auto-generated method stub
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

	public Integer getExamId() {
		return examId;
	}

	public void setExamId(Integer examId) {
		this.examId = examId;
	}

	public List<ExamenLaboratorio> getResultList() {
		return resultList;
	}

	public void setResultList(List<ExamenLaboratorio> resultList) {
		this.resultList = resultList;
	}


	public String getNomCoinci() {
		return nomCoinci;
	}


	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}
	
	
	

}
