package com.sa.kubekit.action.vta;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.vta.ComprobanteImpresion;
import com.sa.model.vta.EmpresaDoc;

@Name("comprobanteImpresionHome") 
@Scope(ScopeType.CONVERSATION) 
public class ComprobanteImpresionHome extends KubeDAO<ComprobanteImpresion>{
	private static final long serialVersionUID = 1L;
	private Integer comprobanteImpresionId; 
	private List<ComprobanteImpresion> resultlist = new ArrayList<ComprobanteImpresion>();
	
	@In
	private LoginUser loginUser;	
	 
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("comprobanteImpresionHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("comprobanteImpresionHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("comprobanteImpresionHome_deleted")));
	}
		
	@SuppressWarnings("unchecked") 
	public void cargarListaComprobantes(){
		System.out.println("Entro a lista comprobantes");
		resultlist = getEntityManager().createQuery("select c from ComprobanteImpresion c ")
				.getResultList();
		
		setInstance(new ComprobanteImpresion());
	}
	 
	/*Obteniendo solo los comprobantes del usuario activo*/
	@SuppressWarnings("unchecked") 
	public void cargarListaComprobantesUsuario(){
		System.out.println("Usuario: " + loginUser.getUser().getId() );
		resultlist = getEntityManager().createQuery("select c.comprobante from ComprobanteUsuarioDoc c" +
				" WHERE c.usuario.id = :P1")
				.setParameter("P1", loginUser.getUser().getId() )
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ComprobanteImpresion.class, comprobanteImpresionId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ComprobanteImpresion());
		}
	}
	
	@SuppressWarnings("unchecked") 
	public void cargarListaComprobantes(EmpresaDoc empresa,String tipo){
		
		resultlist = getEntityManager().createQuery("select c from ComprobanteImpresion c where c.empresaDoc.id=:idEmpresa and c.tipo=:tipo").setParameter("idEmpresa", empresa.getId()).setParameter("tipo",tipo).getResultList();
	}
	
	@Override
	public boolean preSave() {
		System.out.println("Guardando");
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

	public Integer getComprobanteImpresionId() {
		return comprobanteImpresionId;
	}

	public void setComprobanteImpresionId(Integer comprobanteImpresionId) {
		this.comprobanteImpresionId = comprobanteImpresionId;
	}

	public List<ComprobanteImpresion> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<ComprobanteImpresion> resultlist) {
		this.resultlist = resultlist;
	}

	public LoginUser getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(LoginUser loginUser) {
		this.loginUser = loginUser;
	}
	
	
}
