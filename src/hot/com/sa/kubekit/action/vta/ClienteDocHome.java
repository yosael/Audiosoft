package com.sa.kubekit.action.vta;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.crm.Cliente;
import com.sa.model.vta.ClienteDoc;
import com.sa.model.vta.ComprobanteImpresion;

@Name("clienteDocHome")
@Scope(ScopeType.CONVERSATION) 
public class ClienteDocHome extends KubeDAO<ClienteDoc>{

	private static final long serialVersionUID = 1L;
	private Integer clienteDocId; 
	private List<ClienteDoc> resultlist = new ArrayList<ClienteDoc>();
	
	private ComprobanteImpresion comprobanteSele= new ComprobanteImpresion();
	private Boolean mostrarModFactura=false;
	private Boolean mostrarModCredito=false;
	
	
	@Override
	public void create() {
		setCreatedMessage(createValueExpression(sainv_messages
				.get("clienteDocHome_created")));
		setUpdatedMessage(createValueExpression(sainv_messages
				.get("clienteDocHome_updated")));
		setDeletedMessage(createValueExpression(sainv_messages
				.get("clienteDocHome_deleted")));
	}
		
	@SuppressWarnings("unchecked") 
	public void cargarListaClientes(){
		resultlist = getEntityManager().createQuery("select c from ClienteDoc c ")
				.getResultList();
	}
	
	public void load(){
		try{
			setInstance(getEntityManager().find(ClienteDoc.class, clienteDocId));
		}catch (Exception e) {
			clearInstance();
			setInstance(new ClienteDoc());
		}
	}
	
	
	
	//Como verificar si el cliente ya esta registrado. Necesito saber cual es el cliente
	public void verificarClienteDoc(Cliente clienteRegis,String tipoDoc)
	{

		ClienteDoc cliente= new ClienteDoc();
		try {
			
			System.out.println("Entro al try ***** ");
			cliente= (ClienteDoc) getEntityManager().createQuery("SELECT c FROM ClienteDoc c where c.idCliente=:idCliente").setParameter("idCliente", clienteRegis.getId()).getSingleResult();
			
			//Si es diferente de null. Existe. y si existe
			if(cliente==null)
			{
				
				System.out.println("El cliente ESS ess nulo *****");
				//registrar el cliente
				instance= new ClienteDoc();
				instance.setNombre(clienteRegis.getNombres());
				instance.setApellido(clienteRegis.getApellidos());
				instance.setDireccion(clienteRegis.getDireccion());
				instance.setDui(clienteRegis.getDocId());
				instance.setNit("");
				instance.setTelefono1(clienteRegis.getTelefono1());
				instance.setTelefono2(clienteRegis.getTelefono2()!=null?clienteRegis.getTelefono2():"");
				instance.setIdCliente(clienteRegis.getId());
			
				this.save();
			}
			else
			{
				System.out.println("EL cliente NOO nulo *******");
				setInstance(cliente); //Cargar la instancia en la informacion del formulario
				
			}
			
			
		} catch (Exception e) {
			System.out.println("Entro al catch ********");
			
			
			System.out.println("El cliente ESS ess nulo *****");
			//registrar el cliente
			instance= new ClienteDoc();
			instance.setNombre(clienteRegis.getNombres());
			instance.setApellido(clienteRegis.getApellidos());
			instance.setDireccion(clienteRegis.getDireccion());
			instance.setDui(clienteRegis.getDocId());
			instance.setNit("");
			instance.setTelefono1(clienteRegis.getTelefono1());
			instance.setTelefono2(clienteRegis.getTelefono2()!=null?clienteRegis.getTelefono2():"");
			instance.setIdCliente(clienteRegis.getId());
		
			getEntityManager().persist(instance);// Al parecer muestra el id Duplicado.
			getEntityManager().flush();
			
			
			// TODO: handle exception
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		}
		
		if(tipoDoc.equals("FAC"))
			mostrarModFactura=true;
		else if(tipoDoc.equals("CRE"))
			mostrarModCredito=true;
		
	}
	
	public void imprimirHola()
	{
		System.out.println("HOla mundo ********");
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

	public Integer getClienteDocId() {
		return clienteDocId;
	}

	public void setClienteDocId(Integer clienteDocId) {
		this.clienteDocId = clienteDocId;
	}

	public List<ClienteDoc> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<ClienteDoc> resultlist) {
		this.resultlist = resultlist;
	}

	public ComprobanteImpresion getComprobanteSele() {
		return comprobanteSele;
	}

	public void setComprobanteSele(ComprobanteImpresion comprobanteSele) {
		this.comprobanteSele = comprobanteSele;
	}

	public Boolean getMostrarModFactura() {
		return mostrarModFactura;
	}

	public void setMostrarModFactura(Boolean mostrarModFactura) {
		this.mostrarModFactura = mostrarModFactura;
	}

	public Boolean getMostrarModCredito() {
		return mostrarModCredito;
	}

	public void setMostrarModCredito(Boolean mostrarModCredito) {
		this.mostrarModCredito = mostrarModCredito;
	}
	
	
	
	
}
