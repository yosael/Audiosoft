package com.sa.kubekit.action.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.medical.PatientSearch;
import com.sa.kubekit.action.util.EmailService;
import com.sa.model.crm.Cliente;
import com.sa.model.medical.Doctor;

@Name("sendMessage")
@Scope(ScopeType.CONVERSATION)
public class SendMessage {

	@In(required=false, create=true)
	private PatientSearch patientSearch;
	
	@In
	private EmailService emailService;
	
	@In(required = true)
	protected KubeBundle sainv_messages;
	
	private List<Cliente> clientes = new ArrayList<Cliente>();
	private Doctor doctorSelected;
	private boolean check;
	private String text;
	
	@Begin(join=true)
	@Create
	public void init(){
	}
	
	public String send(){
		Map<String, Object> map = new HashMap<String, Object>();
		for(Cliente cliente :clientes){
			map.put("info", cliente);
			map.put("msg", this);
			emailService.sendMessage(10000, "/crm/email/msgcrm.xhtml", map);
		}
		System.out.println("ENVIO EL MENSAJE");
		FacesMessages.instance().add(sainv_messages
				.get("msg_created_msg"));
		return "sent";
	}
	
	public void assignCustomers(){
		try{
			for(Cliente cliente : patientSearch.getResultList()){
				if(cliente.isSelected() && !clientes.contains(cliente)){
					clientes.add(cliente);
				}
			}
		}catch (Exception e) {
		}
		
	}
	
	public void searchCustomers(){
		patientSearch.search();
		for(Cliente cliente : patientSearch.getResultList()){
			if(clientes.contains(cliente)){
				cliente.setSelected(true);
			}
		}
	}
	
	public void removeCustomer(Cliente cliente){
		cliente.setSelected(false);
		this.clientes.remove(cliente);
	}
	
	public void changeAll(){
		for(Cliente cliente : clientes){
			cliente.setSelected(check);
		}
	}
	
	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Doctor getDoctorSelected() {
		return doctorSelected;
	}

	public void setDoctorSelected(Doctor doctorSelected) {
		this.doctorSelected = doctorSelected;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
