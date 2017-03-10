package com.sa.kubekit.action.vta;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.vta.EmpresaDoc;

@Name("empresaDocHome")
@Scope(ScopeType.CONVERSATION)
public class EmpresaDocHome extends KubeDAO<EmpresaDoc>{
	
	private static final long serialVersionUID = 1L;
	private Integer empdocId; 
	private List<EmpresaDoc> resultlist = new ArrayList<EmpresaDoc>();
	private EmpresaDoc empresaSelected;
	
	@In
	private LoginUser loginUser;	
	
	@Override
	public void create() { 
		setCreatedMessage(createValueExpression(sainv_messages
				.get("empresaDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("empresaDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("empresaDocHome_deleted")));
	}
		
	@SuppressWarnings("unchecked") 
	public void cargarListaEmpresas(){
		resultlist = getEntityManager().createQuery("select c from EmpresaDoc c ")
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(EmpresaDoc.class, empdocId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new EmpresaDoc());
			instance.setTipoContribuyente(1);
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
		return true;
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

	public List<EmpresaDoc> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<EmpresaDoc> resultlist) {
		this.resultlist = resultlist;
	}

	public Integer getEmpdocId() {
		return empdocId;
	}

	public void setEmpdocId(Integer empdocId) {
		this.empdocId = empdocId;
	}

	
	public EmpresaDoc getEmpresaSelected() {
		return empresaSelected;
	}

	public void setEmpresaSelected(EmpresaDoc empresaSelected) {
		this.empresaSelected = empresaSelected;
	}
	
	
	
}
