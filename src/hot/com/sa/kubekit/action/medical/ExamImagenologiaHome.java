package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.ExamImagenologia;

@Name("examImagenologiaHome")
@Scope(ScopeType.CONVERSATION)
public class ExamImagenologiaHome extends KubeDAO<ExamImagenologia> {
	
	private Integer examId;
	private List<ExamImagenologia> resultList = new ArrayList<ExamImagenologia>();
	
	private String nomCoinci = "";
	
	@Override
	public void create() {
		
		setCreatedMessage(createValueExpression("El examen se ha registrado satisfactoriamente"));
		setUpdatedMessage(createValueExpression("El examen se ha modificado satisfactoriamente"));
		setDeletedMessage(createValueExpression("El examen ha sido eliminado"));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ExamImagenologia.class, examId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ExamImagenologia());
		}
	}
	
	public void cargarExamenes()
	{
		resultList = getEntityManager().createQuery("SELECT e FROM ExamImagenologia e").getResultList();
	}
	
	public void buscarExamenPorNombre()
	{
		if(nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT e FROM ExamImagenologia e where UPPER(e.nombre) like :busqueda or UPPER(e.codigo) like :busqueda or UPPER(e.categoria) like :busqueda")
					.setParameter("busqueda", "%"+nomCoinci.toUpperCase()+"%")
					.getResultList();
		}
	}
	

	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
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

	public List<ExamImagenologia> getResultList() {
		return resultList;
	}

	public void setResultList(List<ExamImagenologia> resultList) {
		this.resultList = resultList;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}
	
	
	

}
