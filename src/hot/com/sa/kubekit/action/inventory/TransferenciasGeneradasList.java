package com.sa.kubekit.action.inventory;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.security.LoginUser;
import com.sa.kubekit.action.util.KubeQuery;
import com.sa.model.inventory.Transferencia;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
//Lista de transferencias de un solo usuario
@Name("transferenciasGeneradasList")
@Scope(ScopeType.CONVERSATION)//Antes era conversation
public class TransferenciasGeneradasList extends KubeQuery<Transferencia>{

	@In
	private LoginUser loginUser;
		
	@Create
	public void init() {
		//load();
	}
	
	/*public void load(){
		System.out.print("aqui0");
		if(loginUser.getUser().getSucursal()!=null){
			System.out.print("aqui1");	
			setJpql("select t from Transferencia t where " +
					"t.usuarioGenera.id = " + loginUser.getUser().getId() + 
					"AND t.estado != 'A' order by t.fecha desc");
		}else{
			System.out.print("aqui2");
			setJpql("select t from Transferencia t order by t.fecha desc");
		}
	}
	
	public void refresh()
	{
		
		List<Transferencia> lista2=super.entityManager.createQuery("SELECT t FROM Transferencia t where t.usuarioGenera.id=:user and t.estado != 'A' order by t.fecha desc")
										.setParameter("user", loginUser.getUser().getId()).getResultList();
		
		super.setResultList(lista2);
	}*/
}
