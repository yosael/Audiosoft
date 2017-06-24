package com.sa.kubekit.action.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.MedicalAppointment;
import com.sa.model.medical.MotivoConsulta;
import com.sa.model.medical.MotivoConsultaPaciente;



@Name("motivoConsultaHome")
@Scope(ScopeType.CONVERSATION)
public class MotivoConsultaHome extends KubeDAO<MotivoConsulta> implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Integer idMotivo;
	private List<MotivoConsulta> motivosConsulta = new ArrayList<MotivoConsulta>();
	private List<MotivoConsultaPaciente> motivosConsultaPA = new ArrayList<MotivoConsultaPaciente>();
	private List<MotivoConsultaPaciente> motivosUltimaConsultaPA = new ArrayList<MotivoConsultaPaciente>();
	
	private String busqueda;
	
	
	public void load()
	{
		motivosConsulta = getEntityManager().createQuery("SELECT m FROM MotivoConsulta m").getResultList();
	}
	
	
	public void cargarMotivo()
	{
		try {
			
			setInstance(getEntityManager().find(MotivoConsulta.class, idMotivo));
			
		} catch (Exception e) {
			
			clearInstance();
			instance = new MotivoConsulta();
			
		}
	}
	
	
	public void cargarMotivosUltimaConsulta(Integer idCliente)
	{
		motivosUltimaConsultaPA = getEntityManager().createQuery("SELECT m FROM MotivoConsultaPaciente m where m.consulta.id=(SELECT MAX(c.id) FROM MedicalAppointment c where c.cliente.id="+idCliente+" )").getResultList();
	}
	
	
	public void cargarMotivosConsultaPaciente()
	{
		
		
		
	}
	
	public void buscarMotivosConsulta()
	{
		
		if(busqueda!=null)
		{
			motivosConsulta = getEntityManager().createQuery("SELECT m FROM MotivoConsulta m where UPPER(m.descripcion) like :busqueda ").setParameter("busqueda", "%"+busqueda.toUpperCase()+"%").getResultList();
		}	
		
	}
	
	
	public void persistirMotivosLista()
	{
		try {
			
			for(MotivoConsultaPaciente motiv: motivosConsultaPA)
			{
				if(motiv.getId()==null)
				{
					getEntityManager().persist(motiv);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	
	public void agregarMotivoPaciente(MotivoConsulta motivo,MedicalAppointment medicalAp)
	{
		MotivoConsultaPaciente motivoPaciente = new MotivoConsultaPaciente();
		motivoPaciente.setMotivo(motivo);
		motivoPaciente.setConsulta(medicalAp);
		
		if(motivosConsultaPA.size()>0)
		{
			for(MotivoConsultaPaciente motiv: motivosConsultaPA)
			{
				if(motiv.getMotivo().getId()==motivo.getId())
				{
					FacesMessages.instance().add(Severity.WARN,"El motivo de la consulta ya esta agregado");
					return;
				}
			}
		}
		
		motivosConsultaPA.add(motivoPaciente);
	}
	
	
	
	
	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		
		if(instance.getDescripcion()==null || instance.getDescripcion().equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Agregar descripcion");
			return false;
		}
		
		instance.setDescripcion(instance.getDescripcion().toUpperCase());
		
		return true;
	}



	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		
		if(instance.getDescripcion()==null || instance.getDescripcion().equals(""))
		{
			FacesMessages.instance().add(Severity.WARN,"Agregar descripcion");
			return false;
		}
		
		
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
	
	
	
	
	public Integer getIdMotivo() {
		return idMotivo;
	}
	public void setIdMotivo(Integer idMotivo) {
		this.idMotivo = idMotivo;
	}
	public List<MotivoConsulta> getMotivosConsulta() {
		return motivosConsulta;
	}
	public void setMotivosConsulta(List<MotivoConsulta> motivosConsulta) {
		this.motivosConsulta = motivosConsulta;
	}
	public List<MotivoConsultaPaciente> getMotivosConsultaPA() {
		return motivosConsultaPA;
	}
	public void setMotivosConsultaPA(List<MotivoConsultaPaciente> motivosConsultaPA) {
		this.motivosConsultaPA = motivosConsultaPA;
	}


	public List<MotivoConsultaPaciente> getMotivosUltimaConsultaPA() {
		return motivosUltimaConsultaPA;
	}


	public void setMotivosUltimaConsultaPA(
			List<MotivoConsultaPaciente> motivosUltimaConsultaPA) {
		this.motivosUltimaConsultaPA = motivosUltimaConsultaPA;
	}


	public String getBusqueda() {
		return busqueda;
	}


	public void setBusqueda(String busqueda) {
		this.busqueda = busqueda;
	}
	
	
	

}
