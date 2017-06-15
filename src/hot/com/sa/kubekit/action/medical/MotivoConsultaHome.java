package com.sa.kubekit.action.medical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.sa.kubekit.action.util.KubeDAO;
import com.sa.model.medical.MotivoConsulta;
import com.sa.model.medical.MotivoConsultaPaciente;



@Name("motivoConsultaHome")
@Scope(ScopeType.CONVERSATION)
public class MotivoConsultaHome extends KubeDAO<MotivoConsulta> implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Integer idMotivo;
	private List<MotivoConsulta> motivosConsulta = new ArrayList<MotivoConsulta>();
	private List<MotivoConsultaPaciente> motivosConsultaPA = new ArrayList<MotivoConsultaPaciente>();
	
	
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
		motivosConsultaPA = getEntityManager().createQuery("SELECT m FROM MotivoConsultaPaciente m where m.consulta.id=(SELECT MAX(c.id) FROM MedicalAppointment c where c.cliente.id="+idCliente+" )").getResultList();
	}
	
	
	public void cargarMotivosConsultaPaciente()
	{
		
	}
	
	
	@Override
	public boolean preSave() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean preModify() {
		// TODO Auto-generated method stub
		return false;
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


}
