package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.ExamenOtoneurologia;

@Name("examenOtoneurologiaHome")
@Scope(ScopeType.CONVERSATION)
public class ExamenOtoneurologiaHome extends KubeDAO<ExamenOtoneurologia>{

	private Integer examId;
	private List<ExamenOtoneurologia> resultList = new ArrayList<ExamenOtoneurologia>();
	
	private String nomCoinci = "";
	
	@Override
	public void create() {
		
		setCreatedMessage(createValueExpression("El examen se ha registrado satisfactoriamente"));
		setUpdatedMessage(createValueExpression("El examen se ha modificado satisfactoriamente"));
		setDeletedMessage(createValueExpression("El examen ha sido eliminado"));
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ExamenOtoneurologia.class, examId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ExamenOtoneurologia());
		}
	}
	
	public void cargarExamenes()
	{
		resultList = getEntityManager().createQuery("SELECT e FROM ExamenOtoneurologia e").getResultList();
	}
	
	
	public void buscarExamenPorNombre()
	{
		if(nomCoinci!=null)
		{
			resultList = getEntityManager().createQuery("SELECT e FROM ExamenOtoneurologia e where UPPER(e.nombre) like :busqueda or UPPER(e.codigo) like :busqueda or UPPER(e.categoria) like :busqueda")
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

	public List<ExamenOtoneurologia> getResultList() {
		return resultList;
	}

	public void setResultList(List<ExamenOtoneurologia> resultList) {
		this.resultList = resultList;
	}

	public String getNomCoinci() {
		return nomCoinci;
	}

	public void setNomCoinci(String nomCoinci) {
		this.nomCoinci = nomCoinci;
	}
	
	
	
	

}
