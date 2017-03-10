package com.sa.kubekit.action.sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.sales.CertDescuento;

@Name("certificadoDescHome")
@Scope(ScopeType.CONVERSATION)
public class CertificadoDescHome extends KubeDAO<CertDescuento>{

	private static final long serialVersionUID = 1L;
	private Integer certDescId;
	private List<CertDescuento> resultList = new ArrayList<CertDescuento>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("certdesc_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("certdesc_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("certdesc_deleted")));
	}
	
	@In
	private LoginUser loginUser;
	
	public void load(){
		try{
			setInstance(getEntityManager().find(CertDescuento.class, certDescId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new CertDescuento());
		}
	}
	
	public void getCertificadosList() {
		resultList = getEntityManager().createQuery("SELECT c FROM CertDescuento c").getResultList();
	}

	@Override
	public boolean preSave() {
		SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
		instance.setCodCert(instance.getCodCert()+df.format(new Date()));
		//Validamos que no exista otro certificado con el mismo codigo
		List<CertDescuento> existentes = getEntityManager().createQuery("SELECT c FROM CertDescuento c WHERE c.codCert = :cod ")
				.setParameter("cod", instance.getCodCert())
				.getResultList();
		if(existentes != null && existentes.size() > 0){
			FacesMessages.instance().add(
					sainv_messages.get("certdesc_error_codexist"));
			return false;
		}
		
		if(instance.getTiempoValidez() <= 0){
			FacesMessages.instance().add(
					sainv_messages.get("certdesc_error_tmpvalow"));
			return false;
		}
		
		if(instance.getValDescuento() <= 0){
			FacesMessages.instance().add(
					sainv_messages.get("certdesc_error_valdsclow"));
			return false;
		}
		
		instance.setEstado("ACT");
		instance.setFechaIngreso(new Date());
		instance.setEmpresa(loginUser.getUser().getSucursal().getEmpresa());
		
		return true;
	}

	@Override
	public boolean preModify() {
		if(instance.getTiempoValidez() <= 0){
			FacesMessages.instance().add(
					sainv_messages.get("certdesc_error_tmpvalow"));
			return false;
		}
		
		if(instance.getValDescuento() <= 0){
			FacesMessages.instance().add(
					sainv_messages.get("certdesc_error_valdsclow"));
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

	public Integer getCertDescId() {
		return certDescId;
	}

	public void setCertDescId(Integer certDescId) {
		this.certDescId = certDescId;
	}

	public List<CertDescuento> getResultList() {
		return resultList;
	}

	public void setResultList(List<CertDescuento> resultList) {
		this.resultList = resultList;
	}



}
