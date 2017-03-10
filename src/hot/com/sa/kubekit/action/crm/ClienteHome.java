package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Cliente;

@Name("clienteCRMHome")
@Scope(ScopeType.CONVERSATION)
public class ClienteHome extends KubeDAO<Cliente> {

	private static final long serialVersionUID = 1L;
	private Integer cliId;

	private List<Cliente> clientes = new ArrayList<Cliente>();
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("clienteHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("clienteHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("clienteHome_deleted")));
	}
		
	@SuppressWarnings("unchecked")
	public void cargarListaClientes(){
		try {
			clientes = getEntityManager().createQuery("select  c from Cliente c where id < 2000 ").getResultList();
			clientes.addAll(getEntityManager().createQuery("select  c from Cliente c where id >=2000").getResultList());
		} catch (Exception e) {
			System.out.print(e);
		}
		


	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(Cliente.class, cliId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new Cliente());
		}
	}
	
	/*public void cargarPaciente(Cliente cl)
	{
		System.out.println("Entro a metodo cargar paciente *******");
		//setInstance(cl);
	}*/

	@Override
	public boolean preSave() {
		System.out.println("Estoy en presave e isModificable = "+ instance.getMdif().isModificable() + " medioReferido: "+ instance.getMedioReferido());
		if (instance.getMdif().isModificable()==false && !instance.getMedioReferido().isEmpty()){
			instance.setMedioReferido("");
			return true;
		}
		return false;
	}

	@Override
	public boolean preModify() {
		System.out.println("Estoy en premodify e isModificable = "+ instance.getMdif().isModificable() + " medioReferido: "+ instance.getMedioReferido());
		if (instance.getMdif().isModificable()==false && !instance.getMedioReferido().isEmpty()){
			instance.setMedioReferido("");
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preDelete() {
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

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Integer getCliId() {
		return cliId;
	}

	public void setCliId(Integer cliId) {
		this.cliId = cliId;
	}
	
	
}
