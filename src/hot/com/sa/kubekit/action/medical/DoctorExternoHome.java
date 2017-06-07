package com.sa.kubekit.action.medical;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.DoctorExterno;

@Name("doctorExternoHome")
@Scope(ScopeType.CONVERSATION)
public class DoctorExternoHome extends KubeDAO<DoctorExterno>{

	private static final long serialVersionUID = 1L;
	private Integer docExtId;
	private List<DoctorExterno> resultList = new ArrayList<DoctorExterno>();
	private String docCoinci = "";
	
	
	@In(required=false, create=true)
	ClienteHome clienteHome;
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("docext_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("docext_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("docext_deleted")));
		getDoctores();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(DoctorExterno.class, docExtId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new DoctorExterno());
			}
	}
	
	public void getDoctores() {
		resultList = getEntityManager().createQuery("SELECT d FROM DoctorExterno d ").getResultList();
	}
	
	 public List<Object[]> getDoctores(Object o) {
		 
		return getEntityManager().createQuery("SELECT d.nombres, d.apellidos, d.telefono1, d.email, d FROM DoctorExterno d " +
				"WHERE CONCAT(UPPER(TRIM(d.nombres)),' ',UPPER(TRIM(d.apellidos))) LIKE :nom ")
			.setParameter("nom","%"+o.toString().toUpperCase().trim()+"%")
	 		.getResultList();
		
		//.setParameter("ape", "%"+o.toString()+"%")
		
	} 

	@Override
	public boolean preSave() {
		
		if(instance.getNombres()==null)
		{
			
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre");
			return false;
		}
		else if(instance.getNombres().equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el nombre");
			return false;
		}
		
		if(instance.getApellidos()==null)
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el apellido");
			return false;
		}
		else if(instance.getApellidos().equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Ingrese el apellido");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean preModify() {
		
		return true;
	}

	@Override
	public boolean preDelete() {
		return false;
	}

	@Override
	public void posSave() {
		
		clienteHome.getInstance().setDoctorRef(instance);
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

	public List<DoctorExterno> getResultList() {
		return resultList;
	}

	public void setResultList(List<DoctorExterno> resultList) {
		this.resultList = resultList;
	}

	public Integer getDocExtId() {
		return docExtId;
	}

	public void setDocExtId(Integer docExtId) {
		this.docExtId = docExtId;
	}
	public String getDocCoinci() {
		return docCoinci;
	}

	public void setDocCoinci(String docCoinci) {
		this.docCoinci = docCoinci;
	}
}
