package com.sa.kubekit.action.medical;

import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.sa.kubekit.action.i18n.KubeBundle;
import com.sa.kubekit.action.util.KubeSearcher;
import com.sa.kubekit.action.util.UtilString;
import com.sa.model.crm.Cliente;
import com.sa.model.medical.Doctor;

@Name("patientSearch")
@Role(name = "patientPlanSearch", scope = ScopeType.PAGE)
@Scope(ScopeType.CONVERSATION)
public class PatientSearch extends KubeSearcher<Cliente> {

	private String numId;
	public static String name;
	private String lastname;
	private String email;
	private String phone;
	private String address;

	private Doctor doctor;
	private UtilString utilString = new UtilString();
	

	@In(required = true)
	private KubeBundle sainv_messages;

	@SuppressWarnings("unchecked")
	@Override
	protected void searchImpl() 
	{
		String jpql = "select Distinct p from Cliente p ";
		boolean aux = true;
		System.out.println("cheurchin");
		
		if(doctor!=null){
				jpql+="inner join p.citasMedicas c ";
			if(aux){
				jpql += "where ";
				aux=false;
			}else{
				jpql += "and ";
			}
				jpql += "c.doctor.id = " + doctor.getId() + " ";
		}
		
	
		if (numId != null && !numId.isEmpty()) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
				jpql += "p.docId like '%" + numId + "%' ";
		}

		if (name != null && !name.isEmpty()) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
			    jpql += "UPPER (p.nombres) like UPPER ('%" + name + "%') or ";
			    jpql += "UPPER (p.apellidos ) like UPPER ( '%" + name + "%')";
			
				//jpql += "(" + utilString.addOrToQuery(name, "p", "nombres")+ ") ";
				//jpql += " or " + utilString.addOrToQuery(name, "p", "apellidos")+ ") ";
					
		}
			
		if (phone != null && !phone.isEmpty()) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
				jpql += "p.telefono1 like '%" + phone + "%' ";
		}
		
		if (address != null && !address.isEmpty()) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
				jpql += "p.docId like '%" + address + "%' ";
		}

		if (email != null && !email.isEmpty()) {
			if (aux) {
				jpql += "where ";
				aux = false;
			} else
				jpql += "and ";
				jpql += "p.email like '%" + email + "%' ";
		}
		
		
		System.out.println(numId +" "+ name +" "+ lastname +" "+ phone +" "+ email +" "+ address);
		jpql += " order by p.docId";
		System.out.println("Contenido de jpql: " +jpql);

		if((address==null || address.isEmpty()) && (numId==null || numId.isEmpty()) && (name==null || name.isEmpty()) && (phone==null || phone.isEmpty())){
			System.out.print("chuer");
			
		}
		else{
			try {
				System.out.println("********************* Consulta => " + jpql);
				setResultList(entityManager.createQuery(jpql).getResultList());
				
			} catch (Exception e) {
				System.out.print(e);
			}
			
		}
		if (getResultList() == null)
			FacesMessages.instance().add(sainv_messages.get("patientSearch_msg1"));
		
	}

	public void clean() {
		numId = "";
		name = "";
		email = "";
		//entity = null;
		setResultList(new ArrayList<Cliente>());
	}

	@Override
	protected boolean validateParams() {
		return true;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}